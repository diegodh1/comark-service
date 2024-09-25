package com.comark.app.services;

import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.repository.BudgetRepository;
import com.comark.app.services.util.BudgetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BudgetServiceImpl implements BudgetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BudgetServiceImpl.class);
    private final BudgetUtil budgetUtil;
    private final BudgetRepository budgetRepository;

    public BudgetServiceImpl(BudgetUtil budgetUtil, BudgetRepository budgetRepository) {
        this.budgetUtil = budgetUtil;
        this.budgetRepository = budgetRepository;
    }

    @Override
    public Mono<Success> upsertBudget(byte[] file) {
        return budgetUtil.loadBudgetFromFile(file)
                .flatMap(items -> Mono.just(ImmutableSuccess.builder().build()));

    }
}
