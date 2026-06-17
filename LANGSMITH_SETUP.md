# LangSmith Integration - Phase 1 Setup Guide

## Overview
This project has been fully instrumented with LangSmith for tracing, monitoring, and analyzing LLM calls. All operations are tracked with token counting, cost estimation, and latency analysis.

## Environment Setup

### Required Environment Variables
Add these to your `application.properties` or set them as environment variables:

```properties
# LangSmith Configuration
LANGSMITH_ENABLED=true
LANGSMITH_API_KEY=LANGSMITH_API_KEY_PLACEHOLDER
LANGSMITH_ENDPOINT=https://api.smith.langchain.com
LANGSMITH_PROJECT=StyleAI
LANGSMITH_TRACING=true
```

Or in `application.properties`:
```properties
langsmith.enabled=true
langsmith.api-key=LANGSMITH_API_KEY_PLACEHOLDER
langsmith.endpoint=https://api.smith.langchain.com
langsmith.project=StyleAI
langsmith.tracing-enabled=true
```

## Architecture

The LangSmith integration is built into the backend with the following components:

### 1. **LangSmithConfig** (`config/LangSmithConfig.java`)
- Initializes LangSmith configuration
- Sets up environment variables
- Manages HTTP client for API communication

### 2. **TraceRecorder** (`tracing/TraceRecorder.java`)
- Records all LLM traces in memory and persists to disk
- Generates metrics summaries
- Outputs traces to `./traces/langsmith_traces.jsonl` (JSONL format)
- Outputs metrics to `./traces/metrics_summary.json`

### 3. **TokenCounter** (`tracing/TokenCounter.java`)
- Estimates token counts for prompts and responses
- Calculates estimated costs based on HuggingFace pricing
- Pricing model:
  - Input: $0.0001 per 1000 tokens
  - Output: $0.0003 per 1000 tokens

### 4. **TraceMetadata** (`tracing/TraceMetadata.java`)
- Data model for storing trace information
- Includes: trace ID, operation name, timestamps, tokens, cost, status, error messages
- Supports parent-child trace relationships

### 5. **ProblematicRunDetector** (`tracing/ProblematicRunDetector.java`)
- Analyzes traces to identify issues
- Detection rules:
  - Failed executions (status = error)
  - Excessive output tokens (>2000) - possible hallucination
  - Incomplete responses (<20 tokens output)
  - Excessive latency (>5 seconds)
  - High cost (>$0.01)

### 6. **TraceController** (`controller/TraceController.java`)
- REST API endpoints for querying and analyzing traces
- Comprehensive metrics endpoints

## Instrumented Operations

The following operations are automatically traced:

### Level 1: Workflow Operations
1. **Style Analysis** - Analyzes user fashion preferences
2. **Outfit Generation** - Generates outfit recommendations
3. **Summary Generation** - Creates summary reports

### Level 2: API Calls
- **HuggingFace API Call** - Primary model invocation
- **HuggingFace API Call (Fallback)** - Fallback model invocation

## REST API Endpoints

All endpoints are under `/api/traces/`

### Summary & Overview
```
GET /api/traces/summary
- Returns: Total traces, successful/failed, token stats, cost stats
```

```
GET /api/traces/trace-count
- Returns: Number of recorded traces
```

### Individual Traces
```
GET /api/traces/all
- Returns: All recorded traces sorted by timestamp (newest first)
```

```
GET /api/traces/{traceId}
- Returns: Specific trace details
```

```
GET /api/traces/operation/{operationName}
- Returns: All traces for a specific operation
- Example: /api/traces/operation/Style%20Analysis
```

### Analysis Endpoints

#### Most Expensive Run
```
GET /api/traces/most-expensive
- Returns:
  - trace_id
  - operation_name
  - input_tokens
  - output_tokens
  - total_tokens
  - estimated_cost_usd
  - model_used
  - latency_ms
  - timestamp
```

