package com.comark.app.repository;

import com.comark.app.model.db.BudgetItemTask;
import com.comark.app.model.db.ImmutableBudgetItemTask;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BudgetItemTaskRepository extends ReactiveCrudRepository<BudgetItemTask, String> {
    @Query("SELECT * FROM budget_item_task WHERE budget_id = :id")
    Flux<ImmutableBudgetItemTask> getAllByBudgetId(Integer id);

    @Query("SELECT * FROM budget_item_task WHERE status = 'SCHEDULED' AND budget_id = :id")
    Flux<ImmutableBudgetItemTask> findByStatusIsScheduled(Integer id);

    @Query("update budget_item_task set status = :status where id = :id")
    Mono<ImmutableBudgetItemTask> updateStatus(String status, String id);

    @Query("UPDATE budget_item_task " +
            "SET status = :status, " +
            "actual_amount = :actualAmount, " +
            "actual_accounting_account = :actualAccountingAccount, " +
            "bill_id = :billId " +
            "WHERE id = :id")
    Mono<ImmutableBudgetItemTask> updateBudgetItemTask(
            String status,
            Double actualAmount,
            String actualAccountingAccount,
            String billId,
            String id);

    @Query("SELECT * FROM budget_item_task WHERE id = :id")
    Mono<ImmutableBudgetItemTask> findByTaskId(String id);
}
