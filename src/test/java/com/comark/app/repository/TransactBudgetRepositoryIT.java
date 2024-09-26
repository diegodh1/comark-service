package com.comark.app.repository;

import com.comark.app.model.db.ImmutableBudget;
import com.comark.app.model.dto.budget.ImmutablePresupuestoItemDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.model.enums.Frecuencia;
import com.comark.app.model.enums.PresupuestoTipo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactBudgetRepositoryIT extends IntegrationTestBase{
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetItemRepository budgetItemRepository;
    @Autowired
    private BudgetItemTaskRepository budgetItemTaskRepository;
    @Autowired
    private TransactBudgetRepository transactBudgetRepository;

    @AfterEach
    void clean(){
        budgetItemTaskRepository.deleteAll().block();
        budgetItemRepository.deleteAll().block();
        budgetRepository.deleteAll().block();
    }

    @Test
    void shouldCreateBudget() {
        long creationDate = Instant.now().toEpochMilli();
        var budget = budgetRepository.save(ImmutableBudget.builder()
                .createdAt(creationDate)
                .updatedAt(creationDate)
                .id(2024)
                .budgetAmountFromPreviousYear(0.0)
                .actorId("actor_id")
                .build()).block();
        assert budget != null;
        Assertions.assertEquals(2024, budget.id());
        Assertions.assertEquals("actor_id", budget.actorId());
        Assertions.assertEquals(creationDate, budget.createdAt());
        Assertions.assertEquals(creationDate, budget.updatedAt());
    }

    @Test
    void shouldCreateBudgetAndItemTasks() {
        List<PresupuestoItemDto> presupuestoItemDtos = new ArrayList<>();
        presupuestoItemDtos.add(ImmutablePresupuestoItemDto.builder()
                .tipo(PresupuestoTipo.GASTOS_DIVERSOS)
                .nombre("MANTENIMIENTO")
                .cuentaContableId("cuentaID")
                .frecuencia(Frecuencia.CADA_TRES_MESES)
                .fechaInicio(new Date())
                .presupuesto(28.0)
                .build());
        var response = transactBudgetRepository.transactCreateBudget(presupuestoItemDtos, "actorId", 1500.0)
                .block();
        Assertions.assertNotNull(response);
        Assertions.assertTrue(true);

        var tasks = budgetItemTaskRepository.getAllByBudgetId(2024)
                .collectList().block();
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(4, tasks.size());
        var scheduledDates = tasks.stream()
                .map(task -> Instant.ofEpochMilli(task.scheduledDate()).atZone(ZoneOffset.UTC).toLocalDate())
                .toList();
        var firstDate = scheduledDates.get(0);
        var secondDate = scheduledDates.get(1);
        var thirdDate = scheduledDates.get(2);
        var fourDate = scheduledDates.get(3);
        var expectedMonthIncrease = 3;
        // assert expected dates
        Assertions.assertEquals(expectedMonthIncrease, ChronoUnit.MONTHS.between(firstDate, secondDate));
        Assertions.assertEquals(expectedMonthIncrease, ChronoUnit.MONTHS.between(secondDate, thirdDate));
        Assertions.assertEquals(expectedMonthIncrease, ChronoUnit.MONTHS.between(thirdDate, fourDate));
    }

}
