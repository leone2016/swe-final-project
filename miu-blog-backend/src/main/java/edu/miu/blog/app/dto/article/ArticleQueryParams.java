package edu.miu.blog.app.dto.article;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ArticleQueryParams {
    String tag;          // ?tag=java
    String author;       // ?author=john
    String favorited;    // ?favorited=jane
    int    limit;        // ?limit=10   (default 20)
    int    offset;       // ?offset=0   (default 0)
}