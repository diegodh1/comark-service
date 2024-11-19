package com.comark.app.web;

import com.comark.app.model.db.ImmutableResidentialComplex;
import com.comark.app.model.db.ImmutableResidentialComplexItem;
import com.comark.app.model.db.ImmutableResidentialComplexItemEntity;
import com.comark.app.model.db.ResidentialComplexItemEntity;
import com.comark.app.model.dto.pqr.ImmutablePqrDto;
import com.comark.app.model.dto.residentialComplex.ImmutableResidentialComplexAdministratorDto;
import com.comark.app.model.dto.residentialComplex.ImmutableResidentialComplexDto;
import com.comark.app.model.dto.residentialComplex.ImmutableResidentialComplexItemDto;
import com.comark.app.model.dto.residentialComplex.ImmutableResidentialComplexItemEntityDto;
import com.comark.app.model.enums.IdentificationType;
import com.comark.app.model.enums.ResidentialComplexItemEntityType;
import com.comark.app.model.enums.ResidentialComplexType;
import com.comark.app.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.List;

public class ResidentialComplexControllerIT extends IntegrationTestBase {
    private WebTestClient webTestClient;
    @Autowired
    private ResidentialComplexRepository repository;
    @Autowired
    private ResidentialComplexItemRepository itemRepository;
    @Autowired
    private ResidentialComplexItemEntityRepository residentialComplexItemEntityRepository;
    @Autowired
    private ResidentialComplexAdministratorRepository residentialComplexAdministratorRepository;
    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @AfterEach
    void teardown() {
        residentialComplexItemEntityRepository.deleteAll().block();
        residentialComplexAdministratorRepository.deleteAll().block();
        itemRepository.deleteAll().block();
        repository.deleteAll().block();
    }

    @Test
    public void shouldCreateResidentialComplex() {
        // Send the POST request
        webTestClient.post()
                .uri("/residential-complex")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ImmutableResidentialComplexDto.builder()
                        .id("test")
                        .build())
                .exchange()
                .expectStatus()
                .isOk();
        var residentialComplex = repository.getResidentialComplexById("test").block();
        Assertions.assertNotNull(residentialComplex);
    }

    @Test
    public void shouldAddResidentialComplexItems() {
        repository.save(ImmutableResidentialComplex
                .builder()
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .id("test").build()).block();

        var items = List.of(ImmutableResidentialComplexItemDto.builder()
                .name("name")
                .type(ResidentialComplexType.APARTAMENTO.name())
                .buildingNumber("101A")
                .build(), ImmutableResidentialComplexItemDto.builder()
                .name(ResidentialComplexType.LOCAL.name())
                .type(ResidentialComplexType.APARTAMENTO.name())
                .buildingNumber("102A")
                .build());

        // Send the POST request
        webTestClient.post()
                .uri("/residential-complex/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(items)
                .exchange()
                .expectStatus()
                .isOk();
        var residentialComplexItems = itemRepository.findAllByResidentialComplexId("test").collectList().block();
        Assertions.assertNotNull(residentialComplexItems);
        Assertions.assertEquals(2, residentialComplexItems.size());
    }

    @Test
    public void shouldAddResidentialComplexItemEntity() {
        repository.save(ImmutableResidentialComplex
                .builder()
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .id("test").build()).block();

        itemRepository.save(ImmutableResidentialComplexItem.builder()
                .id("test")
                .residentialComplexId("test")
                .name("name")
                .type(ResidentialComplexType.APARTAMENTO)
                .buildingNumber("101A")
                .build()
        ).block();

        var entities = List.of(ImmutableResidentialComplexItemEntityDto.builder()
                .name("name")
                .isRealStateAgency(false)
                .email("test@test.com")
                .lastName("last_name")
                .type(ResidentialComplexItemEntityType.ARRENDADOR)
                .identificationType(IdentificationType.CEDULA)
                .identificationNumber("1441545")
                .phoneNumber("454")
                .build());

        // Send the POST request
        webTestClient.post()
                .uri("/residential-complex/test/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(entities)
                .exchange()
                .expectStatus()
                .isOk();

        var residentialComplexItemEntities = residentialComplexItemEntityRepository.findAllByResidentialComplexItemId("test").collectList().block();
        Assertions.assertNotNull(residentialComplexItemEntities);
        Assertions.assertEquals(1, residentialComplexItemEntities.size());
    }

    @Test
    public void shouldAddResidentialComplexAdministrator() {
        repository.save(ImmutableResidentialComplex
                .builder()
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .id("test").build()).block();

        // Send the POST request
        webTestClient.post()
                .uri("/residential-complex/admin/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ImmutableResidentialComplexAdministratorDto.builder().email("test@test.com").build())
                .exchange()
                .expectStatus()
                .isOk();
        var residentialComplexItems = residentialComplexAdministratorRepository.findAllResidentialComplexAdministratorByEmail("test@test.com").collectList().block();
        Assertions.assertNotNull(residentialComplexItems);
        Assertions.assertEquals(1, residentialComplexItems.size());
    }
    @Test
    public void shouldThrowErrorOnAddResidentialComplexAdministratorWhenComplexNotFound() {
        // Send the POST request
        webTestClient.post()
                .uri("/residential-complex/admin/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ImmutableResidentialComplexAdministratorDto.builder().email("test@test.com").build())
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
