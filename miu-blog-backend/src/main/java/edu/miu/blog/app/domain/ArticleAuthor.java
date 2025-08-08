package edu.miu.blog.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAuthor {

    @EmbeddedId
    private ArticleAuthorId id;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}