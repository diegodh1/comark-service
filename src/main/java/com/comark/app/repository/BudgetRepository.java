package com.comark.app.repository;

import com.comark.app.model.db.Budget;
import com.comark.app.model.db.ImmutableBudget;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BudgetRepository extends ReactiveCrudRepository<Budget, Integer> {

    @Query("SELECT * FROM budget WHERE id = :id")
    Mono<ImmutableBudget> getBudgetById(@Param("id") String id);

    @Query("SELECT * FROM budget WHERE residential_complex_id = :residentialComplexId ORDER BY created_at DESC LIMIT 1")
    Mono<ImmutableBudget> getLastBudgetByResidentialComplexId(String residentialComplexId);
}
