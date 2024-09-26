package com.comark.app.repository;

import com.comark.app.model.db.ImmutableBudget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

public class TransactBudgetRepositoryIT extends IntegrationTestBase{
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private TransactBudgetRepository transactBudgetRepository;

    @Test
    void shouldCreateBudget() {
        var budget = budgetRepository.save(ImmutableBudget.builder()
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .id(2024)
                .budgetAmountFromPreviousYear(0.0)
                .actorId("ACTOR")
                .build()).block();
        assert budget != null;
        Assertions.assertEquals(budget.id(), 2024);
    }
}
