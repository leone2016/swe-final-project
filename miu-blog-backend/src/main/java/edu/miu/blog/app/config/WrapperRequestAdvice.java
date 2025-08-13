package edu.miu.blog.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;
import java.util.Map;

@Component
public class WrapperRequestAdvice extends RequestBodyAdviceAdapter {
    private final ObjectMapper objectMapper;

    public WrapperRequestAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // This advice applies to all request bodies
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

        if (body instanceof Map<?, ?> map && map.size() == 1) {
            Object innerValue = map.values().iterator().next();
            return objectMapper.convertValue(innerValue, objectMapper.constructType(targetType));
        }
        return body;
    }
}
