package com.konopleva.crudeapp.config;

import com.konopleva.crudeapp.util.AuthRequestInterceptor;
import com.konopleva.crudeapp.util.IdempotenceKeyRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthRequestInterceptor authRequestInterceptor;
    public final IdempotenceKeyRequestInterceptor idempotenceKeyRequestInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authRequestInterceptor);
        registry.addInterceptor(idempotenceKeyRequestInterceptor);
    }
}
