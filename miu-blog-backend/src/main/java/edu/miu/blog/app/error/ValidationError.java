package edu.miu.blog.app.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
}
