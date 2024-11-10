package com.comark.app.model.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableKeycloakResponseError.class)
@JsonDeserialize(as = ImmutableKeycloakResponseError.class)
public interface KeycloakResponseError {
    @JsonProperty("errorMessage")
    String errorMessage();
}
