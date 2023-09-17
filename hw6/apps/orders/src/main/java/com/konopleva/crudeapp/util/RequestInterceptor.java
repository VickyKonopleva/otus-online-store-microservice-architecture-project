package com.konopleva.crudeapp.util;

import com.konopleva.crudeapp.restClient.ApiAuthRestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Base64;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    @Value("${outbound.authProviderUrl}")
    private String apiAuthServiceUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // Decode the Base64-encoded username and password
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));

            // Split the credentials into username and password
            String[] parts = credentials.split(":");
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];

                // You can now access the username and password here
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>())
                        );
                var restTemplate = new ApiAuthRestTemplate(username, password);
                restTemplate.restTemplate().getForEntity(apiAuthServiceUrl, String.class);

            }
        } else {
            throw new BadCredentialsException("No credentials provided");
        }
        return true;
    }

}
