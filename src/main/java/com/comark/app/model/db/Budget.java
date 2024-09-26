package com.comark.app.model.db;

import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("budget")
@Value.Immutable
public interface Budget {
    @Id
    Integer id();
    String actorId();
    Double budgetAmountFromPreviousYear();
    Long createdAt();
    Long updatedAt();
}