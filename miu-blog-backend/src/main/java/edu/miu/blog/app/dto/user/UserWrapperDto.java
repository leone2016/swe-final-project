package edu.miu.blog.app.dto.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWrapperDto<T> {
    private T user;
}
