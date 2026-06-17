# LangSmith Dashboard Integration Guide

## Overview

All traces from your AI Stylist application are **automatically sent to the LangSmith cloud dashboard** at https://smith.langchain.com/. You can monitor all LLM calls, token usage, costs, and performance metrics in real-time from your browser.

## Setup Steps

### Step 1: Verify Environment Configuration

Ensure your application has these environment variables set:

```bash
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

### Step 2: Start Your Application

When your Spring Boot application starts, you should see:

```
========================================
LangSmith Initialization
========================================
Status: ENABLED
Project: StyleAI
Endpoint: https://api.smith.langchain.com
Tracing: ACTIVE
========================================
All LLM calls will be traced to LangSmith
View traces at: https://smith.langchain.com/
========================================
```

### Step 3: Make API Requests

Call your stylist recommendation endpoint:

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

Each request triggers **3 LLM calls**:
1. Style Analysis
2. Outfit Generation
3. Summary Generation

All are automatically traced to LangSmith.

### Step 4: View Traces in LangSmith Dashboard

1. Go to https://smith.langchain.com/
2. Log in with your account
3. Select the **StyleAI** project
4. You will see all traces organized by timestamp

## Trace Metadata

Each trace includes comprehensive metadata visible in LangSmith:

### Token Information
```
Input Tokens: 150
Output Tokens: 250
Total Tokens: 400
```

### Cost Analysis
```
Estimated Cost: $0.000090
Cost per 1K Tokens: $0.000225
```

### Performance Metrics
```
Latency: 3500 ms
Status: success | error
Model Used: mistralai/Mistral-7B-Instruct-v0.3
```

### Custom Tags
```
ai-stylist
evaluation
phase-1
```

## Dashboard Features

### 1. Traces List View

Shows all captured traces with:
- Trace ID
- Operation Name (Style Analysis, Outfit Generation, etc.)
- Timestamp
- Duration
- Status (success/error)
- Model used
- Token counts
- Estimated cost

### 2. Trace Details

Click on any trace to see:

**Overview Tab:**
- Full execution timeline
- Input/output details
- Metadata (tokens, cost, latency)
- Error messages (if any)

**Metadata Tab:**
```json
{
  "input_tokens": 150,
  "output_tokens": 250,
  "total_tokens": 400,
  "estimated_cost_usd": "0.000090",
  "latency_ms": 3500,
  "model_used": "mistralai/Mistral-7B-Instruct-v0.3",
  "tags": ["ai-stylist", "evaluation"]
}
```

**Timeline Tab:**
- Shows start time, end time
- Duration breakdown
- Child spans (if any)

### 3. Filtering & Search

In the dashboard, you can filter by:
- **Operation Name**: Filter by "Style Analysis", "Outfit Generation", etc.
- **Status**: Show only successful or failed traces
- **Date Range**: Filter by time period
- **Model**: Filter by model used
- **Tags**: Filter by custom tags

Example searches:
```
status:error
latency_ms:>5000
total_tokens:>1000
model:"mistralai/Mistral-7B-Instruct-v0.3"
```

### 4. Analytics & Reports

LangSmith provides built-in analytics:

- **Total Traces**: Count of all traces
- **Success Rate**: Percentage of successful executions
- **Average Latency**: Mean execution time
- **Total Cost**: Estimated cost of all LLM calls
- **Token Usage**: Total input/output tokens
- **Error Analysis**: Breakdown of failures

### 5. Feedback & Annotations

You can:
- Mark traces as correct/incorrect
- Add comments
- Create evaluation datasets
- Track improvements over time

## REST API Endpoints (Local)

While traces are stored in LangSmith, you can also query your **local trace cache** via REST APIs:

### Summary
```bash
curl http://localhost:8000/api/traces/summary
```

Response:
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

### All Traces
```bash
curl http://localhost:8000/api/traces/all
```

### Most Expensive Run
```bash
curl http://localhost:8000/api/traces/most-expensive
```

Response:
```json
{
  "trace_id": "abc-123-def",
  "operation_name": "Style Analysis",
  "input_tokens": 180,
  "output_tokens": 350,
  "total_tokens": 530,
  "estimated_cost_usd": "0.000159",
  "model_used": "mistralai/Mistral-7B-Instruct-v0.3",
  "latency_ms": 4200,
  "timestamp": "2024-01-15T10:30:45Z"
}
```

### Latency Analysis
```bash
curl http://localhost:8000/api/traces/latency-analysis
```

Response:
```json
{
  "total_latency_ms": 15000,
  "avg_latency_ms": "2500.00",
  "max_latency_ms": 5000,
  "min_latency_ms": 1500,
  "slowest_operation": "Style Analysis",
  "slowest_operation_latency_ms": 5000,
  "slowest_operation_percentage": "33.33%"
}
```

### Cost Analysis
```bash
curl http://localhost:8000/api/traces/cost-analysis
```

Response:
```json
{
  "total_tokens": 2400,
  "total_input_tokens": 1200,
  "total_output_tokens": 1200,
  "total_cost_usd": "0.000456",
  "avg_cost_per_request_usd": "0.000076",
  "cost_per_1k_tokens_usd": "0.000190"
}
```

### Problematic Runs
```bash
curl http://localhost:8000/api/traces/problematic-runs
```

Response:
```json
[
  {
    "trace_id": "xyz-789",
    "operation": "Outfit Generation",
    "category": "EXCESSIVE_OUTPUT",
    "issue": "Output tokens exceed 2000 (possible hallucination/verbose response)",
    "output_tokens": 2150,
    "severity": "MEDIUM",
    "timestamp": "2024-01-15T10:31:12Z"
  }
]
```

### Metrics Report
```bash
curl http://localhost:8000/api/traces/report
```

Returns comprehensive metrics including:
- Total statistics
- Latency analysis
- Token analysis
- Cost analysis
- Operation breakdown
- Most expensive run details
- Longest trace details
- Failed operations list

## Monitoring Strategy

### Daily Monitoring
1. Check LangSmith dashboard each day
2. Monitor **Success Rate** - should be > 95%
3. Track **Average Latency** - target < 4 seconds
4. Watch **Total Cost** - ensure within budget

### Weekly Analysis
1. Review error patterns in failed traces
2. Identify most expensive operations
3. Analyze token consumption trends
4. Create evaluation datasets from collected traces

### Monthly Reporting
1. Generate comprehensive cost reports
2. Identify optimization opportunities
3. Track improvements over time
4. Document model changes and results

## Trace Flow Example

When you call `/api/stylist/recommend`:

```
Request arrives
    ↓
