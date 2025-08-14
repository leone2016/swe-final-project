package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.comments.CommentRequest;
import edu.miu.blog.app.dto.comments.CommentResponse;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.CommentService;
import edu.miu.blog.app.util.WrapWith;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@WrapWith("comments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{slug}/comments")
    public ResponseEntity<List<CommentResponse>> addComment(
            @PathVariable String slug,
            @RequestBody @Valid CommentRequest request) {
        CurrentUser user = UserContext.get();
        List<CommentResponse> comments = commentService.addComment(slug, request, user.getId());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("{slug}/comments")
    public ResponseEntity<Map<String, List<CommentResponse>>> getCommentsBySlug(@PathVariable String slug) {
        CurrentUser user = UserContext.get();

        List<CommentResponse> comments = commentService.getCommentsByArticleSlug(slug);
        return ResponseEntity.ok(Map.of("comments", comments));
    }

}