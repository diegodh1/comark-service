package com.comark.app.repository;

import com.comark.app.model.db.BudgetItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BudgetItemRepository extends ReactiveCrudRepository<BudgetItem, String> {
}
