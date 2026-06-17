package com.aistyle.controller;

import com.aistyle.tracing.TraceMetadata;
import com.aistyle.tracing.TraceRecorder;
import com.aistyle.tracing.ProblematicRunDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/traces")
public class TraceController {

    @Autowired
    private TraceRecorder traceRecorder;

    @Autowired
    private ProblematicRunDetector problematicRunDetector;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getTracesSummary() {
        Collection<TraceMetadata> traces = traceRecorder.getAllTraces();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total_traces", traces.size());
        summary.put("successful_traces", traces.stream()
            .filter(t -> "success".equals(t.getStatus()))
            .count());
        summary.put("failed_traces", traces.stream()
            .filter(t -> "error".equals(t.getStatus()))
            .count());

        long totalTokens = traces.stream()
            .mapToLong(TraceMetadata::getTotalTokens)
            .sum();
        summary.put("total_tokens", totalTokens);

        double totalCost = traces.stream()
            .mapToDouble(TraceMetadata::getEstimatedCostUsd)
            .sum();
        summary.put("total_cost_usd", String.format("%.6f", totalCost));

        long totalLatency = traces.stream()
            .mapToLong(TraceMetadata::getTotalLatencyMs)
            .sum();
        summary.put("total_latency_ms", totalLatency);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TraceMetadata>> getAllTraces() {
        List<TraceMetadata> traces = new ArrayList<>(traceRecorder.getAllTraces());
        traces.sort(Comparator.comparing(TraceMetadata::getTimestamp).reversed());
        return ResponseEntity.ok(traces);
    }

    @GetMapping("/{traceId}")
    public ResponseEntity<TraceMetadata> getTrace(@PathVariable String traceId) {
        TraceMetadata trace = traceRecorder.getTrace(traceId);
        if (trace == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(trace);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<TraceMetadata>> getFailedTraces() {
        return ResponseEntity.ok(traceRecorder.getFailedTraces());
    }

    @GetMapping("/operation/{operationName}")
    public ResponseEntity<List<TraceMetadata>> getTracesByOperation(@PathVariable String operationName) {
        return ResponseEntity.ok(traceRecorder.getTracesByOperation(operationName));
    }

    @GetMapping("/most-expensive")
    public ResponseEntity<Map<String, Object>> getMostExpensiveRun() {
        Collection<TraceMetadata> traces = traceRecorder.getAllTraces();
        Optional<TraceMetadata> mostExpensive = traces.stream()
            .max(Comparator.comparingDouble(TraceMetadata::getEstimatedCostUsd));

        if (mostExpensive.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No traces found"));
        }

        TraceMetadata trace = mostExpensive.get();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("trace_id", trace.getTraceId());
        result.put("operation_name", trace.getOperationName());
        result.put("input_tokens", trace.getInputTokens());
        result.put("output_tokens", trace.getOutputTokens());
        result.put("total_tokens", trace.getTotalTokens());
        result.put("estimated_cost_usd", String.format("%.6f", trace.getEstimatedCostUsd()));
        result.put("model_used", trace.getModelUsed());
        result.put("latency_ms", trace.getTotalLatencyMs());
        result.put("timestamp", trace.getTimestamp());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/longest-execution")
    public ResponseEntity<Map<String, Object>> getLongestExecution() {
        Collection<TraceMetadata> traces = traceRecorder.getAllTraces();
        Optional<TraceMetadata> longest = traces.stream()
            .max(Comparator.comparingLong(TraceMetadata::getTotalLatencyMs));

        if (longest.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No traces found"));
        }

        TraceMetadata trace = longest.get();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("trace_id", trace.getTraceId());
        result.put("operation_name", trace.getOperationName());
        result.put("total_latency_ms", trace.getTotalLatencyMs());
        result.put("input_tokens", trace.getInputTokens());
        result.put("output_tokens", trace.getOutputTokens());
        result.put("total_tokens", trace.getTotalTokens());
        result.put("model_used", trace.getModelUsed());
        result.put("estimated_cost_usd", String.format("%.6f", trace.getEstimatedCostUsd()));
        result.put("timestamp", trace.getTimestamp());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/latency-analysis")
    public ResponseEntity<Map<String, Object>> getLatencyAnalysis() {
        Collection<TraceMetadata> traces = traceRecorder.getAllTraces();

        if (traces.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No traces found"));
        }

        long totalLatency = traces.stream()
            .mapToLong(TraceMetadata::getTotalLatencyMs)
            .sum();

        long maxLatency = traces.stream()
            .mapToLong(TraceMetadata::getTotalLatencyMs)
            .max()
            .orElse(0);

        long minLatency = traces.stream()
            .mapToLong(TraceMetadata::getTotalLatencyMs)
            .min()
            .orElse(0);

        double avgLatency = totalLatency / (double) traces.size();

        // Find slowest operation type
        Map<String, Long> operationLatencies = new HashMap<>();
        traces.forEach(t -> {
            String op = t.getOperationName();
            operationLatencies.put(op, operationLatencies.getOrDefault(op, 0L) + t.getTotalLatencyMs());
        });

        String slowestOperation = operationLatencies.entrySet().stream()
            .max(Comparator.comparingLong(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse("N/A");

        long slowestOperationLatency = operationLatencies.getOrDefault(slowestOperation, 0L);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_latency_ms", totalLatency);
        result.put("avg_latency_ms", String.format("%.2f", avgLatency));
        result.put("max_latency_ms", maxLatency);
        result.put("min_latency_ms", minLatency);
        result.put("slowest_operation", slowestOperation);
        result.put("slowest_operation_latency_ms", slowestOperationLatency);
        result.put("slowest_operation_percentage", String.format("%.2f%%",
            (slowestOperationLatency / (double) totalLatency) * 100));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/cost-analysis")
    public ResponseEntity<Map<String, Object>> getCostAnalysis() {
        Collection<TraceMetadata> traces = traceRecorder.getAllTraces();

        if (traces.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No traces found"));
        }

        long totalInputTokens = traces.stream()
            .mapToLong(TraceMetadata::getInputTokens)
            .sum();

        long totalOutputTokens = traces.stream()
            .mapToLong(TraceMetadata::getOutputTokens)
            .sum();

        long totalTokens = totalInputTokens + totalOutputTokens;

        double totalCost = traces.stream()
            .mapToDouble(TraceMetadata::getEstimatedCostUsd)
            .sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_tokens", totalTokens);
        result.put("total_input_tokens", totalInputTokens);
        result.put("total_output_tokens", totalOutputTokens);
        result.put("total_cost_usd", String.format("%.6f", totalCost));
        result.put("avg_cost_per_request_usd", String.format("%.6f", totalCost / traces.size()));
        result.put("cost_per_1k_tokens_usd", String.format("%.6f",
            totalTokens > 0 ? (totalCost / totalTokens) * 1000 : 0));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> generateReport() {
        Map<String, Object> report = traceRecorder.generateMetricsSummary();
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearTraces() {
        traceRecorder.clearTraces();
        return ResponseEntity.ok("All traces cleared");
    }

    @GetMapping("/trace-count")
    public ResponseEntity<Map<String, Integer>> getTraceCount() {
        return ResponseEntity.ok(Map.of("trace_count", traceRecorder.getTraceCount()));
    }

    @PostMapping("/analyze-problematic-runs")
    public ResponseEntity<Map<String, Object>> analyzeProblematicRuns() {
        Collection<TraceMetadata> allTraces = traceRecorder.getAllTraces();
        problematicRunDetector.analyzeTraces(new ArrayList<>(allTraces));

        List<ProblematicRunDetector.ProblematicRun> problematicRuns = problematicRunDetector.getProblematicRuns();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_problematic_runs", problematicRuns.size());
        result.put("problematic_runs", problematicRuns);

        // Breakdown by severity
        Map<String, Integer> severityBreakdown = new LinkedHashMap<>();
        severityBreakdown.put("HIGH", problematicRunDetector.getProblematicRunsBySeverity("HIGH").size());
        severityBreakdown.put("MEDIUM", problematicRunDetector.getProblematicRunsBySeverity("MEDIUM").size());
        severityBreakdown.put("LOW", problematicRunDetector.getProblematicRunsBySeverity("LOW").size());
        result.put("severity_breakdown", severityBreakdown);

        // Breakdown by category
        Map<String, Integer> categoryBreakdown = new LinkedHashMap<>();
        problematicRunDetector.getProblematicRuns().stream()
            .map(ProblematicRunDetector.ProblematicRun::getCategory)
            .distinct()
            .forEach(cat -> categoryBreakdown.put(cat,
                problematicRunDetector.getProblematicRunsByCategory(cat).size()));
        result.put("category_breakdown", categoryBreakdown);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/problematic-runs")
    public ResponseEntity<List<ProblematicRunDetector.ProblematicRun>> getProblematicRuns() {
        List<ProblematicRunDetector.ProblematicRun> runs = problematicRunDetector.getProblematicRuns();
        if (runs.isEmpty()) {
            // Analyze first if not done
            Collection<TraceMetadata> allTraces = traceRecorder.getAllTraces();
            problematicRunDetector.analyzeTraces(new ArrayList<>(allTraces));
            runs = problematicRunDetector.getProblematicRuns();
        }
        return ResponseEntity.ok(runs);
    }

    @GetMapping("/problematic-runs/severity/{severity}")
    public ResponseEntity<List<ProblematicRunDetector.ProblematicRun>> getProblematicRunsBySeverity(
            @PathVariable String severity) {
        return ResponseEntity.ok(problematicRunDetector.getProblematicRunsBySeverity(severity));
    }

    @GetMapping("/problematic-runs/category/{category}")
    public ResponseEntity<List<ProblematicRunDetector.ProblematicRun>> getProblematicRunsByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(problematicRunDetector.getProblematicRunsByCategory(category));
    }
}
