package edu.miu.blog.app.config;

import edu.miu.blog.app.util.WrapWith;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

@ControllerAdvice
public class WrapperResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // Aplica a todos los controladores
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        WrapWith annotation = returnType.getMethodAnnotation(WrapWith.class);

        if (annotation == null) {
            annotation = returnType.getContainingClass().getAnnotation(WrapWith.class);
        }

        if (annotation != null) {
            return Map.of(annotation.value(), body);
        }

        return body;
    }
}
