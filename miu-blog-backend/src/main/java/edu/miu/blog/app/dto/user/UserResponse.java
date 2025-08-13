package edu.miu.blog.app.dto.user;
import lombok.Builder;

@Builder
public class UserResponse {
//    private Long id;
    private String email;
    private String token;
    private String username;
    private String bio;
    private String image;
}
