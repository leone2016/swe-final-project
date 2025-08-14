package edu.miu.blog.app.service;

import edu.miu.blog.app.domain.Tag;
import edu.miu.blog.app.dto.tag.TagsResponse;

import java.util.List;

public interface TagService {
    TagsResponse getAllTags();
    List<Tag> getTagsByNames(List<String> tagNames);
}
