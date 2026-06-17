package com.aistyle.tracing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
public class ProblematicRunDetector {

    private List<ProblematicRun> problematicRuns = new ArrayList<>();

    public void analyzeTraces(List<TraceMetadata> traces) {
        problematicRuns.clear();

        for (TraceMetadata trace : traces) {
            // Rule 1: Failed traces
            if ("error".equals(trace.getStatus())) {
                problematicRuns.add(ProblematicRun.builder()
                    .traceId(trace.getTraceId())
                    .operation(trace.getOperationName())
                    .category("FAILED_EXECUTION")
                    .issue("Execution failed with error")
                    .errorMessage(trace.getErrorMessage())
                    .severity("HIGH")
                    .timestamp(trace.getTimestamp())
                    .build());
            }

            // Rule 2: Excessive tokens (hallucination indicator)
            if (trace.getOutputTokens() > 2000) {
                problematicRuns.add(ProblematicRun.builder()
                    .traceId(trace.getTraceId())
                    .operation(trace.getOperationName())
                    .category("EXCESSIVE_OUTPUT")
                    .issue("Output tokens exceed 2000 (possible hallucination/verbose response)")
                    .outputTokens(trace.getOutputTokens())
                    .severity("MEDIUM")
                    .timestamp(trace.getTimestamp())
                    .build());
            }

            // Rule 3: Very short output (incomplete response)
            if (trace.getOutputTokens() < 20 && trace.getStatus().equals("success")) {
                problematicRuns.add(ProblematicRun.builder()
                    .traceId(trace.getTraceId())
                    .operation(trace.getOperationName())
                    .category("INCOMPLETE_RESPONSE")
                    .issue("Output tokens < 20 (possible incomplete response)")
                    .outputTokens(trace.getOutputTokens())
                    .severity("MEDIUM")
                    .timestamp(trace.getTimestamp())
                    .build());
            }

            // Rule 4: Excessive latency
            if (trace.getTotalLatencyMs() > 5000) {
                problematicRuns.add(ProblematicRun.builder()
                    .traceId(trace.getTraceId())
                    .operation(trace.getOperationName())
                    .category("EXCESSIVE_LATENCY")
                    .issue("Execution latency exceeds 5 seconds")
                    .latencyMs(trace.getTotalLatencyMs())
                    .severity("LOW")
                    .timestamp(trace.getTimestamp())
                    .build());
            }

            // Rule 5: High cost
            if (trace.getEstimatedCostUsd() > 0.01) {
                problematicRuns.add(ProblematicRun.builder()
                    .traceId(trace.getTraceId())
                    .operation(trace.getOperationName())
                    .category("HIGH_COST")
                    .issue("Execution cost exceeds $0.01")
                    .costUsd(trace.getEstimatedCostUsd())
                    .severity("LOW")
                    .timestamp(trace.getTimestamp())
                    .build());
            }
        }

        log.info("Detected {} problematic runs", problematicRuns.size());
    }

    public List<ProblematicRun> getProblematicRuns() {
        return new ArrayList<>(problematicRuns);
    }

    public List<ProblematicRun> getProblematicRunsBySeverity(String severity) {
        return problematicRuns.stream()
            .filter(r -> severity.equals(r.getSeverity()))
            .toList();
    }

    public List<ProblematicRun> getProblematicRunsByCategory(String category) {
        return problematicRuns.stream()
            .filter(r -> category.equals(r.getCategory()))
            .toList();
    }

    public int getProblematicRunCount() {
        return problematicRuns.size();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblematicRun {
        private String traceId;
        private String operation;
        private String category;
        private String issue;
        private String errorMessage;
        private Integer outputTokens;
        private Long latencyMs;
        private Double costUsd;
        private String severity; // HIGH, MEDIUM, LOW
        private Object timestamp;

        @Override
        public String toString() {
            return String.format(
                "ProblematicRun{id='%s', operation='%s', category='%s', issue='%s', severity='%s'}",
                traceId, operation, category, issue, severity
            );
        }
    }
}
