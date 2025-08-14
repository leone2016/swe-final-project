package edu.miu.blog.app.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TagsResponse {
    private List<String> tags;
}
