package com.comark.app.model.db;

import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("Budget")
@Value.Immutable
public interface Budget {
    @Id
    Integer id();
    String actorId();
    Double budgetAmountFromPreviousYear();
    Long createdAt();
    Long updatedAt();

    // One-to-Many relationship with BudgetItem
    @MappedCollection(idColumn = "budgetId")
    @Transient
    Set<BudgetItem> budgetItems();
}
