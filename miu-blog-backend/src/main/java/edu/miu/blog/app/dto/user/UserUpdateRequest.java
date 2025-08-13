package edu.miu.blog.app.dto.user;

public record UserUpdateRequest(
        String username,
        String bio,
        String image,
        String password)
{}