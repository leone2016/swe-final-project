package edu.miu.blog.app.repository;

import edu.miu.blog.app.domain.Article;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecifications {
    public static Specification<Article> hasTag(String tag) {
        return (root, query, builder) -> builder.like(root.get("tagList"), "%" + tag + "%");
    }


    public static Specification<Article> authorUsername(String username) {
        return (root, query, cb) ->
                cb.equal(root.join("author").get("username"), username);
    }

    public static Specification<Article> favoritedByUsername(String username) {
        return (root, query, cb) ->
                cb.equal(root.join("favoritedBy").get("username"), username);
    }
}
