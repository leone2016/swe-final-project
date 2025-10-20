package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.chat.ChatRequest;
import edu.miu.blog.app.dto.chat.ChatResponse;
import edu.miu.blog.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.sendToLlama(request);
        return ResponseEntity.ok(response);
    }
}
