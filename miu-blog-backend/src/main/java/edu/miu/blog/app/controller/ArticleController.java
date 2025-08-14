package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.article.*;
import edu.miu.blog.app.dto.roaster.RoasterDto;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.ArticleService;
import edu.miu.blog.app.util.WrapWith;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@WrapWith("article")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @Valid @RequestBody ArticleCreateRequest request) {
        CurrentUser user = UserContext.get();

        ArticleResponse response =
                articleService.createArticle(user, request);

        return ResponseEntity.status(CREATED).body(response);
    }

    @PutMapping("{slug}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable String slug,
            @RequestBody  ArticleCreateRequest request) {
        CurrentUser user = UserContext.get();
        ArticleResponse updated = articleService.updateArticleBySlug(user, slug, request);
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
        CurrentUser user = UserContext.get();


        ArticleQueryParams params = ArticleQueryParams.builder()
                .tag(tag)
                .author(author)
                .favorited(favorited)
                .limit(limit)
                .offset(offset)
                .build();
        return ResponseEntity.ok( articleService.listArticles(user.getId(), params));
    }

    @WrapWith("omit")
    @GetMapping("/feed")
    public ResponseEntity<ArticleListResponse> getFeed(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        CurrentUser user = UserContext.get();
        ArticleListResponse response = articleService.getFeedForUser(user.getId(), limit, offset);
        return ResponseEntity.ok(response);
    }

    @WrapWith("roaster")
    @GetMapping("/roaster")
    public ResponseEntity<List<RoasterDto>> getRoasterUsers(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        var roaster = articleService.findRoasterUsers(limit, offset);
        return ResponseEntity.ok(roaster);
    }


    @GetMapping("{slug}")
    public ResponseEntity<ArticleResponse> getArticleBySlug(
            @PathVariable String slug
    ) {
        CurrentUser user = UserContext.get();
        ArticleResponse response = articleService.getArticleBySlug(slug, user);
        return ResponseEntity.ok(response);
    }
}
