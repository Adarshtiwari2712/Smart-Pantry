package com.smartpantry.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiVisionService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> extractExpiryDetails(byte[] imageBytes, String mimeType) {
        Map<String, String> result = new HashMap<>();

        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String promptText = "Analyze this grocery or cosmetic/medicine package image. "
                    + "Extract the exact product name and the expiration date (or Use Before / Best Before date). "
                    + "If the date is in MM/YY format (e.g., 04/27), convert it to YYYY-MM-DD format assuming the end of the month (e.g., 2027-04-30). "
                    + "Return strictly a JSON object with keys: 'itemName' (string) and 'expiryDate' (format: YYYY-MM-DD). "
                    + "If no expiry date is found, set 'expiryDate' to null. Do not add markdown codeblocks, notes, or extra text.";

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", promptText),
                                            Map.of("inline_data", Map.of(
                                                    "mime_type", mimeType,
                                                    "data", base64Image
                                            ))
                                    )
                            )
                    )
            );

            // Exactly matching your cURL command
            String response = webClient.post()
                    .uri(apiUrl)
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", apiKey.trim())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String rawText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText().trim();

            if (rawText.startsWith("```json")) {
                rawText = rawText.replace("```json", "").replace("```", "").trim();
            } else if (rawText.startsWith("```")) {
                rawText = rawText.replace("```", "").trim();
            }

            JsonNode parsedJson = objectMapper.readTree(rawText);
            result.put("itemName", parsedJson.path("itemName").asText(""));
            result.put("expiryDate", parsedJson.path("expiryDate").asText(""));

        } catch (Exception e) {
            System.err.println("Gemini Vision AI Error: " + e.getMessage());
            result.put("error", "Failed to extract details: " + e.getMessage());
        }

        return result;
    }
}