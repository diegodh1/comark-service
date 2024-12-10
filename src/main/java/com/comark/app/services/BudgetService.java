package com.comark.app.services;

import com.comark.app.model.Success;
import com.comark.app.model.db.BudgetItemTask;
import com.comark.app.model.dto.budget.BudgetItemTaskDto;
import com.comark.app.model.dto.budget.CompleteTaskDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BudgetService {
    Mono<Success> upsertBudget(byte[] file, String residentialComplexId);
    Mono<List<BudgetItemTaskDto>> getAllBudgetItemTasks(String budgetId);
    Mono<BudgetItemTask> completeTask(CompleteTaskDto completeTaskDto);
}
