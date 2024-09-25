package com.comark.app.repository;

import com.comark.app.model.db.BudgetItemTask;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BudgetItemTaskRepository extends ReactiveCrudRepository<BudgetItemTask, String> {
}
