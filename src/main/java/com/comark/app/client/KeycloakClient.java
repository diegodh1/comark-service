package com.comark.app.client;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.dto.keycloak.KeycloakResponseError;
import com.comark.app.model.dto.keycloak.KeycloakTokenResponseDto;
import com.comark.app.model.dto.keycloak.KeycloakUserRepresentationDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class KeycloakClient {
    private WebClient webClient;
    private KeycloakProperty keycloakProperty;

    public KeycloakClient(KeycloakProperty keycloakProperty) {
        this.keycloakProperty = keycloakProperty;
        this.webClient = WebClient.builder().baseUrl(keycloakProperty.getServerUrl()).build();
    }

    public Mono<KeycloakTokenResponseDto> getToken() {
        return webClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(UriComponentsBuilder.newInstance()
                        .queryParam("grant_type", "password")
                        .queryParam("username", keycloakProperty.getUsername())
                        .queryParam("password", keycloakProperty.getPassword())
                        .queryParam("client_id", keycloakProperty.getClientId())
                        .queryParam("client_secret", keycloakProperty.getClientSecret())
                        .build()
                        .toUriString().substring(1))
                .retrieve()
                .bodyToMono(KeycloakTokenResponseDto.class);
    }

    public Mono<Void> createUser(String token, KeycloakUserRepresentationDto keycloakUserRepresentationDto) {
        return webClient.post()
                .uri(String.format("%s/admin/realms/%s/users",keycloakProperty.getServerUrl(), keycloakProperty.getRealm()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .bodyValue(keycloakUserRepresentationDto)
                .retrieve()
                .onStatus(status -> status.value() != 201, response -> response.bodyToMono(KeycloakResponseError.class)
                        .flatMap(error -> Mono.error(new ComarkAppException(
                                response.statusCode().value(),
                                error.errorMessage()
                        ))))
                .bodyToMono(Void.class);
    }
}
