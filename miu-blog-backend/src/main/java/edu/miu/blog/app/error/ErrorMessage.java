package edu.miu.blog.app.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    @NonNull
    private HttpStatus status;

    private String code;
    @NonNull
    private String message;

    @JsonProperty("errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ValidationError> errors;
}
