package edu.miu.blog.app.service.impl;


import edu.miu.blog.app.domain.User;
import edu.miu.blog.app.dto.user.UserRegisterRequest;
import edu.miu.blog.app.dto.user.UserResponse;
import edu.miu.blog.app.error.exception.BusinessException;
import edu.miu.blog.app.repository.UserRepository;
import edu.miu.blog.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.isUserRegister(request)) {
            throw new BusinessException("Credentials already exist");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setBio("");
        user.setImage("");

        userRepository.save(user);
//        String token = jwtUtil.generateToken(user);
        UserResponse response = UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .token("token").build();

        return response;
    }
}
