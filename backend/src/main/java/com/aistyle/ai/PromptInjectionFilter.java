package com.aistyle.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PromptInjectionFilter {

    private static final String[] INJECTION_KEYWORDS = {
        "ignore previous instructions",
        "reveal system prompt",
        "system prompt",
        "act as developer",
        "bypass restrictions",
        "forget instructions",
        "jailbreak",
        "override instructions",
        "disable safety",
        "turn off safety",
        "forget previous",
        "ignore instructions",
        "disregard instructions",
        "hidden instructions",
        "secret instructions"
    };

    private static final Pattern[] INJECTION_PATTERNS = {
        Pattern.compile("(?i)ignore.*(?:previous|above|prior|earlier).*instructions"),
        Pattern.compile("(?i)reveal.*(?:system|hidden|secret).*prompt"),
        Pattern.compile("(?i)(?:system|hidden|secret)\\s+prompt"),
        Pattern.compile("(?i)act\\s+as\\s+(?:developer|admin|system)"),
        Pattern.compile("(?i)(?:bypass|override|disable).*(?:safety|restrictions)"),
        Pattern.compile("(?i)(?:forget|disregard|ignore).*instructions"),
        Pattern.compile("(?i)jailbreak|prompt.*injection"),
        Pattern.compile("(?i)turn\\s+(?:off|down)\\s+safety")
    };

    public boolean isInjectionAttempt(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String lowerInput = input.toLowerCase();

        // Check against keyword blocklist
        for (String keyword : INJECTION_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                log.warn("Potential prompt injection detected - keyword: {}", keyword);
                return true;
            }
        }

        // Check against regex patterns
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                log.warn("Potential prompt injection detected - pattern match");
                return true;
            }
        }

        return false;
    }

    public String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        // Remove or escape potentially dangerous characters
        String sanitized = input
            .replaceAll("(?i)ignore previous instructions", "[REDACTED]")
            .replaceAll("(?i)reveal system prompt", "[REDACTED]")
            .replaceAll("(?i)act as developer", "[REDACTED]")
            .replaceAll("(?i)jailbreak", "[REDACTED]")
            .replaceAll("(?i)bypass restrictions", "[REDACTED]");

        // Limit input length to prevent token explosion attacks
        if (sanitized.length() > 5000) {
            log.warn("Input exceeded maximum length, truncating");
            sanitized = sanitized.substring(0, 5000);
        }

        return sanitized;
    }

    public String validateAndSanitize(String input) throws SecurityException {
        if (isInjectionAttempt(input)) {
            throw new SecurityException("Potential prompt injection attack detected. Input contains suspicious patterns.");
        }
        return sanitizeInput(input);
    }
}
