package com.konopleva.crudeapp.restClient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class ApiAuthRestTemplate {
    private final String username;
    private final String password;
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(username, password)
        );

        return restTemplate;
    }
}
