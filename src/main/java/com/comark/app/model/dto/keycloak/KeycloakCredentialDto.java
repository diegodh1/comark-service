package com.comark.app.model.dto.keycloak;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableKeycloakCredentialDto.class)
@JsonDeserialize(as = ImmutableKeycloakCredentialDto.class)
public interface KeycloakCredentialDto {
    String value();
    Boolean temporary();
    String type();
}
