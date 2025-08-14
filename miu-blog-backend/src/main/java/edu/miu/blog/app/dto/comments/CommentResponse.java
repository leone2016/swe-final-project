package edu.miu.blog.app.dto.comments;

import edu.miu.blog.app.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String body;
    private Long article;
    private UserResponse author;
}
