package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.user.*;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.UserService;
import edu.miu.blog.app.util.WrapWith;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@WrapWith("user")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserResponse> register(
            @RequestBody UserRegisterRequest body) {
        return ResponseEntity.status(CREATED).body(userService.register(body));
    }


    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
             @RequestBody UserLoginRequest request) {
        return ResponseEntity.status(OK).body(userService.login(request));
    }

    @WrapWith("omit")
    @GetMapping()
    public ResponseEntity<UserResponse> findMe() {
        CurrentUser user = UserContext.get();
        return ResponseEntity.ok(userService.findByEmail(user.getEmail()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            @RequestBody UserUpdateRequest request) {
        CurrentUser user = UserContext.get();
        return ResponseEntity.ok(userService.update(user.getEmail(), request));
    }
}