StylistService.getStylistRecommendation()
    ↓
StylistWorkflow.analyzeStyle()
    ├─ Records: "Style Analysis" trace
    ├─ Sends to LangSmith (async)
    └─ Returns: StyleAnalysisResponse
    ↓
StylistWorkflow.generateOutfitRecommendations()
    ├─ Records: "Outfit Generation" trace
    ├─ Sends to LangSmith (async)
    └─ Returns: recommendations string
    ↓
StylistWorkflow.generateSummaryReport()
    ├─ Records: "Summary Generation" trace
    ├─ Sends to LangSmith (async)
    └─ Returns: summary string
    ↓
Response sent to client

Meanwhile (async):
├─ Trace 1 sent to LangSmith
├─ Trace 2 sent to LangSmith
└─ Trace 3 sent to LangSmith
    ↓
Visible in LangSmith dashboard within seconds
```

## Troubleshooting

### Traces Not Appearing in LangSmith

1. **Check API Key**: Verify `LANGSMITH_API_KEY` is correct
2. **Check Endpoint**: Verify `LANGSMITH_ENDPOINT` is `https://api.smith.langchain.com`
3. **Check Project**: Verify `LANGSMITH_PROJECT` is `StyleAI`
4. **Check Logs**: Look for "Trace successfully sent to LangSmith" messages
5. **Check Network**: Ensure outbound HTTPS access to api.smith.langchain.com

### Incorrect Token Counts

Token counts use word-based estimation. For accurate counts:
- Token estimation: word_count * 1.3
- Accuracy: ±15%
- For exact counts: Review HuggingFace API responses

### Missing Metadata

Ensure metadata includes:
- `input_tokens`
- `output_tokens`
- `total_tokens`
- `estimated_cost_usd`
- `latency_ms`
- `model_used`
- `tags`

## LangSmith Features for Evaluation

### Dataset Creation
In LangSmith, you can:
1. Star traces as "examples"
2. Create evaluation datasets
3. Add expected outputs
4. Run evaluators against the dataset

### Feedback & Evaluation
1. Mark traces as correct/incorrect
2. Add notes and comments
3. Track evaluation scores
4. Compare before/after improvements

### Performance Tracking
1. Compare latency over time
2. Track cost trends
3. Monitor error rates
4. Identify regressions

## Next Steps

1. **Phase 2**: Run 20 test cases and review all traces in LangSmith
2. **Phase 3**: Create evaluation dataset from traces
3. **Phase 4**: Create evaluators in LangSmith
4. **Phase 5**: Implement improvements and track impact

## Documentation

- [LangSmith Dashboard](https://smith.langchain.com/)
- [LangSmith Documentation](https://docs.smith.langchain.com/)
- [LangSmith API Reference](https://docs.smith.langchain.com/reference/api)