#### Longest Execution
```
GET /api/traces/longest-execution
- Returns:
  - trace_id
  - operation_name
  - total_latency_ms
  - input_tokens
  - output_tokens
  - total_tokens
  - model_used
  - estimated_cost_usd
  - timestamp
```

#### Latency Analysis
```
GET /api/traces/latency-analysis
- Returns:
  - total_latency_ms
  - avg_latency_ms
  - max_latency_ms
  - min_latency_ms
  - slowest_operation
  - slowest_operation_latency_ms
  - slowest_operation_percentage (contribution to total)
```

#### Cost Analysis
```
GET /api/traces/cost-analysis
- Returns:
  - total_tokens
  - total_input_tokens
  - total_output_tokens
  - total_cost_usd
  - avg_cost_per_request_usd
  - cost_per_1k_tokens_usd
```

### Failed & Problematic Runs
```
GET /api/traces/failed
- Returns: All traces with status = "error"
```

```
GET /api/traces/problematic-runs
- Returns: All detected problematic runs (failures, excessive tokens, etc.)
```

```
POST /api/traces/analyze-problematic-runs
- Analyzes all traces and returns breakdown by severity and category
```

```
GET /api/traces/problematic-runs/severity/{severity}
- Returns: Problematic runs filtered by severity (HIGH, MEDIUM, LOW)
```

```
GET /api/traces/problematic-runs/category/{category}
- Returns: Problematic runs filtered by category
- Categories: FAILED_EXECUTION, EXCESSIVE_OUTPUT, INCOMPLETE_RESPONSE, EXCESSIVE_LATENCY, HIGH_COST
```

### Report Generation
```
POST /api/traces/generate-report
- Generates comprehensive metrics report
- Output: ./traces/metrics_summary.json
```

### Maintenance
```
DELETE /api/traces/clear
- Clears all recorded traces
```

## Data Storage

### Traces File
**Location**: `./traces/langsmith_traces.jsonl`

Each line is a JSON object with:
```json
{
  "trace_id": "uuid",
  "operation_name": "Style Analysis",
  "timestamp": "2024-01-15T10:30:45.123Z",
  "start_time_ms": 1234567890,
  "end_time_ms": 1234567895,
  "total_latency_ms": 5000,
  "status": "success|error",
  "input_tokens": 150,
  "output_tokens": 250,
  "total_tokens": 400,
  "estimated_cost_usd": 0.00012,
  "model_used": "mistralai/Mistral-7B-Instruct-v0.3"
}
```

### Metrics Summary
**Location**: `./traces/metrics_summary.json`

Contains:
- Total trace statistics
- Latency analysis
- Token analysis
- Cost analysis
- Operation breakdown
- Most expensive run
- Longest trace
- Failed operations

## Usage Example

### 1. Make a Recommendation Request
```bash
curl -X POST http://localhost:8000/api/stylist/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "gender": "Female",
    "ageGroup": "TWENTIES",
    "bodyType": "HOURGLASS",
    "skinTone": "MEDIUM",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Blue, Black, White",
    "stylePreference": "CASUAL",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "H&M, Forever 21",
    "fitPreference": "FITTED",
    "wardrobePreferences": "Minimalist"
  }'
```

### 2. View Summary
```bash
curl http://localhost:8000/api/traces/summary
```

Output:
```json
{
  "total_traces": 6,
  "successful_traces": 6,
  "failed_traces": 0,
  "total_tokens": 2400,
  "total_cost_usd": 0.000456,
  "total_latency_ms": 15000
}
```

### 3. Get Most Expensive Run
```bash
curl http://localhost:8000/api/traces/most-expensive
```

### 4. Analyze Latency
```bash
curl http://localhost:8000/api/traces/latency-analysis
```

Output:
```json
{
  "total_latency_ms": 15000,
  "avg_latency_ms": 2500.00,
  "max_latency_ms": 5000,
  "min_latency_ms": 1500,
  "slowest_operation": "Style Analysis",
  "slowest_operation_latency_ms": 5000,
  "slowest_operation_percentage": "33.33%"
}
```

