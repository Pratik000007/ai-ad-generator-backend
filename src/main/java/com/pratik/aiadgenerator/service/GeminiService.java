package com.pratik.aiadgenerator.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${huggingface.api.token}")
    private String hfToken;

    private final WebClient webClient;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    // =============================
    // TEXT ADS GENERATION (Keep exactly as you have it)
    // =============================
    public String generateAds(String name, String description, String audience) {
        String prompt = """
                Generate 5 marketing ads.
                Return ONLY valid JSON array.
                Format: [{"headline": "...", "description": "...", "cta": "..."}]
                Product: %s | Description: %s | Audience: %s
                """.formatted(name, description, audience);

        return callGeminiText(prompt);
    }

    // =============================
    // IMAGE GENERATION (Reliable & Free)
    // =============================

    public String generateAdImage(String name, String description, String audience) {
        // 1. Check if token exists
        if (hfToken == null || hfToken.isEmpty()) {
            return "https://via.placeholder.com/1024?text=Missing+HF+Token";
        }

        String basePrompt = String.format("Professional marketing advertisement for %s. %s. 4k, photorealistic.", name, description);
        JSONObject payload = new JSONObject();
        payload.put("inputs", basePrompt);

        try {
            byte[] imageBytes = WebClient.builder().build()
                    .post()
                    // NEW ROUTER URL: This is the updated 2026 format
                    .uri("https://router.huggingface.co/hf-inference/models/black-forest-labs/FLUX.1-schnell")
                    .header("Authorization", "Bearer " + hfToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(payload.toString())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    // Retry specifically for 503 (Model Loading)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10))
                            .filter(ex -> ex instanceof WebClientResponseException &&
                                    ((WebClientResponseException) ex).getStatusCode().value() == 503))
                    .block();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

        } catch (WebClientResponseException e) {
            System.err.println("HF Router Error: " + e.getResponseBodyAsString());
            return "https://via.placeholder.com/1024?text=HF+Router+Error+" + e.getStatusCode();
        } catch (Exception e) {
            return "https://via.placeholder.com/1024?text=Error+Generating+Image";
        }
    }


    // =============================
    // PRIVATE METHODS
    // =============================
    private String callGeminiText(String prompt) {
        try {
            JSONObject part = new JSONObject().put("text", prompt);
            JSONObject content = new JSONObject().put("parts", new JSONArray().put(part));
            JSONObject requestBody = new JSONObject().put("contents", new JSONArray().put(content));

            return webClient.post()
                    .uri("/v1beta/models/gemini-3-flash-preview:generateContent?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Gemini Text API failed: " + e.getResponseBodyAsString());
        }
    }

    // This method is no longer strictly needed for Pollinations, but keep it
    // if your other service methods call it to avoid compilation errors.
    private String extractImageFromResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray candidates = json.getJSONArray("candidates");
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");

            for (int i = 0; i < parts.length(); i++) {
                JSONObject part = parts.getJSONObject(i);
                if (part.has("inlineData")) {
                    String base64 = part.getJSONObject("inlineData").getString("data");
                    return "data:image/png;base64," + base64;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract image");
        }
    }
}