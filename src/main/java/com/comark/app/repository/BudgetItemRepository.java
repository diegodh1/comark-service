package com.comark.app.repository;

import com.comark.app.model.db.BudgetItem;
import com.comark.app.model.db.ImmutableBudgetItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BudgetItemRepository extends ReactiveCrudRepository<BudgetItem, String> {
    @Query("SELECT * FROM budget_item WHERE budget_id = :id")
    Flux<ImmutableBudgetItem> getAllByBudgetId(String id);
}
