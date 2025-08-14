package edu.miu.blog.app.repository;

import edu.miu.blog.app.domain.Article;
import edu.miu.blog.app.domain.Comment;
import edu.miu.blog.app.dto.comments.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticle(Article article);
    List<CommentResponse> getCommentsByArticleSlug(String slug);

}