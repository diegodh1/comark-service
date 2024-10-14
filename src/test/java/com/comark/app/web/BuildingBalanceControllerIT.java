package com.comark.app.web;

import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.db.ImmutableBuildingBalance;
import com.comark.app.model.dto.balance.BalanceDto;
import com.comark.app.model.dto.balance.ImmutableBalanceDto;
import com.comark.app.model.dto.budget.ImmutablePresupuestoItemDto;
import com.comark.app.model.enums.Frecuencia;
import com.comark.app.model.enums.PresupuestoTipo;
import com.comark.app.repository.BuildingBalanceRepository;
import com.comark.app.repository.IntegrationTestBase;
import com.comark.app.services.BalanceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingBalanceControllerIT extends IntegrationTestBase {
    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BuildingBalanceRepository buildingBalanceRepository;

    @BeforeEach
    void setup(){
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @AfterEach
    void clean(){
        buildingBalanceRepository.deleteAll().block();
    }

    @Test
    public void shouldCreateBuildingBalanceFromFileSuccessfully() throws IOException {
        // Send the POST request with the file
        webTestClient.post()
                .uri("/balance/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipartBodyBuilder().build()))
                .exchange()
                .expectStatus()
                .isOk();

        var response = buildingBalanceRepository.getAllApartments().collectList().block();
        assert response != null;
        Assertions.assertEquals(response.size(), 5);
    }

    @Test
    public void shouldGetAllBalanceReports() {
        List<BuildingBalance> balanceList = new ArrayList<>();
        balanceList.add(
                ImmutableBuildingBalance.builder()
                        .id("test")
                        .apartmentNumber("1")
                        .administrationCharge(1.0)
                        .monthCharge(2.0)
                        .lastPaid(3.0)
                        .totalToPaid(4.0)
                        .finalCharge(5.0)
                        .legalCharge(5.0)
                        .interestCharge(0.0)
                        .lastBalance(0.0)
                        .date(Instant.now().toEpochMilli())
                        .interestRate(0.0)
                        .otherCharge(0.0)
                        .interestBalance(0.0)
                        .penaltyCharge(0.0)
                        .additionalCharge(0.0)
                        .discount(0.0)
                        .build()
        );

        balanceList.add(
                ImmutableBuildingBalance.builder()
                        .id("test2")
                        .apartmentNumber("2")
                        .administrationCharge(1.0)
                        .monthCharge(2.0)
                        .lastPaid(3.0)
                        .totalToPaid(4.0)
                        .finalCharge(5.0)
                        .legalCharge(5.0)
                        .lastBalance(0.0)
                        .interestCharge(0.0)
                        .date(Instant.now().toEpochMilli())
                        .interestRate(0.0)
                        .otherCharge(0.0)
                        .interestBalance(0.0)
                        .penaltyCharge(0.0)
                        .additionalCharge(0.0)
                        .discount(0.0)
                        .build()
        );

        buildingBalanceRepository.saveAll(balanceList).collectList().block();

        // exchange
        webTestClient.get()
                .uri("/balance")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BuildingBalance.class)  // Expect a list of BudgetItemTaskDto
                .consumeWith(response -> {
                    List<BuildingBalance> responseBody = response.getResponseBody();
                    assertNotNull(responseBody);  // Ensure the body is not null
                    assertFalse(responseBody.isEmpty());  // Ensure the list is not empty (adjust based on test case)
                    assertEquals(responseBody.size(), 2);  // Ensure it contains tasks
                });
    }

    @Test
    public void shouldGetAllBalanceReportsByApartmentNumber() {
        List<BuildingBalance> balanceList = new ArrayList<>();
        balanceList.add(
                ImmutableBuildingBalance.builder()
                        .id("test")
                        .apartmentNumber("1")
                        .administrationCharge(1.0)
                        .monthCharge(2.0)
                        .lastPaid(3.0)
                        .totalToPaid(4.0)
                        .finalCharge(5.0)
                        .lastBalance(0.0)
                        .legalCharge(5.0)
                        .interestCharge(0.0)
                        .date(Instant.now().toEpochMilli())
                        .interestRate(0.0)
                        .otherCharge(0.0)
                        .interestBalance(0.0)
                        .penaltyCharge(0.0)
                        .additionalCharge(0.0)
                        .discount(0.0)
                        .build()
        );

        balanceList.add(
                ImmutableBuildingBalance.builder()
                        .id("test2")
                        .apartmentNumber("2")
                        .administrationCharge(1.0)
                        .monthCharge(2.0)
                        .lastPaid(3.0)
                        .totalToPaid(4.0)
                        .lastBalance(0.0)
                        .finalCharge(5.0)
                        .legalCharge(5.0)
                        .interestCharge(0.0)
                        .date(Instant.now().toEpochMilli())
                        .interestRate(0.0)
                        .otherCharge(0.0)
                        .interestBalance(0.0)
                        .penaltyCharge(0.0)
                        .additionalCharge(0.0)
                        .discount(0.0)
                        .build()
        );

        buildingBalanceRepository.saveAll(balanceList).collectList().block();

        // exchange
        webTestClient.get()
                .uri("/balance/2")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BuildingBalance.class)  // Expect a list of BudgetItemTaskDto
                .consumeWith(response -> {
                    List<BuildingBalance> responseBody = response.getResponseBody();
                    assertNotNull(responseBody);  // Ensure the body is not null
                    assertFalse(responseBody.isEmpty());  // Ensure the list is not empty (adjust based on test case)
                    assertEquals(responseBody.size(), 1);  // Ensure it contains tasks
                });
    }

    @Test
    public void shouldGetAccountBalanceByApartmentNumber() {
        List<BuildingBalance> balanceList = new ArrayList<>();
        balanceList.add(
                ImmutableBuildingBalance.builder()
                        .id("test")
                        .apartmentNumber("1")
                        .administrationCharge(1.0)
                        .monthCharge(2.0)
                        .lastPaid(3.0)
                        .totalToPaid(4.0)
                        .finalCharge(5.0)
                        .lastBalance(0.0)
                        .legalCharge(5.0)
                        .interestCharge(0.0)
                        .date(Instant.now().toEpochMilli())
                        .interestRate(0.0)
                        .otherCharge(0.0)
                        .interestBalance(0.0)
                        .penaltyCharge(0.0)
                        .additionalCharge(0.0)
                        .discount(0.0)
                        .build()
        );

        balanceList.add(
                ImmutableBuildingBalance.builder()
                        .id("test2")
                        .apartmentNumber("2")
                        .administrationCharge(1.0)
                        .monthCharge(2.0)
                        .lastPaid(3.0)
                        .totalToPaid(4.0)
                        .lastBalance(0.0)
                        .finalCharge(5000000.25)
                        .legalCharge(5.0)
                        .interestCharge(0.0)
                        .date(Instant.now().toEpochMilli())
                        .interestRate(0.0)
                        .otherCharge(0.0)
                        .interestBalance(0.0)
                        .penaltyCharge(0.0)
                        .additionalCharge(0.0)
                        .discount(0.0)
                        .build()
        );

        buildingBalanceRepository.saveAll(balanceList).collectList().block();

        // exchange
        webTestClient.get()
                .uri("/balance/accountBalance/2")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody() // Expect a list of BudgetItemTaskDto
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                });
    }


    private MultipartBodyBuilder createMultipartBodyBuilder() throws IOException {
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        multipartBodyBuilder.part("file", new ClassPathResource("building_balance/building_test_file.xlsx"), mediaType);
        return multipartBodyBuilder;
    }
}
