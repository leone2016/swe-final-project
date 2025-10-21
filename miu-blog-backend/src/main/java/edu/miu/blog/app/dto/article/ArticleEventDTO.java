package edu.miu.blog.app.dto.article;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEventDTO {
    private String title;
    private String description;
    private String authorName;
    private String authorEmail;
    private LocalDateTime createdAt;
}