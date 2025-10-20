package edu.miu.blog.app.service.impl;

import edu.miu.blog.app.domain.Article;
import edu.miu.blog.app.domain.Comment;
import edu.miu.blog.app.domain.User;
import edu.miu.blog.app.dto.comments.CommentRequest;
import edu.miu.blog.app.dto.comments.CommentResponse;
import edu.miu.blog.app.dto.user.UserResponse;
import edu.miu.blog.app.error.exception.ResourceNotFoundException;
import edu.miu.blog.app.repository.ArticleRepository;
import edu.miu.blog.app.repository.CommentRepository;
import edu.miu.blog.app.repository.UserRepository;
import edu.miu.blog.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentResponse> addComment(String slug, CommentRequest request, Long userId) {
        log.info("Adding comment to article with slug: {} by user: {}", slug, userId);
        
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        User authorComment = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setBody(request.getBody());
        comment.setArticle(article);
        comment.setAuthor(authorComment);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);
        log.info("Comment added successfully to article: {}", slug);

        return commentRepository.findByArticle(article).stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .body(c.getBody())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .article(c.getArticle().getId())
                        .author(getUserById(c.getAuthor()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByArticleSlug(String slug) {
        log.info("Getting comments for article with slug: {}", slug);
        
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        List<CommentResponse> comments = commentRepository.findByArticle(article).stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .body(c.getBody())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .article(c.getArticle().getId())
                        .author(getUserById(c.getAuthor()))
                        .build())
                .collect(Collectors.toList());
                
        log.info("Retrieved {} comments for article: {}", comments.size(), slug);
        return comments;
    }

    public UserResponse getUserById(User  user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }
}