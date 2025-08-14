package edu.miu.blog.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

@ControllerAdvice
public class WrapperRequestAdvice extends RequestBodyAdviceAdapter {

    private final ObjectMapper objectMapper;

    public WrapperRequestAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // aplica a todos los métodos
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage,
                                           MethodParameter parameter,
                                           Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException, IOException {

        // Leer el JSON original como Map
        Map<String, Object> wrapper = objectMapper.readValue(inputMessage.getBody(), Map.class);

        if (wrapper.size() == 1) {
            Object innerValue = wrapper.values().iterator().next();
            byte[] jsonBytes = objectMapper.writeValueAsBytes(innerValue);

            // Devolver nuevo HttpInputMessage con el contenido reescrito
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() {
                    return new ByteArrayInputStream(jsonBytes);
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
        }

        return inputMessage; // si no cumple formato, pasa tal cual
    }
}