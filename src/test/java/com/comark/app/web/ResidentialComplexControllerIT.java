package com.comark.app.web;

import com.comark.app.model.db.*;
import com.comark.app.model.dto.pqr.ImmutablePqrDto;
import com.comark.app.model.dto.residentialComplex.*;
import com.comark.app.model.enums.EventStatus;
import com.comark.app.model.enums.IdentificationType;
import com.comark.app.model.enums.ResidentialComplexItemEntityType;
import com.comark.app.model.enums.ResidentialComplexType;
import com.comark.app.repository.*;
import com.comark.app.services.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
    private ResidentialComplexItemEventRepository residentialComplexItemEventRepository;
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
        residentialComplexItemEventRepository.deleteAll().block();
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
        var residentialComplexItems = itemRepository.findAllByResidentialComplexId("test", "101A").collectList().block();
        Assertions.assertNotNull(residentialComplexItems);
        Assertions.assertEquals(1, residentialComplexItems.size());
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
                .percentage(0.0)
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
    public void shouldAddResidentialComplexItemEvent() {
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
                .percentage(0.0)
                .build()
        ).block();

        // Send the POST request
        webTestClient.post()
                .uri("/residential-complex/event/test/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ImmutableResidentialComplexEventDto.builder()
                        .name("name")
                        .organizerId("organizerId")
                        .restriction("restriction")
                        .description("description")
                        .startDateTime("25/12/2024 14:30:00")
                        .endDateTime("25/12/2024 16:30:00")
                        .build())
                .exchange()
                .expectStatus()
                .isOk();

        var events = residentialComplexItemEventRepository.findAllResidentialComplexItemEventsByStatus(EventStatus.PENDIENTE.name(), "test").collectList().block();
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
    }

    @Test
    public void shouldThrowErrorWhenResidentialComplexItemEventOverlaps() {
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
                .percentage(0.0)
                .build()
        ).block();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss");
        LocalDateTime startLocalDateTime = LocalDateTime.parse("25/12/2024:14:30:00", formatter);
        LocalDateTime endLocalDateTime = LocalDateTime.parse("25/12/2024:16:30:00", formatter);

        residentialComplexItemEventRepository.save(ImmutableResidentialComplexItemEvent.builder()
                .residentialComplexItemId("test")
                .id(UUID.randomUUID().toString())
                .residentialComplexId("test")
                .organizerId("organizerId")
                .startDateTime(startLocalDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli())
                .endDateTime(endLocalDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli())
                .description("desc")
                .eventStatus(EventStatus.APROVADO)
                .restrictions("restrictions")
                .name("name")
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build()).block();

        // Send the POST request
        webTestClient.post()
                .uri("/residential-complex/event/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ImmutableResidentialComplexEventDto.builder()
                        .name("name")
                        .organizerId("organizerId")
                        .restriction("restriction")
                        .description("description")
                        .startDateTime("25/12/2024:15:30:00")
                        .endDateTime("25/12/2024:17:30:00")
                        .build())
                .exchange()
                .expectStatus()
                .isBadRequest();
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
