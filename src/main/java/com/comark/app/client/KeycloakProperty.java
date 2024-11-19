package com.comark.app.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperty {
    private String serverUrl;
    private String clientId;
    private String clientSecret;
    private String realm;
    private String username;
    private String password;
    private String issuer;
}
