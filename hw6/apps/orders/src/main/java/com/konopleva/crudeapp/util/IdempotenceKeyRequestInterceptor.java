package com.konopleva.crudeapp.util;

import com.konopleva.crudeapp.annotation.IdempotenceKey;
import com.konopleva.crudeapp.exception.IdempotenceException;
import com.konopleva.crudeapp.restClient.ApiAuthRestTemplate;
import com.konopleva.crudeapp.service.IdempotenceKeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;


@Component
public class IdempotenceKeyRequestInterceptor implements HandlerInterceptor {
    private final IdempotenceKeyService idempotenceKeyService;

    public IdempotenceKeyRequestInterceptor(IdempotenceKeyService idempotenceKeyService) {
        this.idempotenceKeyService = idempotenceKeyService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) { return true; }

        Method method = handlerMethod.getMethod();
        IdempotenceKey methodAnnotation = method.getAnnotation(IdempotenceKey.class);
        if (methodAnnotation != null) {
            return idempotenceKeyService.checkIdempotenceKey(request);
        }
        return true;
    }
}
