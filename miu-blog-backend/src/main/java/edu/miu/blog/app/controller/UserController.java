package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.user.*;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.UserService;
import edu.miu.blog.app.util.WrapWith;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@WrapWith("user")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserResponse> register(
            @RequestBody UserRegisterRequest body) {
        log.info("User registration request for username: {}", body.username());
        UserResponse response = userService.register(body);
        log.info("User registered successfully with email: {}", response.getEmail());
        return ResponseEntity.status(CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
             @RequestBody UserLoginRequest request) {
        log.info("User login attempt for email: {}", request.email());
        UserResponse response = userService.login(request);
        log.info("User logged in successfully: {}", response.getUsername());
        return ResponseEntity.status(OK).body(response);
    }

    @WrapWith("omit")
    @GetMapping()
    public ResponseEntity<UserResponse> findMe() {
        CurrentUser user = UserContext.get();
        log.debug("User requesting own profile: {}", user.getUsername());
        UserResponse response = userService.findByEmail(user.getEmail());
        log.debug("Profile retrieved for user: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            @RequestBody UserUpdateRequest request) {
        CurrentUser user = UserContext.get();
        log.info("User update request for: {}", user.getUsername());
        UserResponse response = userService.update(user.getEmail(), request);
        log.info("User updated successfully: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }
}
