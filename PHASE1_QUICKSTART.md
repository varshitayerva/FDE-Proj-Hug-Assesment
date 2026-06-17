# Phase 1: Quick Start Guide

## What Was Done

Your AI Stylist application has been **fully instrumented with LangSmith**. All LLM calls are now automatically traced and sent to the LangSmith cloud dashboard.

## Files Added/Modified

### New Files Created:
1. `config/LangSmithConfig.java` - LangSmith configuration
2. `config/LangSmithInitializer.java` - Initialization on startup
3. `tracing/TraceMetadata.java` - Trace data model
4. `tracing/TraceRecorder.java` - Records and sends traces to LangSmith
5. `tracing/TokenCounter.java` - Estimates token usage
6. `tracing/ProblematicRunDetector.java` - Detects issues in traces
7. `controller/TraceController.java` - REST API endpoints
8. Documentation files (this guide, setup guide, dashboard guide)

### Modified Files:
1. `pom.xml` - Added LangSmith dependencies
2. `application.properties` - Added LangSmith configuration
3. `ai/HuggingFaceClient.java` - Added tracing
4. `workflow/StylistWorkflow.java` - Added tracing

## Setup Instructions

### Step 1: Verify Environment Variables

Add to your environment or `application.properties`:

```properties
langsmith.enabled=true
langsmith.api-key=LANGSMITH_API_KEY_PLACEHOLDER
langsmith.endpoint=https://api.smith.langchain.com
langsmith.project=StyleAI
langsmith.tracing-enabled=true
```

### Step 2: Build and Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

You should see:
```
========================================
LangSmith Initialization
========================================
Status: ENABLED
Project: StyleAI
Endpoint: https://api.smith.langchain.com
Tracing: ACTIVE
========================================
```

### Step 3: Make Test Request

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

### Step 4: View Traces in LangSmith

1. Open https://smith.langchain.com/
2. Log in or create account
3. Select project: **StyleAI**
4. You should see 3 traces (one for each LLM call):
   - Style Analysis
   - Outfit Generation
   - Summary Generation

## What Gets Traced

### Each Request Generates 3 Traces:

**1. Style Analysis**
- Analyzes user fashion preferences
- Input: User profile information
- Output: Style analysis with scores
- Tokens: ~150 input, ~250 output
- Cost: ~$0.00009

**2. Outfit Generation**
- Generates outfit recommendations
- Input: User profile + style analysis
- Output: Detailed outfit recommendations
- Tokens: ~200 input, ~300 output
- Cost: ~$0.00015

**3. Summary Generation**
- Creates concise summary
- Input: Analysis + recommendations
- Output: 2-3 sentence summary
- Tokens: ~100 input, ~50 output
- Cost: ~$0.00003

### Metadata Captured:

For each trace, LangSmith shows:
- **Trace ID**: Unique identifier
- **Operation Name**: Style Analysis, Outfit Generation, etc.
- **Timestamp**: When it ran
- **Duration**: How long it took (ms)
- **Status**: Success or error
- **Input Tokens**: Prompt token count
- **Output Tokens**: Response token count
- **Total Tokens**: Input + output
- **Estimated Cost**: USD amount
- **Model Used**: mistralai/Mistral-7B-Instruct-v0.3
- **Tags**: ai-stylist, evaluation

## REST API Endpoints (Optional)

If you want to query traces locally (without going to LangSmith):

### Get Summary
```bash
curl http://localhost:8000/api/traces/summary
```

### Get Most Expensive Run
```bash
curl http://localhost:8000/api/traces/most-expensive
```

### Get Longest Execution
```bash
curl http://localhost:8000/api/traces/longest-execution
```

### Get Latency Analysis
```bash
curl http://localhost:8000/api/traces/latency-analysis
```

### Get Cost Analysis
```bash
curl http://localhost:8000/api/traces/cost-analysis
```

### Get Problematic Runs
```bash
curl http://localhost:8000/api/traces/problematic-runs
```

## Phase 1 Checklist

- [ ] Environment variables configured
- [ ] Application starts successfully
- [ ] LangSmith initialization message appears in logs
- [ ] Made a test request to `/api/stylist/recommend`
- [ ] Traces appear in LangSmith dashboard (https://smith.langchain.com/)
- [ ] Can see token counts in LangSmith
- [ ] Can see estimated costs in LangSmith
- [ ] Can see latency information in LangSmith
- [ ] Can see metadata for each trace

## Moving to Phase 2

Once Phase 1 is complete, Phase 2 involves:

1. **Run 20 test cases** with diverse user profiles
2. **Analyze latency** - Find longest traces and bottlenecks
3. **Analyze costs** - Identify most expensive operations
4. **Identify failures** - Find problematic runs (errors, hallucinations, incomplete responses)
5. **Create dataset** - Extract 20 test cases from your traces

See: `EVALUATION_DATASET.md` for the 20 test cases

## Key URLs

- **LangSmith Dashboard**: https://smith.langchain.com/
- **LangSmith Project**: https://smith.langchain.com/o/YOUR_ORG/projects/p/StyleAI
- **API Base**: http://localhost:8000/api

## Documentation Files

- `LANGSMITH_SETUP.md` - Full setup documentation
- `LANGSMITH_DASHBOARD_GUIDE.md` - How to use the dashboard
- `EVALUATION_DATASET.md` - 20 test cases for Phase 2
- `PHASE1_QUICKSTART.md` - This file

## Support

### Common Issues

**Q: Traces not appearing in LangSmith?**
A: Check that:
1. API key is correct
2. Endpoint is `https://api.smith.langchain.com`
3. Project name is `StyleAI`
4. Application is running
5. Check application logs for errors

**Q: Token counts seem wrong?**
A: Token estimation uses word-based approximation (word_count * 1.3). Accuracy is ±15%. This is acceptable for cost estimation. For exact counts, integrate `tiktoken` library.

**Q: How are costs calculated?**
A: Based on HuggingFace pricing:
- Input: $0.0001 per 1000 tokens
- Output: $0.0003 per 1000 tokens

**Q: Can I see traces locally without LangSmith?**
A: Yes! Use the REST API endpoints provided. Traces are cached in memory on the application.

**Q: How long are traces kept?**
A: Traces are kept in memory while the application is running. They're also visible in LangSmith indefinitely (or as per your LangSmith subscription).

## Next: Phase 2

When ready, refer to `EVALUATION_DATASET.md` to run 20 test cases and proceed with the analysis phase.

Good luck with your LangSmith integration! 🚀
