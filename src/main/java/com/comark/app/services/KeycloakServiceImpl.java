package com.comark.app.services;

import com.comark.app.client.KeycloakClient;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
public class KeycloakServiceImpl implements KeycloakService {
    private WebClient keycloakClient;

    @Override
    public Mono<String> getToken() {
        return keycloakClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(UriComponentsBuilder.newInstance()
                        .queryParam("grant_type", "password")
                        .queryParam("username", "admin")
                        .queryParam("password", "admin")
                        .queryParam("client_id", "admin-cli")
                        .queryParam("client_secret", "0UmEnWsDRJ65gc5UQylWUj1stI6QN1Bx")
                        .build()
                        .toUriString().substring(1)) // Remove the "?" at the start of the query string
                .retrieve()
                .bodyToMono(String.class);
    }
}
