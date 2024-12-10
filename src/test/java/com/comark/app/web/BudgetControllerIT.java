package com.comark.app.web;

import com.comark.app.model.db.ImmutableBudgetItem;
import com.comark.app.model.db.ImmutableResidentialComplex;
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
import java.time.Instant;
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
    @Autowired
    private ResidentialComplexRepository residentialComplexRepository;
    @Autowired
    private ActivityRepository activityRepository;

    @BeforeEach
    void setup(){
        residentialComplexRepository.save(ImmutableResidentialComplex.builder().id("residentialComplexId").createdAt(Instant.now().toEpochMilli()).updatedAt(Instant.now().toEpochMilli()).build()).block();
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @AfterEach
    void clean(){
        activityRepository.deleteAll().block();
        budgetItemTaskRepository.deleteAll().block();
        budgetItemRepository.deleteAll().block();
        budgetRepository.deleteAll().block();
        residentialComplexRepository.deleteAll().block();
    }

    @Test
    public void shouldCreateBudgetFromFileSuccessfully() throws IOException {
        // Send the POST request with the file
        webTestClient.post()
                .uri("/budget/upload/residentialComplexId")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipartBodyBuilder().build()))
                .exchange()
                .expectStatus()
                .isOk();

        var lastBudget = budgetRepository.getLastBudgetByResidentialComplexId("residentialComplexId").block();
        assert lastBudget != null;
        Assertions.assertEquals(lastBudget.budgetYear(), 2024);
        Assertions.assertEquals(lastBudget.residentialComplexId(), "residentialComplexId");
        Assertions.assertEquals(lastBudget.budgetAmountFromPreviousYear(), -15000000.0);
        var budgetItems = budgetItemRepository.getAllByBudgetId(lastBudget.id()).collectList().block();
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
        transactBudgetRepository.transactCreateBudget(presupuestoItemDtos,"residentialComplexId", "actorId", 1500.0)
                .block();
        var lastBudget = budgetRepository.getLastBudgetByResidentialComplexId("residentialComplexId").block();
        Assertions.assertNotNull(lastBudget);
        // Send the get request
        webTestClient.get()
                .uri("/budget/" + lastBudget.id())
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
        transactBudgetRepository.transactCreateBudget(presupuestoItemDtos,"residentialComplexId", "actorId", 1500.0)
                .block();

        var lastBudget = budgetRepository.getLastBudgetByResidentialComplexId("residentialComplexId").block();
        Assertions.assertNotNull(lastBudget);
        var items = budgetItemTaskRepository.getAllByBudgetId(lastBudget.id()).collectList().block();

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
        transactBudgetRepository.transactCreateBudget(presupuestoItemDtos,"residentialComplexId", "actorId", 0.0)
                .block();
        var lastBudget = budgetRepository.getLastBudgetByResidentialComplexId("residentialComplexId").block();
        Assertions.assertNotNull(lastBudget);
        var itemTasks = budgetItemTaskRepository.getAllByBudgetId(lastBudget.id()).collectList().block();
        var items = budgetItemRepository.getAllByBudgetId(lastBudget.id()).collectList().block();

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
                .uri("/budget/report/" + lastBudget.id())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Map.class)
                .value(responseBody -> {
                    // Example: Assert the structure is correct
                    assertNotNull(responseBody);
                    assertInstanceOf(Map.class, responseBody);
                    Map<String, List<ReportValueDto>> result = (Map<String, List<ReportValueDto>>) responseBody;
                    assertFalse(result.isEmpty());
                });
    }

    private  MultipartBodyBuilder createMultipartBodyBuilder() throws IOException {
        final MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        multipartBodyBuilder.part("file", new ClassPathResource("budget/budget_test_file_2.xlsx"), mediaType);
        return multipartBodyBuilder;
    }

}
