package com.comark.app.repository;

import com.comark.app.model.db.BudgetItemTask;
import com.comark.app.model.db.ImmutableBudgetItemTask;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BudgetItemTaskRepository extends ReactiveCrudRepository<BudgetItemTask, String> {
    @Query("SELECT * FROM budget_item_task WHERE budget_id = :id")
    Flux<ImmutableBudgetItemTask> getAllByBudgetId(Integer id);
}
