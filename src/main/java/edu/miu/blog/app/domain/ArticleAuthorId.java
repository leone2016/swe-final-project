package edu.miu.blog.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAuthorId {

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "user_id")
    private Long userId;
}