### 5. Detect Problematic Runs
```bash
curl http://localhost:8000/api/traces/analyze-problematic-runs
```

### 6. Generate Report
```bash
curl -X POST http://localhost:8000/api/traces/generate-report
```

## Token Estimation Details

The token counter uses word-based estimation:
- **Formula**: `word_count * 1.3`
- **Rationale**: Average English word ≈ 1.3 tokens
- **Accuracy**: ±15% (acceptable for cost estimation)

For more accurate token counting in production, consider:
- Using OpenAI's `tiktoken` library
- Using HuggingFace's `transformers` library
- Using actual token counts from API responses

## Cost Calculation

**HuggingFace API Pricing** (estimated):
- Input: $0.0001 per 1000 tokens
- Output: $0.0003 per 1000 tokens

Example:
```
Input tokens: 150
Output tokens: 250
Input cost: (150 / 1000) * 0.0001 = $0.000015
Output cost: (250 / 1000) * 0.0003 = $0.000075
Total cost: $0.000090
```

## Problematic Run Detection Rules

| Category | Condition | Severity | Action |
|----------|-----------|----------|--------|
| FAILED_EXECUTION | status = "error" | HIGH | Investigate error message |
| EXCESSIVE_OUTPUT | output_tokens > 2000 | MEDIUM | Review for hallucination |
| INCOMPLETE_RESPONSE | output_tokens < 20 | MEDIUM | Review for truncation |
| EXCESSIVE_LATENCY | latency > 5s | LOW | Optimize model |
| HIGH_COST | cost > $0.01 | LOW | Optimize prompts |

## Monitoring Dashboard

To view traces in LangSmith dashboard:
1. Go to https://smith.langchain.com/
2. Select project: **StyleAI**
3. View all traces in real-time

## Performance Tips

1. **Token Optimization**: Keep prompts concise
2. **Latency Optimization**: Use fallback model if primary is slow
3. **Cost Optimization**: Reduce output token limits in API calls
4. **Batch Processing**: Process multiple requests to amortize overhead

## Troubleshooting

### Traces Not Recording
- Check: `langsmith.enabled=true` in properties
- Check: LangSmith API key is correct
- Check: `./traces/` directory exists and is writable

### Wrong Token Counts
- Token estimation uses word-based approximation
- For accurate counts, integrate `tiktoken` library
- Check actual API responses for token usage

### Missing Traces
- Verify exception wasn't thrown (check logs)
- Check TraceRecorder is autowired
- Verify application is still running

## Next Steps (Phase 2-5)

1. **Phase 2**: Run 10-20 test cases and analyze traces
2. **Phase 3**: Build evaluation dataset
3. **Phase 4**: Create evaluators (LLM-based and custom)
4. **Phase 5**: Implement optimizations based on findings

## Files Modified/Created

### New Files
- `config/LangSmithConfig.java`
- `config/LangSmithInitializer.java`
- `tracing/TraceMetadata.java`
- `tracing/TraceRecorder.java`
- `tracing/TokenCounter.java`
- `tracing/ProblematicRunDetector.java`
- `controller/TraceController.java`

### Modified Files
- `pom.xml` - Added dependencies
- `application.properties` - Added LangSmith configuration
- `ai/HuggingFaceClient.java` - Added tracing
- `workflow/StylistWorkflow.java` - Added tracing

## Dependencies Added

```xml
<dependency>
    <groupId>org.langchain4j</groupId>
    <artifactId>langchain4j-core</artifactId>
    <version>0.28.0</version>
</dependency>
```

## Success Criteria

Phase 1 is complete when:
- ✅ Application starts with LangSmith enabled
- ✅ API endpoints return trace data
- ✅ Traces are recorded in `./traces/langsmith_traces.jsonl`
- ✅ Metrics are calculated correctly
- ✅ Problematic runs are detected
- ✅ REST API endpoints work as documented
