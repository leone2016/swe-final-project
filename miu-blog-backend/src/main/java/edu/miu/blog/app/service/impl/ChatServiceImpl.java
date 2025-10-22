package edu.miu.blog.app.service.impl;

import edu.miu.blog.app.dto.chat.ChatRequest;
import edu.miu.blog.app.dto.chat.ChatResponse;
import edu.miu.blog.app.service.ChatService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/chat";

    // Store memory of the chat in memory
    private final List<Map<String, String>> conversationHistory = new ArrayList<>();

    @Override
    public ChatResponse sendToLlama(ChatRequest request) {

        if (conversationHistory.isEmpty()) {
            conversationHistory.add(Map.of(
                    "role", "system",
                    "content", "You are Edd, a friendly helpful assistant that chats like a human. you only assist" +
                            "people for creating blogs and you are integrated in a a blog website."
            ));
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Convert ChatMessage objects into Map of String, String
        List<Map<String, String>> newMessages = request.getMessages().stream()
                .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
                .toList();

        // Add to history
        conversationHistory.addAll(newMessages);

        Map<String, Object> body = Map.of(
                "model", request.getModel(),
                "messages", conversationHistory,
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

            Map data = response.getBody();
            if (data != null && data.get("message") instanceof Map<?, ?> msg) {
                String content = (String) msg.get("content");
                System.out.println("Ollama Response: " + content);

                // Store assistant reply in history
                conversationHistory.add(Map.of("role", "assistant", "content", content));

                return new ChatResponse(content);
            }

            return new ChatResponse("No valid reply from Ollama.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ollama error: " + e.getMessage());
            return new ChatResponse("Error calling Ollama: " + e.getMessage());
        }
    }

}
