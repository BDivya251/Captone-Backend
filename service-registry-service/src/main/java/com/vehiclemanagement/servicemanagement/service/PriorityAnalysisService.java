package com.vehiclemanagement.servicemanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class PriorityAnalysisService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.enabled:true}")
    private boolean apiEnabled;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public PriorityAnalysisResponse analyzePriority(String description) {

        // Try AI if enabled and key is available
        if (apiEnabled && apiKey != null && !apiKey.isBlank()) {
            try {
                log.info("Using Gemini AI for priority analysis");
                String response = callGemini(description);
                return parseResponse(response);
            } catch (Exception e) {
                log.warn("Gemini AI failed: {}, using keyword analysis", e.getMessage());
            }
        } else {
            log.info("Gemini AI not configured, using keyword analysis");
        }

        return fallback(description);
    }

    private String callGemini(String description) {
        String url = GEMINI_URL + "?key=" + apiKey;

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", buildPrompt(description))))));

        body.put("generationConfig", Map.of(
                "temperature", 0.3,
                "topP", 0.8,
                "topK", 10,
                "maxOutputTokens", 200));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Gemini API error: " + response.getStatusCode());
        }

        Map<String, Object> responseBody = response.getBody();
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Empty Gemini response");
        }

        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        return parts.get(0).get("text").toString();
    }

    private PriorityAnalysisResponse parseResponse(String aiText) {
        String priority = "MEDIUM";
        String reason = "No clear reason provided";

        if (aiText == null || aiText.isEmpty()) {
            return new PriorityAnalysisResponse(priority, reason);
        }

        String upper = aiText.toUpperCase();

        // Detect PRIORITY
        if (upper.contains("PRIORITY:")) {
            String[] parts = upper.split("PRIORITY:");
            if (parts.length > 1) {
                String line = parts[1].split("\n")[0].trim();
                if (line.contains("HIGH"))
                    priority = "HIGH";
                else if (line.contains("LOW"))
                    priority = "LOW";
                else
                    priority = "MEDIUM";
            }
        }

        // Detect REASON
        if (aiText.contains("REASON:")) {
            String[] parts = aiText.split("REASON:");
            if (parts.length > 1) {
                reason = parts[1].trim();
                if (reason.contains("\n")) {
                    reason = reason.substring(0, reason.indexOf("\n")).trim();
                }
            }
        }

        return new PriorityAnalysisResponse(priority, reason);
    }

    private String buildPrompt(String description) {
        return """
                Analyze the following vehicle service request and determine priority.

                Request:
                %s

                Respond ONLY in this format:
                PRIORITY: HIGH | MEDIUM | LOW
                REASON: Short explanation
                """.formatted(description);
    }

    private PriorityAnalysisResponse fallback(String description) {
        String text = description.toLowerCase();

        if (text.contains("brake") || text.contains("engine") || text.contains("smoke")
                || text.contains("accident") || text.contains("fire")) {
            return new PriorityAnalysisResponse("HIGH",
                    "Safety-critical issue detected");
        }

        if (text.contains("service") || text.contains("oil") ||
                text.contains("noise") || text.contains("battery")) {
            return new PriorityAnalysisResponse("MEDIUM",
                    "Routine maintenance or performance issue");
        }

        return new PriorityAnalysisResponse("LOW",
                "Cosmetic or non-urgent issue");
    }

    public static class PriorityAnalysisResponse {
        private String priority;
        private String reason;

        public PriorityAnalysisResponse(String priority, String reason) {
            this.priority = priority;
            this.reason = reason;
        }

        public String getPriority() {
            return priority;
        }

        public String getReason() {
            return reason;
        }
    }
}
