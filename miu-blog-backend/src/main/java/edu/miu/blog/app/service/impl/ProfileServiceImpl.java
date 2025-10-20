package edu.miu.blog.app.service.impl;

import edu.miu.blog.app.domain.User;
import edu.miu.blog.app.dto.profile.ProfileDto;
import edu.miu.blog.app.error.exception.ResourceNotFoundException;
import edu.miu.blog.app.repository.UserRepository;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.service.ProfileService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Override
    public ProfileDto follow(CurrentUser followingEmail, String usernameFollower) {
        log.info("User {} attempting to follow {}", followingEmail.getUsername(), usernameFollower);
        
        User following = userRepository.findByUsername(followingEmail.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User to follow not found"));

        if (following.getUsername().equals(usernameFollower)) {
            log.warn("User {} attempted to follow themselves", following.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot follow yourself.");
        }

        User follower = userRepository.findByUsername(usernameFollower)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Follower user not found"));

        follower.getFollowers().add(following);
        userRepository.save(follower);
        log.info("User {} successfully followed {}", following.getUsername(), usernameFollower);

        return ProfileDto.builder()
                .username(follower.getUsername())
                .bio(follower.getBio())
                .image(follower.getImage())
                .following(true)
                .build();

    }

    @Override
    public ProfileDto unfollow(CurrentUser userContext, String username) {
        log.info("User {} attempting to unfollow {}", userContext.getUsername(), username);
        
        if (userContext == null || username == null) {
            log.warn("Invalid unfollow request - userContext or username is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current user and username must be provided.");
        }

        User currentUser = userRepository.findByUsername(userContext.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Logged in user not found"));

        User toUnfollow = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User to unfollow not found"));

        if (currentUser.getId().equals(toUnfollow.getId())) {
            log.warn("User {} attempted to unfollow themselves", currentUser.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot unfollow yourself.");
        }

        // Aquí eliminamos la relación desde el lado propietario
        boolean removed = toUnfollow.getFollowers().removeIf(user -> user.getId().equals(currentUser.getId()));

        if (removed) {
            entityManager.merge(toUnfollow);
            entityManager.flush();
            log.info("User {} successfully unfollowed {}", currentUser.getUsername(), username);
        } else {
            log.warn("No follow relationship found between {} and {}", currentUser.getUsername(), username);
        }

        return ProfileDto.builder()
                .username(toUnfollow.getUsername())
                .bio(toUnfollow.getBio())
                .image(toUnfollow.getImage())
                .following(false)
                .build();
    }

    @Override
    public ProfileDto getProfile(Long currentUserId, String username) {
        log.info("Getting profile for username: {} by user: {}", username, currentUserId);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User targetUser = userRepository.findByUsernameWithFollowers(username)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        boolean following = targetUser.getFollowers().contains(currentUser);
        log.debug("User {} is following {}: {}", currentUser.getUsername(), username, following);

        return ProfileDto.builder()
                .username(targetUser.getUsername())
                .bio(targetUser.getBio())
                .image(targetUser.getImage())
                .following(following)
                .build();
    }

}