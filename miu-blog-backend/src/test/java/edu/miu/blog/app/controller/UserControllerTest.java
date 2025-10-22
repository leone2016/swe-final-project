package edu.miu.blog.app.controller;


import edu.miu.blog.app.security.JwtUtil;
import edu.miu.blog.app.service.UserService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeAll
    public static void setup() {
        RestAssured.port = 8092;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/users";
    }

    @Test
    public void testHealth() {
        given()
            .when()
                .get("/health")
            .then()
                .statusCode(200);
    }
}
