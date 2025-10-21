package edu.miu.blog.app.service.impl;

import edu.miu.blog.app.dto.chat.ChatRequest;
import edu.miu.blog.app.dto.chat.ChatResponse;
import edu.miu.blog.app.service.ChatService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/chat";

    @Override
    public ChatResponse sendToLlama(ChatRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Include "stream": false to get full response at once
        Map<String, Object> body = Map.of(
                "model", request.getModel(),
                "messages", request.getMessages(),
                "stream", false
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            System.out.println("Sending to Ollama: " + OLLAMA_URL);
            ResponseEntity<Map> response = restTemplate.exchange(
                    OLLAMA_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> data = response.getBody();
            if (data != null && data.get("message") instanceof Map<?, ?> msg) {
                String content = (String) msg.get("content");
                System.out.println("🟢 Ollama Response: " + content);
                return new ChatResponse(content);
            }

            return new ChatResponse("No valid reply from Ollama.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("🔴 Ollama error: " + e.getMessage());
            return new ChatResponse("Error calling Ollama: " + e.getMessage());
        }
    }
}
