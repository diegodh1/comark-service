package com.comark.app.web;

import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.dto.pqr.ImmutablePqrDto;
import com.comark.app.model.enums.PqrType;
import com.comark.app.repository.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class PqrControllerIT extends IntegrationTestBase {
    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext context;
    @BeforeEach
    void setup(){
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void shouldCreatePqr() {
        // Send the POST request with the file
        webTestClient.post()
                .uri("/pqr")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ImmutablePqrDto.builder().property("property")
                        .description("description")
                        .assignedTo("assignedTo")
                        .type("PETICION")
                        .dependency("dependency")
                        .userName("username")
                        .build())
                .exchange()
                .expectStatus()
                .isOk();
    }
}
