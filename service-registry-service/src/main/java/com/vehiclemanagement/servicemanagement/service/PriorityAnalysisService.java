//package com.vehiclemanagement.servicemanagement.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.google.genai.Client;
//import com.google.genai.types.GenerateContentResponse;
//
//import java.util.*;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class PriorityAnalysisService {
//
//    @Value("${gemini.api.key:}")
//    private String apiKey;
//
//    @Value("${gemini.api.enabled:true}")
//    private boolean apiEnabled;
//    private final Client client;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
//
//    public PriorityAnalysisResponse analyzePriority(String description,String date) {
//
//        // Try AI if enabled and key is available
//        if (apiEnabled && apiKey != null && !apiKey.isBlank()) {
//            try {
//                log.info("Using Gemini AI for priority analysis");
//                PriorityAnalysisResponse response = askGemini(description,date);
////                return parseResponse(response);
//                return response;
//            } catch (Exception e) {
//                log.warn("Gemini AI failed: {}, using keyword analysis", e.getMessage());
//            }
//        } else {
//            log.info("Gemini AI not configured, using keyword analysis");
//        }
////        return null;
//        return fallback(description);
//    }
//
//    public  PriorityAnalysisResponse askGemini(String description,String date) {
//        // Using a valid model name. 'gemini-2.5-flash' is likely incorrect/future.
//        // Standard models: gemini-1.5-flash, gemini-1.5-pro, gemini-pro
//    	String text="""
//        Analyze the following vehicle service request and determine priority.
//
//        Request:
//        %s
//
//        Respond ONLY in this format:
//        priority: HIGH | MEDIUM | LOW
//        reason: Short explanation
//        """+"the description of the problem is "+description+" also the created date and time is "+date+" give the response in json fromat";
//        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", description,
//                null);
//        System.out.println(response.text());
//        PriorityAnalysisResponse a=this.parseResponse(response.text());
//        System.out.println(a);
//        return a;
////        PriorityAnalysisResponse(response.text().priority,)
////        System.out.println(response.text());
////        return response.text();
//    }
//
//   
//
//    private PriorityAnalysisResponse parseResponse(String aiText) {
//        String priority = "MEDIUM";
//        String reason = "No clear reason provided";
//
//        if (aiText == null || aiText.isEmpty()) {
//            return new PriorityAnalysisResponse(priority, reason);
//        }
//
//        String upper = aiText.toUpperCase();
//
//        // Detect PRIORITY
//        if (upper.contains("PRIORITY:")) {
//            String[] parts = upper.split("PRIORITY:");
//            if (parts.length > 1) {
//                String line = parts[1].split("\n")[0].trim();
//                if (line.contains("HIGH"))
//                    priority = "HIGH";
//                else if (line.contains("LOW"))
//                    priority = "LOW";
//                else
//                    priority = "MEDIUM";
//            }
//        }
//
//        // Detect REASON
//        if (aiText.contains("REASON:")) {
//            String[] parts = aiText.split("REASON:");
//            if (parts.length > 1) {
//                reason = parts[1].trim();
//                if (reason.contains("\n")) {
//                    reason = reason.substring(0, reason.indexOf("\n")).trim();
//                }
//            }
//        }
//
//        return new PriorityAnalysisResponse(priority, reason);
//    }
//
//    private String buildPrompt(String description) {
//        return """
//                Analyze the following vehicle service request and determine priority.
//
//                Request:
//                %s
//
//                Respond ONLY in this format:
//                PRIORITY: HIGH | MEDIUM | LOW
//                REASON: Short explanation
//                """.formatted(description);
//    }
//
//    private PriorityAnalysisResponse fallback(String description) {
//        String text = description.toLowerCase();
//
//        if (text.contains("brake") || text.contains("engine") || text.contains("smoke")
//                || text.contains("accident") || text.contains("fire")) {
//            return new PriorityAnalysisResponse("HIGH",
//                    "Safety-critical issue detected");
//        }
//
//        if (text.contains("service") || text.contains("oil") ||
//                text.contains("noise") || text.contains("battery")) {
//            return new PriorityAnalysisResponse("MEDIUM",
//                    "Routine maintenance or performance issue");
//        }
//
//        return new PriorityAnalysisResponse("LOW",
//                "Cosmetic or non-urgent issue");
//    }
//
//    public static class PriorityAnalysisResponse {
//        private String priority;
//        private String reason;
//
//        public PriorityAnalysisResponse(String priority, String reason) {
//            this.priority = priority;
//            this.reason = reason;
//        }
//
//        public String getPriority() {
//            return priority;
//        }
//
//        public String getReason() {
//            return reason;
//        }
//    }
//}

package com.vehiclemanagement.servicemanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriorityAnalysisService {

    @Value("${gemini.api.enabled:true}")
    private boolean apiEnabled;

    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Entry point used by controller / service
     */
    public PriorityAnalysisResponse analyzePriority(String description, String date) {

        if (apiEnabled) {
            try {
                log.info("Using Gemini AI for priority analysis");
                return askGemini(description, date);
            } catch (Exception e) {
                log.warn("Gemini AI failed: {}, using keyword analysis", e.getMessage());
            }
        } else {
            log.info("Gemini AI disabled, using keyword analysis");
        }

        return fallback(description);
    }

    /**
     * Gemini AI call with strict JSON prompt
     */
    private PriorityAnalysisResponse askGemini(String description, String date) {

        String prompt = """
You are a classification system.

Analyze the vehicle service request and decide its priority.Consider date also to avoid those with less priority to wait for long time

Rules:
- Respond ONLY with valid JSON
- No markdown
- No explanations
- No extra text

Input:
Description: %s
CreatedAt: %s

Output format:
{
  "priority": "HIGH | MEDIUM | LOW",
  "reason": "One short sentence"
}
""".formatted(description, date);

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
                );

        String aiText = response.text();
        log.debug("Gemini raw response: {}", aiText);

        try {
            return objectMapper.readValue(aiText, PriorityAnalysisResponse.class);
        } catch (Exception e) {
            log.warn("Failed to parse Gemini JSON, falling back");
            return fallback(description);
        }
    }

    /**
     * Fallback keyword-based logic (SAFE & DETERMINISTIC)
     */
    private PriorityAnalysisResponse fallback(String description) {

        String text = description.toLowerCase();

        if (text.contains("brake")
                || text.contains("engine")
                || text.contains("smoke")
                || text.contains("fire")
                || text.contains("accident")
                || text.contains("won't start")
                || text.contains("not starting")) {

            return new PriorityAnalysisResponse(
                    "HIGH",
                    "Safety-critical or engine-related issue detected"
            );
        }

        if (text.contains("noise")
                || text.contains("battery")
                || text.contains("service")
                || text.contains("oil")
                || text.contains("vibration")
                || text.contains("maintenance")) {

            return new PriorityAnalysisResponse(
                    "MEDIUM",
                    "Routine maintenance or performance-related issue"
            );
        }

        return new PriorityAnalysisResponse(
                "LOW",
                "Non-urgent or cosmetic issue"
        );
    }

    /**
     * Response DTO
     */
    public static class PriorityAnalysisResponse {

        private String priority;
        private String reason;

        public PriorityAnalysisResponse() {
        }

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

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}

