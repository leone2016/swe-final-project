package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.profile.ProfileDto;
import edu.miu.blog.app.security.CurrentUser;
import edu.miu.blog.app.security.UserContext;
import edu.miu.blog.app.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable String username) throws ChangeSetPersister.NotFoundException {
        CurrentUser user = UserContext.get(); // current authenticated user
        ProfileDto profile = profileService.getProfile(user.getId(), username);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<ProfileDto> follow(@PathVariable String username) {

        CurrentUser user = UserContext.get();

        return ResponseEntity.ok(profileService.follow(user, username));
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<ProfileDto> unfollow(@PathVariable String username) {
        CurrentUser user = UserContext.get();
        return ResponseEntity.ok(profileService.unfollow(user, username));
    }
}