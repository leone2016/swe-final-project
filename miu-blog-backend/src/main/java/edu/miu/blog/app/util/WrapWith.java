package edu.miu.blog.app.util;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WrapWith {
    String value();
}
