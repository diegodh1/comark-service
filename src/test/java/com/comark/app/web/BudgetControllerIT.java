package com.comark.app.web;

import com.comark.app.model.db.ImmutableBudgetItem;
import com.comark.app.model.dto.budget.*;
import com.comark.app.model.enums.Frecuencia;
import com.comark.app.model.enums.PresupuestoTipo;
import com.comark.app.model.enums.TaskStatus;
import com.comark.app.repository.*;
import com.comark.app.services.BudgetService;
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
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    @Autowired
    private TransactBudgetRepository transactBudgetRepository;
    @Autowired
    private BudgetService budgetService;

    @BeforeEach
    void setup(){
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @AfterEach
    void clean(){
        budgetItemTaskRepository.deleteAll().block();
        budgetItemRepository.deleteAll().block();
        budgetRepository.deleteAll().block();
    }

    @Test
    public void shouldCreateBudgetFromFileSuccessfully() throws IOException {
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

    @Test
    public void shouldGetAllBudgetTaskSuccessfully() {
        List<PresupuestoItemDto> presupuestoItemDtos = new ArrayList<>();
        presupuestoItemDtos.add(ImmutablePresupuestoItemDto.builder()
                .tipo(PresupuestoTipo.GASTOS_DIVERSOS)
                .nombre("MANTENIMIENTO")
                .cuentaContableId("cuentaID")
                .frecuencia(Frecuencia.CADA_TRES_MESES)
                .fechaInicio(new Date())
                .presupuesto(28.0)
                .build());
        transactBudgetRepository.transactCreateBudget(presupuestoItemDtos, "actorId", 1500.0)
                .block();
        // Send the get request
        webTestClient.get()
                .uri("/budget/2024")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BudgetItemTaskDto.class)  // Expect a list of BudgetItemTaskDto
                .consumeWith(response -> {
                    List<BudgetItemTaskDto> budgetItemTaskDtos = response.getResponseBody();
                    assertNotNull(budgetItemTaskDtos);  // Ensure the body is not null
                    assertFalse(budgetItemTaskDtos.isEmpty());  // Ensure the list is not empty (adjust based on test case)
                    assertEquals(budgetItemTaskDtos.size(), 4);  // Ensure it contains tasks
                });
    }

    @Test
    public void shouldCompleteTaskSuccessfully() {
        // Create a task to be updated
        List<PresupuestoItemDto> presupuestoItemDtos = new ArrayList<>();
        presupuestoItemDtos.add(ImmutablePresupuestoItemDto.builder()
                .tipo(PresupuestoTipo.GASTOS_DIVERSOS)
                .nombre("MANTENIMIENTO")
                .cuentaContableId("cuentaID")
                .frecuencia(Frecuencia.CADA_TRES_MESES)
                .fechaInicio(new Date())
                .presupuesto(28.0)
                .build());
        transactBudgetRepository.transactCreateBudget(presupuestoItemDtos, "actorId", 1500.0)
                .block();

        var items = budgetItemTaskRepository.getAllByBudgetId(2024).collectList().block();

        // Send the POST request with the CompleteTaskDto
        webTestClient.post()
                .uri("/budget/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(ImmutableCompleteTaskDto.builder().id(items.get(0).id())
                        .actualAccountingAccount("accountId")
                        .amount(150.0)
                        .billId("billId")
                        .build()), CompleteTaskDto.class) // Set the body
                .exchange()
                .expectStatus()
                .isOk() // Expect HTTP 200 OK
                .expectBody(Void.class); // Expect no body in response

        // Verify that the task was updated in the repository
        budgetItemTaskRepository.findByTaskId(items.get(0).id())
                .doOnNext(task -> {
                    assertThat(task.status()).isEqualTo(TaskStatus.COMPLETED);
                    assertThat(task.actualAmount()).isEqualTo(150.0);
                    assertThat(task.actualAccountingAccount()).isEqualTo("accountId");
                    assertThat(task.billId()).isEqualTo("billId");
                })
                .block();
    }

    @Test
    public void shouldGetReportOfBudgetSuccessfully() {
        // Create a task to be updated
        List<PresupuestoItemDto> presupuestoItemDtos = new ArrayList<>();
        presupuestoItemDtos.add(ImmutablePresupuestoItemDto.builder()
                .tipo(PresupuestoTipo.GASTOS_DIVERSOS)
                .nombre("MANTENIMIENTO")
                .cuentaContableId("cuentaID")
                .frecuencia(Frecuencia.CADA_TRES_MESES)
                .fechaInicio(new Date())
                .presupuesto(1000.0)
                .build());

        presupuestoItemDtos.add(ImmutablePresupuestoItemDto.builder()
                .tipo(PresupuestoTipo.INGRESOS)
                .nombre("INGRESOS")
                .cuentaContableId("cuentaID")
                .frecuencia(Frecuencia.CADA_TRES_MESES)
                .fechaInicio(new Date())
                .presupuesto(2000.0)
                .build());
        transactBudgetRepository.transactCreateBudget(presupuestoItemDtos, "actorId", 0.0)
                .block();

        var itemTasks = budgetItemTaskRepository.getAllByBudgetId(2024).collectList().block();
        var items = budgetItemRepository.getAllByBudgetId(2024).collectList().block();

        assert itemTasks != null;
        assert items != null;
        for(var item: itemTasks){
            budgetService.completeTask(
                    ImmutableCompleteTaskDto.builder().id(item.id())
                            .actualAccountingAccount("accountId")
                            .amount(items.stream().filter(it -> it.id().equals(item.budgetItemId())).findFirst().map(ImmutableBudgetItem::expectedFrequencyAmount).orElse(0.0))
                            .billId("billId")
                            .build()
            ).block();
        }

        // Send the get request
        webTestClient.get()
                .uri("/budget//report/2024")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Map.class)
                .value(responseBody -> {
                    // Example: Assert the structure is correct
                    assertNotNull(responseBody);
                    assertInstanceOf(Map.class, responseBody);
                    Map<String, Map<Integer, String>> result = (Map<String, Map<Integer, String>>) responseBody;
                    double totalExpectedIncome = result.get("EXPECTED_INCOME").values().stream().map(Double::valueOf).max(Double::compareTo).orElse(0.0);
                    double totalActualIncome = result.get("ACTUAL_INCOME").values().stream().map(Double::valueOf).max(Double::compareTo).orElse(1.0);
                    double totalExpectedExpense = result.get("EXPECTED_EXPENSE").values().stream().map(Double::valueOf).max(Double::compareTo).orElse(0.0);
                    double totalActualExpense = result.get("ACTUAL_EXPENSE").values().stream().map(Double::valueOf).max(Double::compareTo).orElse(1.0);
                    assertEquals(totalExpectedIncome, totalActualIncome);
                    assertEquals(totalExpectedExpense, totalActualExpense);
                });
    }

    private  MultipartBodyBuilder createMultipartBodyBuilder() throws IOException {
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        multipartBodyBuilder.part("file", new ClassPathResource("budget/budget_test_file_2.xlsx"), mediaType);
        return multipartBodyBuilder;
    }

}
