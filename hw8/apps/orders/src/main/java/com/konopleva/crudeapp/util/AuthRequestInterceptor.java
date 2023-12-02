package com.konopleva.crudeapp.util;

import com.konopleva.crudeapp.restClient.ApiAuthRestTemplate;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Base64;

@Component
@Slf4j
public class AuthRequestInterceptor implements HandlerInterceptor {
    @Value("${outbound.authProviderUrl}")
    private String apiAuthServiceUrl;

    private final JwtValidator jwtValidator;

    public AuthRequestInterceptor(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            var isTokenValid = jwtValidator.validateToken(token);
            if (!isTokenValid) {
                throw new JwtException("Token is not valid");
            }
            return true;
        }

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // Decode the Base64-encoded username and password
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));

            // Split the credentials into username and password
            String[] parts = credentials.split(":");
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];
                log.info("Trying to authenticate user with username {}", username);
                // You can now access the username and password here
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>())
                        );
                var restTemplate = new ApiAuthRestTemplate(username, password);
                var authServerResp = restTemplate.restTemplate()
                        .getForEntity(apiAuthServiceUrl, String.class);
                log.info("User with username {} successfully authenticated", username);
                String jwt = authServerResp.getBody();
                log.info("Jwt obtained: {}", jwt);
            }
        } else {
            throw new BadCredentialsException("No credentials provided");
        }
        return true;
    }

}
