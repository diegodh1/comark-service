package com.comark.app.repository;

import com.comark.app.model.db.Budget;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BudgetRepository extends ReactiveCrudRepository<Budget, Integer> {
}
