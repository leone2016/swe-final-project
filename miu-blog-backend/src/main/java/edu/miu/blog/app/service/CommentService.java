package edu.miu.blog.app.service;

import edu.miu.blog.app.dto.comments.CommentRequest;
import edu.miu.blog.app.dto.comments.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> addComment(String slug, CommentRequest request, Long userId);
    List<CommentResponse> getCommentsByArticleSlug(String slug);
}
