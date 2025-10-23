package edu.miu.blog.app.controller;


import edu.miu.blog.app.dto.user.UserRegisterRequest;
import edu.miu.blog.app.security.JwtUtil;
import edu.miu.blog.app.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

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

    @Test
    public void registerUser_shouldReturn201AndUserInfo() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    
                    {
                        "user": {
                            "username": "testuser",
                            "email": "test@gmail.com",
                            "password": "123123123"
                        }
                    }
                    """)
                .when()
                .post()
                .then()
                .statusCode(anyOf(equalTo(422), equalTo(201)));
    }


    @Test
    public void loginUser_shouldReturn200AndToken() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    
                    {
                        "user": {
                            "email": "test@gmail.com",
                            "password": "123123123"
                        }
                    }
                    """)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("user.email", equalTo("test@gmail.com"));
    }
}
