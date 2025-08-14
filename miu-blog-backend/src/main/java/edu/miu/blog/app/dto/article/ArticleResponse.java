package edu.miu.blog.app.dto.article;

import edu.miu.blog.app.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ArticleResponse {
    private Long id;
    private String slug;
    private String title;
    private String description;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tagList;
    private int favoritesCount;
    private boolean favorited;
    private UserResponse author;
}
