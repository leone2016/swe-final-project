package edu.miu.blog.app.controller;

import edu.miu.blog.app.dto.tag.TagsResponse;
import edu.miu.blog.app.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<TagsResponse> getTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }
}
