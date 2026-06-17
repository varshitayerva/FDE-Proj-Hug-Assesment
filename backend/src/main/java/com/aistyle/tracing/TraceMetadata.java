package com.aistyle.tracing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceMetadata {

    @JsonProperty("trace_id")
    private String traceId;

    @JsonProperty("span_id")
    private String spanId;

    @JsonProperty("operation_name")
    private String operationName;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("start_time_ms")
    private long startTimeMs;

    @JsonProperty("end_time_ms")
    private long endTimeMs;

    @JsonProperty("total_latency_ms")
    private long totalLatencyMs;

    @JsonProperty("status")
    private String status;

    @JsonProperty("error_message")
    private String errorMessage;

    // Token Counting
    @JsonProperty("input_tokens")
    private int inputTokens;

    @JsonProperty("output_tokens")
    private int outputTokens;

    @JsonProperty("total_tokens")
    private int totalTokens;

    // Cost Estimation
    @JsonProperty("estimated_cost_usd")
    private double estimatedCostUsd;

    @JsonProperty("model_used")
    private String modelUsed;

    // Metadata
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("metadata")
    private Map<String, Object> metadata = new HashMap<>();

    // Parent Trace Info
    @JsonProperty("parent_trace_id")
    private String parentTraceId;

    @JsonProperty("child_traces")
    private Map<String, TraceMetadata> childTraces = new HashMap<>();

    // Span Details
    @JsonProperty("inputs")
    private Map<String, Object> inputs;

    @JsonProperty("outputs")
    private Map<String, Object> outputs;

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    public void addChildTrace(String name, TraceMetadata trace) {
        this.childTraces.put(name, trace);
    }

    public double calculateTokenCost() {
        // Cost estimation for HuggingFace models
        // Approximate: $0.0001 per 1000 input tokens, $0.0003 per 1000 output tokens
        double inputCost = (inputTokens / 1000.0) * 0.0001;
        double outputCost = (outputTokens / 1000.0) * 0.0003;
        this.estimatedCostUsd = inputCost + outputCost;
        return estimatedCostUsd;
    }

    @Override
    public String toString() {
        return String.format(
            "TraceMetadata{operation='%s', latency=%dms, tokens=%d, cost=$%.6f, status='%s'}",
            operationName, totalLatencyMs, totalTokens, estimatedCostUsd, status
        );
    }
}
