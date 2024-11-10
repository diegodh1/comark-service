package com.comark.app.services;

import reactor.core.publisher.Mono;

public interface KeycloakService {
    Mono<String> getToken();
}
