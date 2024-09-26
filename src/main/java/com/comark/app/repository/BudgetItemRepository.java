package com.comark.app.repository;

import com.comark.app.model.db.BudgetItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetItemRepository extends ReactiveCrudRepository<BudgetItem, String> {
}
