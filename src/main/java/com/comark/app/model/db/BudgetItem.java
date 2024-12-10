package com.comark.app.model.db;

import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nullable;

@Table("budget_item")
@Value.Immutable
public interface BudgetItem {
    @Id
    String id();
    String budgetId(); // Foreign key
    String type();
    String name();
    @Nullable
    String detail();
    Double amount();
    Integer frequency();
    Double expectedFrequencyAmount();
    Long initialDate();
    String accountingAccount();
    Long createdAt();
    Long updatedAt();
}
