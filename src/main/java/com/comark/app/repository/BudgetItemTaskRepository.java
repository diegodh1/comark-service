package com.comark.app.repository;

import com.comark.app.model.db.BudgetItemTask;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetItemTaskRepository extends ReactiveCrudRepository<BudgetItemTask, String> {
}
