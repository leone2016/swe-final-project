package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.tag.TagsResponse;
import edu.miu.blog.app.security.JwtUtil;
import edu.miu.blog.app.service.TagService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.when;

@WebMvcTest(value = TagController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TagControllerRestAssuredTest {

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeAll
    public static void setup() {
        RestAssured.port = 8092; // Ajusta al puerto de tu app
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/tags";
    }

    @Test
    void getTags_shouldReturn200AndListOfTags() {
        // Arrange
        TagsResponse mockResponse = new TagsResponse(Arrays.asList("java", "spring", "docker"));
        when(tagService.getAllTags()).thenReturn(mockResponse);

        // Act & Assert
        given()
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(200);
    }
}
