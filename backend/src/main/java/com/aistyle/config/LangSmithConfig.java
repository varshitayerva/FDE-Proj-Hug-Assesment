package com.aistyle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.net.http.HttpClient;

@Slf4j
@Configuration
public class LangSmithConfig {

    @Value("${langsmith.enabled:true}")
    private boolean enabled;

    @Value("${langsmith.api-key}")
    private String apiKey;

    @Value("${langsmith.endpoint}")
    private String endpoint;

    @Value("${langsmith.project}")
    private String project;

    @Value("${langsmith.tracing-enabled:true}")
    private boolean tracingEnabled;

    public LangSmithConfig() {
        // Set environment variables for LangSmith
        if (isEnabled()) {
            System.setProperty("LANGCHAIN_TRACING_V2", "true");
            System.setProperty("LANGCHAIN_API_KEY", getApiKey());
            System.setProperty("LANGCHAIN_ENDPOINT", getEndpoint());
            System.setProperty("LANGCHAIN_PROJECT", getProject());

            if (tracingEnabled) {
                log.info("LangSmith Tracing initialized for project: {}", project);
                log.info("LangSmith Endpoint: {}", endpoint);
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getProject() {
        return project;
    }

    public boolean isTracingEnabled() {
        return tracingEnabled;
    }

    public HttpClient getHttpClient() {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    }
}
