package com.finbot.userservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public GeminiService(@Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.url}") String apiUrl) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public String getAdvice(String prompt) {
        try {
            // POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=API_KEY
            String url = apiUrl + "?key=" + apiKey;

            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

            if(response.getBody() != null) {
                List<Map> candidates = (List<Map>) response.getBody().get("candidates");
                Map candidate = candidates.get(0);
                Map contentMap = (Map) candidate.get("contents");
                List<Map> parts = (List<Map>) contentMap.get("parts");
                return (String) parts.get(0).get("text");
            }
        } catch (Exception e) {
            log.error("Gemini API hatası: {}", e.getMessage());
        }

        return "Şu an öneri alınamıyor, lütfen daha sonra tekar deneyin.";
    }
}
