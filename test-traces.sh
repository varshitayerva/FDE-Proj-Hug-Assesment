#!/bin/bash

# LangSmith Tracing Test Script
# This script tests all trace endpoints and generates reports

BASE_URL="http://localhost:8000/api"
TRACES_URL="$BASE_URL/traces"

echo "======================================"
echo "LangSmith Traces Test Suite"
echo "======================================"
echo ""

# Test 1: Get Summary
echo "TEST 1: Get Traces Summary"
echo "Endpoint: GET $TRACES_URL/summary"
curl -s -X GET "$TRACES_URL/summary" | jq . || echo "Failed"
echo ""
echo ""

# Test 2: Get Trace Count
echo "TEST 2: Get Trace Count"
echo "Endpoint: GET $TRACES_URL/trace-count"
curl -s -X GET "$TRACES_URL/trace-count" | jq . || echo "Failed"
echo ""
echo ""

# Test 3: Get All Traces
echo "TEST 3: Get All Traces (first 3)"
echo "Endpoint: GET $TRACES_URL/all"
curl -s -X GET "$TRACES_URL/all" | jq '.[0:3]' || echo "Failed"
echo ""
echo ""

# Test 4: Get Most Expensive Run
echo "TEST 4: Get Most Expensive Run"
echo "Endpoint: GET $TRACES_URL/most-expensive"
curl -s -X GET "$TRACES_URL/most-expensive" | jq . || echo "Failed"
echo ""
echo ""

# Test 5: Get Longest Execution
echo "TEST 5: Get Longest Execution"
echo "Endpoint: GET $TRACES_URL/longest-execution"
curl -s -X GET "$TRACES_URL/longest-execution" | jq . || echo "Failed"
echo ""
echo ""

# Test 6: Get Latency Analysis
echo "TEST 6: Get Latency Analysis"
echo "Endpoint: GET $TRACES_URL/latency-analysis"
curl -s -X GET "$TRACES_URL/latency-analysis" | jq . || echo "Failed"
echo ""
echo ""

# Test 7: Get Cost Analysis
echo "TEST 7: Get Cost Analysis"
echo "Endpoint: GET $TRACES_URL/cost-analysis"
curl -s -X GET "$TRACES_URL/cost-analysis" | jq . || echo "Failed"
echo ""
echo ""

# Test 8: Get Failed Traces
echo "TEST 8: Get Failed Traces"
echo "Endpoint: GET $TRACES_URL/failed"
curl -s -X GET "$TRACES_URL/failed" | jq . || echo "Failed"
echo ""
echo ""

# Test 9: Analyze Problematic Runs
echo "TEST 9: Analyze Problematic Runs"
echo "Endpoint: POST $TRACES_URL/analyze-problematic-runs"
curl -s -X POST "$TRACES_URL/analyze-problematic-runs" | jq . || echo "Failed"
echo ""
echo ""

# Test 10: Get Problematic Runs
echo "TEST 10: Get Problematic Runs"
echo "Endpoint: GET $TRACES_URL/problematic-runs"
curl -s -X GET "$TRACES_URL/problematic-runs" | jq . || echo "Failed"
echo ""
echo ""

# Test 11: Generate Report
echo "TEST 11: Generate Metrics Report"
echo "Endpoint: POST $TRACES_URL/generate-report"
curl -s -X POST "$TRACES_URL/generate-report" || echo "Failed"
echo ""
echo ""

echo "======================================"
echo "Test Suite Complete"
echo "======================================"
echo ""
echo "Reports generated in ./traces/"
echo "  - langsmith_traces.jsonl (all traces)"
echo "  - metrics_summary.json (aggregated metrics)"
echo ""
