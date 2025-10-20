package edu.miu.blog.app.service;

import edu.miu.blog.app.dto.chat.ChatRequest;
import edu.miu.blog.app.dto.chat.ChatResponse;

public interface ChatService {
    public ChatResponse sendToLlama(ChatRequest request);
}

