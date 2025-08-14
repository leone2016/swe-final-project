package edu.miu.blog.app.service;

import edu.miu.blog.app.dto.profile.ProfileDto;
import edu.miu.blog.app.security.CurrentUser;

public interface ProfileService {
    ProfileDto follow(CurrentUser followerEmail, String username);
    ProfileDto unfollow(CurrentUser userContext, String username);
    ProfileDto getProfile(Long currentUserId, String username);
}
