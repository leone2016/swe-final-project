package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.comments.CommentRequest;
import edu.miu.blog.app.dto.comments.CommentResponse;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.CommentService;
import edu.miu.blog.app.util.WrapWith;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WrapWith("comments")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/articles")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{slug}/comments")
    public ResponseEntity<List<CommentResponse>> addComment(
            @PathVariable String slug,
            @RequestBody @Valid CommentRequest request) {
        log.info("Adding comment to article with slug: {}", slug);
        CurrentUser user = UserContext.get();
        log.debug("User adding comment: {}", user.getUsername());
        List<CommentResponse> comments = commentService.addComment(slug, request, user.getId());
        log.info("Comment added successfully to article: {}", slug);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("{slug}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsBySlug(@PathVariable String slug) {
        log.info("Getting comments for article with slug: {}", slug);
        CurrentUser user = UserContext.get();
        log.debug("User requesting comments: {}", user.getUsername());

        List<CommentResponse> comments = commentService.getCommentsByArticleSlug(slug);
        log.info("Retrieved {} comments for article: {}", comments.size(), slug);
        return ResponseEntity.ok(comments);
    }

}