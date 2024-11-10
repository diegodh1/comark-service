package com.comark.app.services;

import com.comark.app.client.KeycloakClient;
import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.dto.activity.UserDto;
import com.comark.app.model.dto.keycloak.ImmutableKeycloakCredentialDto;
import com.comark.app.model.dto.keycloak.ImmutableKeycloakUserRepresentationDto;
import com.comark.app.model.dto.keycloak.KeycloakTokenResponseDto;
import com.comark.app.model.dto.keycloak.KeycloakUserRepresentationDto;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {
    private final KeycloakClient keycloakClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(KeycloakClient keycloakClient) {
        this.keycloakClient = keycloakClient;
    }


    @Override
    public Mono<Void> createUser(UserDto user) {
        return keycloakClient.getToken()
                .map(KeycloakTokenResponseDto::getAccessToken)
                .flatMap(accessToken -> keycloakClient.createUser(accessToken, mapToUserRepresentationDto(user)))
                .then(Mono.empty());
    }

    private KeycloakUserRepresentationDto mapToUserRepresentationDto(UserDto user) {
        return ImmutableKeycloakUserRepresentationDto.builder()
                .email(user.email())
                .username(user.username())
                .emailVerified(true)
                .enabled(true)
                .firstName(user.name())
                .lastName(user.lastName())
                .addCredentials(ImmutableKeycloakCredentialDto.builder().value(user.password()).temporary(false).type("password").build())
                .build();
    }

    @Override
    public Mono<UsersResource> getAllUsers() {
        return Mono.empty();
    }
}
