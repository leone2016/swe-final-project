package edu.miu.blog.app.service;

import edu.miu.blog.app.dto.user.*;

public interface UserService {
    UserResponse register(UserRegisterRequest request);

    UserResponse login(UserLoginRequest request);

    UserResponse findByEmail(String email);

    UserResponse update(String email, UserUpdateRequest request);
//    UserLoginResponse register(UserRegisterRequest request);
//    void delete(String email);
}
