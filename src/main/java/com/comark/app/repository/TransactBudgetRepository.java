package com.comark.app.repository;

import com.comark.app.model.Success;
import com.comark.app.model.dto.budget.BudgetItemTaskDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import lombok.NonNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransactBudgetRepository {
    Mono<Success> transactCreateBudget(List<PresupuestoItemDto> budgetItems, @NotBlank String residentialComplexId, @NotBlank String actorId, @NonNull Double budgetAmountFromPreviousYear);
    Mono<List<BudgetItemTaskDto>> getAllBudgetItemTasks(@NonNull String budgetId);
}
