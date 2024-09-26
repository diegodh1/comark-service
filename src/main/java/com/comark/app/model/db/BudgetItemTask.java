package com.comark.app.model.db;

import com.comark.app.model.enums.TaskStatus;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;

import javax.annotation.Nullable;

@Value.Immutable
public interface BudgetItemTask {
    @Id
    String id();
    Integer budgetId();// Redundant key for querying faster
    String budgetItemId(); // Foreign key
    Double actualAmount();
    @Nullable
    String actualAccountingAccount();
    Long scheduledDate();
    @Nullable
    String updatedBy();
    TaskStatus status();
    Long createdAt();
    Long updatedAt();
}
