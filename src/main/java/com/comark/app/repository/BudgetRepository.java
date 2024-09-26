package com.comark.app.repository;

import com.comark.app.model.db.Budget;
import com.comark.app.model.db.ImmutableBudget;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BudgetRepository extends ReactiveCrudRepository<Budget, Integer> {

    @Query("SELECT * FROM budget WHERE id = :id")
    Mono<ImmutableBudget> getBudgetById(@Param("id") Integer id);
}
