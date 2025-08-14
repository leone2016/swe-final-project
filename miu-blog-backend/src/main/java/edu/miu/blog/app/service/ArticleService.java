package edu.miu.blog.app.service;

import edu.miu.blog.app.dto.article.*;
import edu.miu.blog.app.dto.roaster.RoasterDto;
import edu.miu.blog.app.security.CurrentUser;

import java.util.List;
import java.util.Objects;

public interface ArticleService {
    ArticleResponse createArticle(CurrentUser user, ArticleCreateRequest request);

    ArticleResponse updateArticleBySlug(CurrentUser user, String slug, ArticleCreateRequest request);

    ArticleListResponse listArticles(Long currentUserId, ArticleQueryParams params);

    ArticleListResponse getFeedForUser(Long userId, int limit, int offset);

    List<RoasterDto> findRoasterUsers(int limit, int offset);

    ArticleResponse getArticleBySlug(String slug, CurrentUser currentUser);
}

