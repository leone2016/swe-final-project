package edu.miu.blog.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest (
    @NotBlank
    String username,

    @Email
    @NotBlank
    String email,

    @NotBlank
    String password)
{}