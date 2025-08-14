package edu.miu.blog.app.dto.comments;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank
    private String body;
}