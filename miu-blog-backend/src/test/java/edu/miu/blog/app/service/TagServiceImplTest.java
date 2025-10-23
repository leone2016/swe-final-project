package edu.miu.blog.app.service;

import edu.miu.blog.app.domain.Tag;
import edu.miu.blog.app.dto.tag.TagsResponse;
import edu.miu.blog.app.repository.TagRepository;
import edu.miu.blog.app.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


class TagServiceImplTest {

    private final TagRepository tagRepository = mock(TagRepository.class);
    private final TagServiceImpl tagService = new TagServiceImpl(tagRepository);

    @Test
    void getAllTags_shouldReturnListOfTagNames() {
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        tag1.setName("java");
        tag2.setName("spring");
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        List<Tag> tags = List.of(tag1, tag2);
        when(tagRepository.findAll()).thenReturn(tags);

        TagsResponse response = tagService.getAllTags();

        assertEquals(List.of("java", "spring"), response.getTags());
        verify(tagRepository, times(1)).findAll();
    }
}