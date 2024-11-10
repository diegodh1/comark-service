package com.comark.app.services;

import com.comark.app.model.Success;
import com.comark.app.model.dto.activity.UserDto;
import org.keycloak.admin.client.resource.UsersResource;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Void> createUser(UserDto user);
    Mono<UsersResource> getAllUsers();
}
