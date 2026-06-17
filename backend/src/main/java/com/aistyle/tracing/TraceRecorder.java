package com.aistyle.tracing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aistyle.config.LangSmithConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TraceRecorder {

    private final ConcurrentHashMap<String, TraceMetadata> traces = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    @Autowired
    private LangSmithConfig langSmithConfig;

    public TraceRecorder() {
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    }

    public String recordTrace(String operationName, long startTime, long endTime,
                             int inputTokens, int outputTokens, String modelUsed) {
        String traceId = UUID.randomUUID().toString();

        TraceMetadata trace = TraceMetadata.builder()
            .traceId(traceId)
            .operationName(operationName)
            .timestamp(Instant.now())
            .startTimeMs(startTime)
            .endTimeMs(endTime)
            .totalLatencyMs(endTime - startTime)
            .inputTokens(inputTokens)
            .outputTokens(outputTokens)
            .totalTokens(inputTokens + outputTokens)
            .modelUsed(modelUsed)
            .status("success")
            .build();

        trace.calculateTokenCost();
        traces.put(traceId, trace);

        // Send to LangSmith
        sendToLangSmith(trace);
        log.info("Trace recorded and sent to LangSmith: {}", trace);

        return traceId;
    }

    public String recordTraceError(String operationName, long startTime, long endTime,
                                   String errorMessage, int inputTokens, String modelUsed) {
        String traceId = UUID.randomUUID().toString();

        TraceMetadata trace = TraceMetadata.builder()
            .traceId(traceId)
            .operationName(operationName)
            .timestamp(Instant.now())
            .startTimeMs(startTime)
            .endTimeMs(endTime)
            .totalLatencyMs(endTime - startTime)
            .inputTokens(inputTokens)
            .outputTokens(0)
            .totalTokens(inputTokens)
            .modelUsed(modelUsed)
            .status("error")
            .errorMessage(errorMessage)
            .build();

        traces.put(traceId, trace);

        // Send to LangSmith
        sendToLangSmith(trace);
        log.error("Error trace recorded and sent to LangSmith: {}", trace);

        return traceId;
    }

    private void sendToLangSmith(TraceMetadata trace) {
        if (!langSmithConfig.isEnabled()) {
            log.debug("LangSmith is disabled, skipping trace transmission");
            return;
        }

        try {
            // Format trace for LangSmith API
            Map<String, Object> langSmithTrace = formatForLangSmith(trace);
            String jsonBody = objectMapper.writeValueAsString(langSmithTrace);

            String endpoint = langSmithConfig.getEndpoint();
            String apiKey = langSmithConfig.getApiKey();
            String project = langSmithConfig.getProject();

            // Debug: log the request details
            log.info("=== LangSmith Trace Debug ===");
            log.info("Endpoint: {}", endpoint);
            log.info("Project: {}", project);
            log.info("API Key (first 20 chars): {}...", apiKey.substring(0, Math.min(20, apiKey.length())));
            log.info("Trace ID: {}", trace.getTraceId());
            log.info("Operation: {}", trace.getOperationName());
            log.info("Request Body: {}", jsonBody.substring(0, Math.min(200, jsonBody.length())) + "...");
            log.info("============================");

            // Send to LangSmith runs endpoint
            String url = endpoint.replaceAll("/$", "") + "/api/v1/runs";

            log.info("Sending to URL: {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send trace to LangSmith: {}", exception.getMessage(), exception);
                    } else {
                        log.info("LangSmith API Response - Status: {}, Body: {}", response.statusCode(), response.body());
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            log.info("✓ Trace successfully sent to LangSmith project: {}", project);
                        } else {
                            log.warn("⚠ LangSmith API returned status {}: {}", response.statusCode(), response.body());
                        }
                    }
                });

        } catch (Exception e) {
            log.error("Error formatting or sending trace to LangSmith: {}", e.getMessage(), e);
        }
    }

    private Map<String, Object> formatForLangSmith(TraceMetadata trace) {
        Map<String, Object> langSmithRun = new LinkedHashMap<>();

        // Convert timestamps to ISO format (required by LangSmith)
        java.time.Instant startInstant = java.time.Instant.ofEpochMilli(trace.getStartTimeMs());
        java.time.Instant endInstant = java.time.Instant.ofEpochMilli(trace.getEndTimeMs());

        // Required fields for LangSmith API
        langSmithRun.put("id", trace.getTraceId());
        langSmithRun.put("name", trace.getOperationName());
        langSmithRun.put("run_type", "llm");
        langSmithRun.put("session_name", langSmithConfig.getProject()); // IMPORTANT: must be session_name, not project_name
        langSmithRun.put("start_time", startInstant.toString());
        langSmithRun.put("end_time", endInstant.toString());

        // Status
        langSmithRun.put("status", "success".equals(trace.getStatus()) ? "completed" : "error");
        if ("error".equals(trace.getStatus())) {
            langSmithRun.put("error", trace.getErrorMessage());
        }

        // Inputs (required)
        Map<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("model", trace.getModelUsed());
        inputs.put("tokens", trace.getInputTokens());
        langSmithRun.put("inputs", inputs);

        // Outputs
        Map<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("tokens", trace.getOutputTokens());
        langSmithRun.put("outputs", outputs);

        // Extra metadata
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("input_tokens", trace.getInputTokens());
        metadata.put("output_tokens", trace.getOutputTokens());
        metadata.put("total_tokens", trace.getTotalTokens());
        metadata.put("estimated_cost_usd", String.format("%.6f", trace.getEstimatedCostUsd()));
        metadata.put("latency_ms", trace.getTotalLatencyMs());
        metadata.put("model_used", trace.getModelUsed());

        langSmithRun.put("extra", metadata);

        return langSmithRun;
    }

    public Map<String, Object> generateMetricsSummary() {
        if (traces.isEmpty()) {
            log.warn("No traces to summarize");
            return Map.of("message", "No traces available");
        }

        Map<String, Object> summary = new LinkedHashMap<>();

        // Total Statistics
        int totalTraces = traces.size();
        int failedTraces = (int) traces.values().stream()
            .filter(t -> "error".equals(t.getStatus()))
            .count();

        summary.put("total_traces", totalTraces);
        summary.put("successful_traces", totalTraces - failedTraces);
        summary.put("failed_traces", failedTraces);
        summary.put("success_rate", String.format("%.2f%%", ((totalTraces - failedTraces) / (double) totalTraces) * 100));

        // Latency Analysis
        long totalLatency = traces.values().stream()
            .mapToLong(TraceMetadata::getTotalLatencyMs)
            .sum();
        long maxLatency = traces.values().stream()
            .mapToLong(TraceMetadata::getTotalLatencyMs)
            .max()
            .orElse(0);
        long minLatency = traces.values().stream()
            .mapToLong(TraceMetadata::getTotalLatencyMs)
            .min()
            .orElse(0);
        double avgLatency = totalLatency / (double) totalTraces;

        Map<String, Object> latencyStats = new LinkedHashMap<>();
        latencyStats.put("total_ms", totalLatency);
        latencyStats.put("avg_ms", String.format("%.2f", avgLatency));
        latencyStats.put("max_ms", maxLatency);
        latencyStats.put("min_ms", minLatency);
        summary.put("latency_stats", latencyStats);

        // Token Analysis
        long totalInputTokens = traces.values().stream()
            .mapToLong(TraceMetadata::getInputTokens)
            .sum();
        long totalOutputTokens = traces.values().stream()
            .mapToLong(TraceMetadata::getOutputTokens)
            .sum();
        long totalTokens = totalInputTokens + totalOutputTokens;

        Map<String, Object> tokenStats = new LinkedHashMap<>();
        tokenStats.put("total_input_tokens", totalInputTokens);
        tokenStats.put("total_output_tokens", totalOutputTokens);
        tokenStats.put("total_tokens", totalTokens);
        tokenStats.put("avg_tokens_per_request", totalTokens / totalTraces);
        summary.put("token_stats", tokenStats);

        // Cost Analysis
        double totalCost = traces.values().stream()
            .mapToDouble(TraceMetadata::getEstimatedCostUsd)
            .sum();

        Map<String, Object> costStats = new LinkedHashMap<>();
        costStats.put("total_cost_usd", String.format("%.6f", totalCost));
        costStats.put("avg_cost_per_request_usd", String.format("%.6f", totalCost / totalTraces));
        costStats.put("cost_per_1k_tokens_usd", String.format("%.6f", (totalCost / totalTokens) * 1000));
        summary.put("cost_stats", costStats);

        // Operation Breakdown
        Map<String, Map<String, Object>> operationStats = new LinkedHashMap<>();
        traces.values().stream()
            .collect(java.util.stream.Collectors.groupingBy(TraceMetadata::getOperationName))
            .forEach((op, opTraces) -> {
                Map<String, Object> opStats = new LinkedHashMap<>();
                opStats.put("count", opTraces.size());
                opStats.put("avg_latency_ms", String.format("%.2f",
                    opTraces.stream().mapToLong(TraceMetadata::getTotalLatencyMs).average().orElse(0)));
                opStats.put("total_tokens", opTraces.stream().mapToLong(TraceMetadata::getTotalTokens).sum());
                opStats.put("total_cost_usd", String.format("%.6f",
                    opTraces.stream().mapToDouble(TraceMetadata::getEstimatedCostUsd).sum()));
                operationStats.put(op, opStats);
            });
        summary.put("operation_breakdown", operationStats);

        // Most Expensive Run
        Optional<TraceMetadata> mostExpensive = traces.values().stream()
            .max(Comparator.comparingDouble(TraceMetadata::getEstimatedCostUsd));
        if (mostExpensive.isPresent()) {
            TraceMetadata expensive = mostExpensive.get();
            Map<String, Object> expensiveStats = new LinkedHashMap<>();
            expensiveStats.put("trace_id", expensive.getTraceId());
            expensiveStats.put("operation", expensive.getOperationName());
            expensiveStats.put("input_tokens", expensive.getInputTokens());
            expensiveStats.put("output_tokens", expensive.getOutputTokens());
            expensiveStats.put("total_tokens", expensive.getTotalTokens());
            expensiveStats.put("cost_usd", String.format("%.6f", expensive.getEstimatedCostUsd()));
            summary.put("most_expensive_run", expensiveStats);
        }

        // Longest Trace
        Optional<TraceMetadata> longest = traces.values().stream()
            .max(Comparator.comparingLong(TraceMetadata::getTotalLatencyMs));
        if (longest.isPresent()) {
            TraceMetadata longestTrace = longest.get();
            Map<String, Object> longestStats = new LinkedHashMap<>();
            longestStats.put("trace_id", longestTrace.getTraceId());
            longestStats.put("operation", longestTrace.getOperationName());
            longestStats.put("latency_ms", longestTrace.getTotalLatencyMs());
            longestStats.put("input_tokens", longestTrace.getInputTokens());
            longestStats.put("output_tokens", longestTrace.getOutputTokens());
            summary.put("longest_trace", longestStats);
        }

        // Failed Operations
        List<Map<String, Object>> failedOps = new ArrayList<>();
        traces.values().stream()
            .filter(t -> "error".equals(t.getStatus()))
            .forEach(t -> {
                Map<String, Object> failedOp = new LinkedHashMap<>();
                failedOp.put("trace_id", t.getTraceId());
                failedOp.put("operation", t.getOperationName());
                failedOp.put("error_message", t.getErrorMessage());
                failedOps.add(failedOp);
            });
        if (!failedOps.isEmpty()) {
            summary.put("failed_operations", failedOps);
        }

        log.info("Metrics summary generated");
        return summary;
    }

    public TraceMetadata getTrace(String traceId) {
        return traces.get(traceId);
    }

    public Collection<TraceMetadata> getAllTraces() {
        return traces.values();
    }

    public List<TraceMetadata> getFailedTraces() {
        return traces.values().stream()
            .filter(t -> "error".equals(t.getStatus()))
            .sorted(Comparator.comparing(TraceMetadata::getTimestamp).reversed())
            .toList();
    }

    public List<TraceMetadata> getTracesByOperation(String operation) {
        return traces.values().stream()
            .filter(t -> operation.equals(t.getOperationName()))
            .sorted(Comparator.comparing(TraceMetadata::getTimestamp).reversed())
            .toList();
    }

    public void clearTraces() {
        traces.clear();
        log.info("All traces cleared");
    }

    public int getTraceCount() {
        return traces.size();
    }
}
