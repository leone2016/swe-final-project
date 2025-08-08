package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.user.UserRegisterRequest;
import edu.miu.blog.app.dto.user.UserResponse;
import edu.miu.blog.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> register( @RequestBody UserRegisterRequest body) {
        return ResponseEntity.status(CREATED).body(userService.register(body));
    }
}
