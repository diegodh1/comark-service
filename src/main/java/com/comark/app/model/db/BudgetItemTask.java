package com.comark.app.model.db;

import com.comark.app.model.enums.TaskStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nullable;

@Table("budget_item_task")
@Value.Immutable
@JsonSerialize(as = ImmutableBudgetItemTask.class)
@JsonDeserialize(as = ImmutableBudgetItemTask.class)
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
    String billId();
    @Nullable
    String updatedBy();
    TaskStatus status();
    Long createdAt();
    Long updatedAt();
}
