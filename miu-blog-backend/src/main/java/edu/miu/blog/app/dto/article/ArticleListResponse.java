package edu.miu.blog.app.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ArticleListResponse {
    private List<ArticleResponse> articles;
    private long articlesCount;
}

