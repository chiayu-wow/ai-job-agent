package com.example.ai_job_agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Slf4j
@Service
public class GroqService {

    // Read API key from application.yml
    @Value("${groq.api.key}")
    private String apiKey;

    // Groq API endpoint and model
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.1-8b-instant";

    // HTTP client to make API calls
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Send a message to Groq API and get a response
     * @param systemPrompt - defines AI's role and behavior
     * @param userMessage  - the actual question or task
     * @return AI's response as a String
     */
    public String chat(String systemPrompt, String userMessage) {
        try {
            // Build request body (equivalent to Python dict)
            Map<String, Object> body = new HashMap<>();
            body.put("model", MODEL);
            body.put("temperature", 0.3); // 0 = precise, 1 = creative

            // Build messages array (same as Python's messages list)
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt)); // AI role setup
            messages.add(Map.of("role", "user", "content", userMessage));    // user's question
            body.put("messages", messages);

            // Set HTTP headers (Content-Type + Authorization)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey); // Authorization: Bearer your-key

            // Wrap body + headers into one request object
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Call Groq API (like Python's requests.post())
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, request, Map.class);

            // Parse response: response → choices[0] → message → content
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            log.error("Groq API error: {}", e.getMessage());
            throw new RuntimeException("Failed to call Groq API", e);
        }
    }
}