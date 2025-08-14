package edu.miu.blog.app.dto.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private String username;
    private String bio;
    private String image;
    private boolean following;
}