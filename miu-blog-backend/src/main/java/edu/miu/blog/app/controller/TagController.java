package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.tag.TagsResponse;
import edu.miu.blog.app.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Slf4j
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<TagsResponse> getTags() {
        log.info("Getting all tags");
        TagsResponse response = tagService.getAllTags();
        log.info("Retrieved {} tags", response.getTags().size());
        return ResponseEntity.ok(response);
    }
}
