package edu.miu.blog.app.service.impl;

import edu.miu.blog.app.domain.Tag;
import edu.miu.blog.app.dto.tag.TagsResponse;
import edu.miu.blog.app.repository.TagRepository;
import edu.miu.blog.app.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;


    @Override
    public TagsResponse getAllTags() {
        log.info("Getting all tags");
        var names = tagRepository.findAll()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        log.info("Retrieved {} tags", names.size());
        return new TagsResponse(names);
    }

    @Override
    public List<Tag> getTagsByNames(List<String> tagNames) {
        log.debug("Processing {} tag names", tagNames != null ? tagNames.size() : 0);
        
        return Optional.ofNullable(tagNames)
                .orElse(Collections.emptyList())
                .stream()
                .map(name -> {
                    log.debug("Processing tag: {}", name);
                    return tagRepository.findByName(name)
                            .orElseGet(() -> {
                                log.debug("Creating new tag: {}", name);
                                Tag t = new Tag();
                                t.setName(name);
                                return tagRepository.save(t);
                            });
                })
                .collect(Collectors.toList());
    }
}
