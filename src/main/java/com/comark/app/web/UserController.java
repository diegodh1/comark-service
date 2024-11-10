package com.comark.app.web;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.dto.activity.UserDto;
import com.comark.app.services.UserService;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UsersResource>> getAllUsers() {
        return userService.getAllUsers()
                .map(response -> ResponseEntity.ok().body(response));
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto)
                .then(Mono.fromCallable(() -> ResponseEntity.ok().build()))
                .onErrorResume(ComarkAppException.class, error -> Mono.just(ResponseEntity.status(error.getStatusCode()).body(error.getErrorMessage())));

    }
}
