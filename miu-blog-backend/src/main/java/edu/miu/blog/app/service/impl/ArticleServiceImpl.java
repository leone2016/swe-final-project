package edu.miu.blog.app.service.impl;

import edu.miu.blog.app.domain.Article;
import edu.miu.blog.app.domain.Tag;
import edu.miu.blog.app.domain.User;
import edu.miu.blog.app.dto.article.*;
import edu.miu.blog.app.dto.roaster.RoasterDto;
import edu.miu.blog.app.dto.user.UserResponse;
import edu.miu.blog.app.error.exception.ResourceNotFoundException;
import edu.miu.blog.app.repository.ArticleRepository;
import edu.miu.blog.app.repository.ArticleSpecifications;
import edu.miu.blog.app.repository.TagRepository;
import edu.miu.blog.app.repository.UserRepository;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.JwtUtil;
import edu.miu.blog.app.service.ArticleService;
import edu.miu.blog.app.service.TagService;
import edu.miu.blog.app.util.SlugUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final JwtUtil jwtUtil;


    @Override
    @Transactional
    public ArticleResponse createArticle(CurrentUser userLogin, ArticleCreateRequest req) {
        log.info("Creating article for user: {} with title: {}", userLogin.getUsername(), req.getTitle());

        // ---------- Tags ----------
        String tagsConcatenados = Optional.ofNullable(req.getTagList())
                .orElse(List.of())
                .stream()
                .collect(Collectors.joining(","));

        List<Tag> tags = tagService.getTagsByNames(req.getTagList());
        log.debug("Processing {} tags for article", tags.size());
        
        // ---------- Author ----------
        User author = userRepository
                .findById(userLogin.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        log.debug("Found author: {}", author.getUsername());
        
        // ---------- Article ----------
        Article article = new Article();
        article.setAuthor(author);
        article.setTagList(tagsConcatenados);
        article.setTitle(req.getTitle());
        article.setDescription(req.getDescription());
        article.setBody(req.getBody());
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setSlug(SlugUtil.slugify(req.getTitle())); // util simple
        article.setTags(tags);

        articleRepository.save(article);
        log.info("Article saved successfully with slug: {}", article.getSlug());

        return handlerArticleResponse(article);
    }

    @Override
    public ArticleResponse updateArticleBySlug(CurrentUser user, String slug, ArticleCreateRequest req) {
        log.info("Updating article with slug: {} by user: {}", slug, user.getUsername());
        
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (!article.getAuthor().getId().equals(user.getId())) {
            log.warn("User {} attempted to update article {} they don't own", user.getUsername(), slug);
            throw new ResourceNotFoundException("You are not the author of this article");
        }

        if (req.getTitle() != null) {
            log.debug("Updating article title from '{}' to '{}'", article.getTitle(), req.getTitle());
            article.setTitle(req.getTitle());
            article.setSlug(SlugUtil.slugify(req.getTitle()));
        }

        if (req.getDescription() != null) {
            log.debug("Updating article description");
            article.setDescription(req.getDescription());
        }

        if (req.getBody() != null) {
            log.debug("Updating article body");
            article.setBody(req.getBody());
        }

        if (req.getTagList() != null) {
            log.debug("Updating article tags");
            List<Tag> tags = tagService.getTagsByNames(req.getTagList());
            article.setTags(tags);
            article.setTagList(String.join(",", req.getTagList()));
        }

        article.setUpdatedAt(LocalDateTime.now());
        articleRepository.save(article);
        log.info("Article updated successfully with slug: {}", article.getSlug());

        return handlerArticleResponse(article);
    }

    @Override
    public ArticleListResponse listArticles(Long currentUserId, ArticleQueryParams params) {
        log.info("Listing articles for user: {} with params: tag={}, author={}, favorited={}, limit={}, offset={}", 
                currentUserId, params.getTag(), params.getAuthor(), params.getFavorited(), params.getLimit(), params.getOffset());
        
        Specification<Article> spec = Specification.unrestricted();

        if (params.getTag() != null) {
            log.debug("Filtering by tag: {}", params.getTag());
            spec = spec.and(ArticleSpecifications.hasTag(params.getTag()));
        }

        if (params.getAuthor() != null) {
            if (userRepository.findByUsername(params.getAuthor()).isEmpty()) {
                log.warn("Author not found: {}", params.getAuthor());
                return new ArticleListResponse(List.of(), 0);
            }
            log.debug("Filtering by author: {}", params.getAuthor());
            spec = spec.and(ArticleSpecifications.authorUsername(params.getAuthor()));
        }

        if (params.getFavorited() != null) {
            if (userRepository.findByUsername(params.getFavorited()).isEmpty()) {
                log.warn("Favorited user not found: {}", params.getFavorited());
                return new ArticleListResponse(List.of(), 0);
            }
            log.debug("Filtering by favorited: {}", params.getFavorited());
            spec = spec.and(ArticleSpecifications.favoritedByUsername(params.getFavorited()));
        }

        PageRequest pageReq = PageRequest.of(
                params.getOffset() / params.getLimit(),
                params.getLimit(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Article> page = articleRepository.findAll(spec, pageReq);
        log.info("Found {} articles out of {} total", page.getContent().size(), page.getTotalElements());

        List<ArticleResponse> responses = page.getContent().stream()
                .map(article ->{
                    ArticleResponse articleResponse = handlerArticleResponse(article);
                    articleResponse.setAuthor(handlerUserResponse(article.getAuthor()));
                    return articleResponse;
                })
                .collect(Collectors.toList());

        return new ArticleListResponse(responses, page.getTotalElements());
    }

    @Override
    public ArticleListResponse getFeedForUser(Long userId, int limit, int offset) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<User> following = user.getFollowing();
        if (following.isEmpty()) {
            return new ArticleListResponse(List.of(), 0);
        }

        Page<Article> articles = articleRepository.findByAuthorsIn(
                following,
                PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<ArticleResponse> responseList = articles.getContent().stream()
                .map(article -> {
                    ArticleResponse articleResponse = handlerArticleResponse(article);
                    articleResponse.setAuthor(handlerUserResponse(article.getAuthor()));
                    return articleResponse;
                })
                .toList();

        return new ArticleListResponse(responseList, articles.getTotalElements());
    }

    @Override
    public List<RoasterDto> findRoasterUsers(int limit, int offset) {
        return articleRepository.findRoasterUsers(limit, offset);
    }

    @Override
    public ArticleResponse getArticleBySlug(String slug, CurrentUser currentUser) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException( "Article not found"));

        User author = article.getAuthor();

        // current user following author's article?
        boolean following = author.getFollowers().stream()
                .anyMatch(f -> f.getId().equals(currentUser.getId()));
        UserResponse authorDto =  UserResponse.builder()
                .id(author.getId())
                .username(author.getUsername())
                .email(author.getEmail())
                .bio(author.getBio())
                .image(author.getImage())
                .following(following)
                .build();;
        ArticleResponse articleResponse = handlerArticleResponse(article);
        articleResponse.setAuthor(authorDto);
        return articleResponse;
    }

    private ArticleResponse handlerArticleResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .body(article.getBody())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .tagList(Arrays.stream(article.getTagList().split(",")).toList())
                .favoritesCount(0)
                .favorited(false)
                .build();
    }

    private UserResponse handlerUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }


}
