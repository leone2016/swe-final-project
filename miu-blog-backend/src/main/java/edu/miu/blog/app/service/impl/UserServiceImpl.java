package edu.miu.blog.app.service.impl;


import edu.miu.blog.app.domain.User;
import edu.miu.blog.app.dto.user.UserLoginRequest;
import edu.miu.blog.app.dto.user.UserRegisterRequest;
import edu.miu.blog.app.dto.user.UserResponse;
import edu.miu.blog.app.dto.user.UserUpdateRequest;
import edu.miu.blog.app.error.exception.BusinessException;
import edu.miu.blog.app.error.exception.ResourceNotFoundException;
import edu.miu.blog.app.repository.UserRepository;
import edu.miu.blog.app.security.JwtUtil;
import edu.miu.blog.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        log.info("User registration attempt for email: {}", request.email());
        
        if (userRepository.isUserRegister(request)) {
            log.warn("Registration failed - credentials already exist for email: {}", request.email());
            throw new BusinessException("Credentials already exist");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setBio("");
        user.setImage("https://cdn.vectorstock.com/i/preview-2x/43/98/default-avatar-photo-placeholder-icon-grey-vector-38594398.webp");

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
        
        String token = jwtUtil.generateToken(user);
        UserResponse response = UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .token(token).build();

        return response;
    }

    @Override
    public UserResponse login(UserLoginRequest request) {
        log.info("User login attempt for email: {}", request.email());
        
        User user = userRepository.findByEmailAndPassword(
                        request.email(),
                        request.password())
                .orElseThrow(() -> {
                    log.warn("Login failed - invalid credentials for email: {}", request.email());
                    return new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
                });
        
        log.info("User logged in successfully: {}", user.getUsername());
        String token = jwtUtil.generateToken(user);

        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .bio(user.getBio())
                .image(user.getImage())
                .token(token).build();


    }

    @Override
    public UserResponse findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        log.debug("User found: {}", user.getUsername());
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .token(jwtUtil.generateToken(user))
                .build();
    }

    @Override
    public UserResponse update(String email, UserUpdateRequest request) {
        log.info("Updating user with email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.username() != null && !request.username().isBlank()) {
            log.debug("Updating username from '{}' to '{}'", user.getUsername(), request.username());
            user.setUsername(request.username());
        }
        if (request.bio() != null && !request.bio().isBlank()) {
            log.debug("Updating bio");
            user.setBio(request.bio());
        }
        if (request.image() != null && !request.image().isBlank()) {
            log.debug("Updating image");
            user.setImage(request.image());
        }
        if (request.password() != null && !request.password().isBlank()) {
            log.debug("Updating password");
            user.setPassword(request.password());
        }

        userRepository.saveAndFlush(user);
        log.info("User updated successfully: {}", user.getUsername());

        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .token(jwtUtil.generateToken(user))
                .build();
    }



}
