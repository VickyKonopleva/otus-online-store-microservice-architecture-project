package com.konopleva.crudeapp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class JwtValidator {
    @Value("${jwt.secret}")
    private String secret;

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            if (null == SecurityContextHolder.getContext().getAuthentication()) {
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                new UsernamePasswordAuthenticationToken(claims.getSubject(), new ArrayList<>())
                        );
                log.info("User with login {} successfully authenticated", claims.getSubject());
            }
            log.info("Jwt successfully parsed");
            return true;
        } catch (Exception e) {
            return false; // Token is invalid or expired
        }
    }
}
