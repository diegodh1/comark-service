package com.comark.app.model.dto.budget;

import com.comark.app.model.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(as = ImmutableBudgetItemTaskDto.class)
@JsonDeserialize(as = ImmutableBudgetItemTaskDto.class)
public interface BudgetItemTaskDto {
    String id();
    String name();
    @Nullable
    String details();
    @Nullable
    Double actualAmount();
    Double expectedAmount();
    @Nullable
    String actualAccountingAccount();
    String expectedAccountingAccount();
    String scheduledDate();
    String status();
}
