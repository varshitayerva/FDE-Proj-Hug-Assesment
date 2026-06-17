package com.aistyle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LangSmithInitializer implements CommandLineRunner {

    @Autowired
    private LangSmithConfig langSmithConfig;

    @Override
    public void run(String... args) throws Exception {
        if (!langSmithConfig.isEnabled()) {
            log.warn("LangSmith is disabled. Set langsmith.enabled=true to enable tracing.");
            return;
        }

        log.info("========================================");
        log.info("LangSmith Initialization");
        log.info("========================================");
        log.info("Status: ENABLED");
        log.info("Project: {}", langSmithConfig.getProject());
        log.info("Endpoint: {}", langSmithConfig.getEndpoint());
        log.info("Tracing: {}", langSmithConfig.isTracingEnabled() ? "ACTIVE" : "DISABLED");
        log.info("========================================");
        log.info("All LLM calls will be traced to LangSmith");
        log.info("View traces at: https://smith.langchain.com/");
        log.info("========================================");

        // Initialize environment variables
        System.setProperty("LANGCHAIN_TRACING_V2", "true");
        System.setProperty("LANGCHAIN_PROJECT", langSmithConfig.getProject());

        log.info("LangSmith environment configured successfully");
    }
}
