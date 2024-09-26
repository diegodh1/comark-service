package com.comark.app.model.db;

import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;

@Value.Immutable
public interface BudgetItem {
    @Id
    String id();
    Integer budgetId(); // Foreign key
    String type();
    @Embedded.Empty
    String name();
    String detail();
    Double amount();
    Integer frequency();
    Double expectedFrequencyAmount();
    Long initialDate();
    String accountingAccount();
    Long createdAt();
    Long updatedAt();

    // One-to-Many relationship with BudgetItemTask
    @MappedCollection(idColumn = "budgetItemId")
    @Transient // Avoid cyclic references during serialization
    Set<BudgetItemTask> budgetItemTasks();
}
