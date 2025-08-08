package edu.miu.blog.app.service;

import edu.miu.blog.app.dto.user.UserRegisterRequest;
import edu.miu.blog.app.dto.user.UserResponse;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
}
