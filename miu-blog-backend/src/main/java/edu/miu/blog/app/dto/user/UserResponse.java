package edu.miu.blog.app.dto.user;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String token;
    private String username;
    private String bio;
    private boolean following;
    private String image;
}
