//package edu.miu.blog.app.controller;
//
//import edu.miu.blog.app.dto.article.ArticleResponse;
//import edu.miu.blog.app.security.CurrentUser;
//import edu.miu.blog.app.security.JwtUtil;
//import edu.miu.blog.app.service.ArticleService;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import edu.miu.blog.app.security.UserContext;
//
//import java.util.ArrayList;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.Matchers.anyOf;
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//
//@WebMvcTest(value = ArticleController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
//class ArticleControllerTest {
//    @MockitoBean
//    private ArticleService articleService;
//
//
//    @MockitoBean
//    private JwtUtil jwtUtil;
//
//    private ArticleResponse sampleResponse;
//
//
//    @BeforeEach
//    void setup() {
//
//        UserContext.set(new CurrentUser(1L, "leo@example.com", "leo" ));
//
//        sampleResponse = ArticleResponse.builder()
//                .id(1L)
//                .slug("sample-article")
//                .title("Sample Article")
//                .description("This is a sample article.")
//                .body("Sample body content.")
//                .tagList(new ArrayList<>())
//                .favoritesCount(0)
//                .favorited(false)
//                .author(null)
//                .build();
//
//        RestAssured.port = 8092;
//        RestAssured.baseURI = "http://localhost";
//        RestAssured.basePath = "/api/articles";
//    }
//
//    @Test
//    void shouldCreateArticle() {
//        Mockito.when(articleService.createArticle(any(), any()))
//                .thenReturn(sampleResponse);
//
//        given()
//                .contentType(ContentType.JSON)
//                .body("""
//                        {
//                          "title": "First Article",
//                          "description": "Basic description",
//                          "body": "Sample body",
//                          "tagList": ["java","spring"]
//                        }
//                        """)
//                .when()
//                .post()
//                .then()
//                .statusCode(anyOf(is(201), is(422)))
//                .body("article.title", equalTo("First Article"));
//    }
//
//}
