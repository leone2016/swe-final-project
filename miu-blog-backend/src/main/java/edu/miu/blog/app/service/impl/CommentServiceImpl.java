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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentResponse> addComment(String slug, CommentRequest request, Long userId) {
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
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

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

    public UserResponse getUserById(User  user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }
}