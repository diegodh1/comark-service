package com.comark.app.web;

import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.db.Pqr;
import com.comark.app.model.dto.balance.BalanceItemReportDto;
import com.comark.app.model.dto.pqr.ImmutablePqrDto;
import com.comark.app.model.dto.pqr.PqrDto;
import com.comark.app.model.enums.PqrType;
import com.comark.app.repository.IntegrationTestBase;
import com.comark.app.repository.PqrRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PqrControllerIT extends IntegrationTestBase {
    private WebTestClient webTestClient;
    @Autowired
    private PqrRepository pqrRepository;
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

    @Test
    public void shouldGetPqr() {
        String username = "username";
        List<Pqr> pqrs = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            pqrs.add(ImmutablePqr.builder()
                    .userName(username)
                    .type(PqrType.PETICION)
                    .description("description")
                    .dependency("dependency")
                    .id(UUID.randomUUID().toString())
                    .property("property")
                    .assignedTo("assignedTo")
                    .date(Instant.now().toEpochMilli())
                    .response("response")
                    .responseDate(Instant.now().toEpochMilli())
                    .build()
            );
        }
        pqrRepository.saveAll(pqrs).collectList().block();
        webTestClient.get()
                .uri("/pqr/username?pageNumber=2&pageSize=5")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(PqrDto.class)  // Expect a list of BudgetItemTaskDto
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);  // Ensure the body is not null
                    assertFalse(responseBody.isEmpty());  // Ensure the list is not empty (adjust based on test case)
                    assertEquals(5, response.getResponseBody().size());  // Ensure it contains tasks
                });

    }
}
