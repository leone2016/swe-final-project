package edu.miu.blog.app.dto;

import java.time.LocalDateTime;

public record ArticleCreatedEvent(
        String title,
        String author,
        String email,
        String summary,
        LocalDateTime createdAt
) {}
