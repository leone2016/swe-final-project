package edu.miu.blog.app.service.impl;

import edu.miu.blog.app.domain.Tag;
import edu.miu.blog.app.dto.tag.TagsResponse;
import edu.miu.blog.app.repository.TagRepository;
import edu.miu.blog.app.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;


    @Override
    public TagsResponse getAllTags() {
        var names = tagRepository.findAll()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        return new TagsResponse(names);
    }

    @Override
    public List<Tag> getTagsByNames(List<String> tagNames) {
        return Optional.ofNullable(tagNames)
                .orElse(Collections.emptyList())
                .stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> {
                            Tag t = new Tag();
                            t.setName(name);
                            return tagRepository.save(t);
                        }))
                .collect(Collectors.toList());
    }
}
