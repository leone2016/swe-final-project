package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.article.*;
import edu.miu.blog.app.dto.roaster.RoasterDto;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.ArticleService;
import edu.miu.blog.app.util.WrapWith;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@WrapWith("article")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @Valid @RequestBody ArticleCreateRequest request) {
        log.info("Creating new article with title: {}", request.getTitle());
        CurrentUser user = UserContext.get();
        log.debug("User creating article: {}", user.getUsername());

        ArticleResponse response =
                articleService.createArticle(user, request);

        log.info("Article created successfully with slug: {}", response.getSlug());
        return ResponseEntity.status(CREATED).body(response);
    }

    @PutMapping("{slug}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable String slug,
            @RequestBody  ArticleCreateRequest request) {
        log.info("Updating article with slug: {}", slug);
        CurrentUser user = UserContext.get();
        log.debug("User updating article: {}", user.getUsername());
        
        ArticleResponse updated = articleService.updateArticleBySlug(user, slug, request);
        log.info("Article updated successfully with slug: {}", updated.getSlug());
        return ResponseEntity.ok(updated);
    }

    @WrapWith("omit")
    @GetMapping
    public ResponseEntity<ArticleListResponse> listArticles(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        log.info("Listing articles with filters - tag: {}, author: {}, favorited: {}, limit: {}, offset: {}", 
                tag, author, favorited, limit, offset);
        CurrentUser user = UserContext.get();
        log.debug("User requesting articles: {}", user.getUsername());

        ArticleQueryParams params = ArticleQueryParams.builder()
                .tag(tag)
                .author(author)
                .favorited(favorited)
                .limit(limit)
                .offset(offset)
                .build();
        
        ArticleListResponse response = articleService.listArticles(user.getId(), params);
        log.info("Found {} articles", response.getArticlesCount());
        return ResponseEntity.ok(response);
    }

    @WrapWith("omit")
    @GetMapping("/feed")
    public ResponseEntity<ArticleListResponse> getFeed(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        log.info("Getting feed for user with limit: {}, offset: {}", limit, offset);
        CurrentUser user = UserContext.get();
        log.debug("User requesting feed: {}", user.getUsername());
        
        ArticleListResponse response = articleService.getFeedForUser(user.getId(), limit, offset);
        log.info("Feed contains {} articles", response.getArticlesCount());
        return ResponseEntity.ok(response);
    }

    @WrapWith("roaster")
    @GetMapping("/roaster")
    public ResponseEntity<List<RoasterDto>> getRoasterUsers(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        log.info("Getting roaster users with limit: {}, offset: {}", limit, offset);

        var roaster = articleService.findRoasterUsers(limit, offset);
        log.info("Found {} roaster users", roaster.size());
        return ResponseEntity.ok(roaster);
    }


    @GetMapping("{slug}")
    public ResponseEntity<ArticleResponse> getArticleBySlug(
            @PathVariable String slug
    ) {
        log.info("Getting article by slug: {}", slug);
        CurrentUser user = UserContext.get();
        log.debug("User requesting article: {}", user.getUsername());
        
        ArticleResponse response = articleService.getArticleBySlug(slug, user);
        log.info("Article retrieved successfully: {}", response.getTitle());
        return ResponseEntity.ok(response);
    }
}
