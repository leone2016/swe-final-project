package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.profile.ProfileDto;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.ProfileService;
import edu.miu.blog.app.util.WrapWith;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@WrapWith("profile")
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable String username) throws ChangeSetPersister.NotFoundException {
        log.info("Getting profile for username: {}", username);
        CurrentUser user = UserContext.get(); // current authenticated user
        log.debug("User requesting profile: {}", user.getUsername());
        ProfileDto profile = profileService.getProfile(user.getId(), username);
        log.info("Profile retrieved successfully for: {}", username);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<ProfileDto> follow(@PathVariable String username) {
        log.info("User attempting to follow: {}", username);
        CurrentUser user = UserContext.get();
        log.debug("User following: {}", user.getUsername());

        ProfileDto response = profileService.follow(user, username);
        log.info("User {} successfully followed {}", user.getUsername(), username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<ProfileDto> unfollow(@PathVariable String username) {
        log.info("User attempting to unfollow: {}", username);
        CurrentUser user = UserContext.get();
        log.debug("User unfollowing: {}", user.getUsername());
        ProfileDto response = profileService.unfollow(user, username);
        log.info("User {} successfully unfollowed {}", user.getUsername(), username);
        return ResponseEntity.ok(response);
    }
}