package com.comark.app.web;

import com.comark.app.repository.BudgetItemRepository;
import com.comark.app.repository.BudgetItemTaskRepository;
import com.comark.app.repository.BudgetRepository;
import com.comark.app.repository.IntegrationTestBase;
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

public class BudgetControllerIT extends IntegrationTestBase {

    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetItemRepository budgetItemRepository;
    @Autowired
    private BudgetItemTaskRepository budgetItemTaskRepository;

    @BeforeEach
    void setup(){
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void shouldCreateBudgetFromFileSuccessfully() throws IOException {
        // Use a sample Excel file for testing (e.g., stored in the resources folder)
        ClassPathResource fileResource = new ClassPathResource("");

        // Send the POST request with the file
        webTestClient.post()
                .uri("/budget/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipartBodyBuilder().build()))
                .exchange()
                .expectStatus()
                .isOk();

        var budget = budgetRepository.getBudgetById(2024).block();
        assert budget != null;
        Assertions.assertEquals(budget.id(), 2024);
        Assertions.assertEquals(budget.budgetAmountFromPreviousYear(), -15000000.0);
        var budgetItems = budgetItemRepository.getAllByBudgetId(2024).collectList().block();
        assert budgetItems != null;
        Assertions.assertEquals(budgetItems.size(), 15);
    }

    private  MultipartBodyBuilder createMultipartBodyBuilder() throws IOException {
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        multipartBodyBuilder.part("file", new ClassPathResource("budget/budget_test_file_2.xlsx"), mediaType);
        return multipartBodyBuilder;
    }

}
