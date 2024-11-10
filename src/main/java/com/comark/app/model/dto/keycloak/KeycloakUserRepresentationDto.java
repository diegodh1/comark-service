package com.comark.app.model.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableKeycloakUserRepresentationDto.class)
@JsonDeserialize(as = ImmutableKeycloakUserRepresentationDto.class)
public interface KeycloakUserRepresentationDto {
    String username();
    @JsonProperty("firstName")
    String firstName();
    @JsonProperty("lastName")
    String lastName();
    @JsonProperty("email")
    String email();
    @JsonProperty("emailVerified")
    Boolean emailVerified();
    Boolean enabled();
    List<KeycloakCredentialDto> credentials();
}
