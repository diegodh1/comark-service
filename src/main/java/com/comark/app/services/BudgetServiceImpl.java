package com.comark.app.services;

import com.comark.app.model.Success;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.repository.TransactBudgetRepositoryImpl;
import com.comark.app.services.util.BudgetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BudgetServiceImpl.class);
    private final BudgetUtil budgetUtil;
    private final TransactBudgetRepositoryImpl transactBudgetRepository;

    public BudgetServiceImpl(BudgetUtil budgetUtil, TransactBudgetRepositoryImpl transactBudgetRepository) {
        this.budgetUtil = budgetUtil;
        this.transactBudgetRepository = transactBudgetRepository;
    }

    @Override
    public Mono<Success> upsertBudget(byte[] file, String actorId) {
        return budgetUtil.loadBudgetFromFile(file)
                .flatMap(budgetList -> Mono.just(Tuples.of(
                        budgetList.stream().filter(it -> !it.getNombre().equals("EXCENDENTES AÑOS ANTERIORES")).collect(Collectors.toList()),
                        getBudgetFromPreviousYear(budgetList))))
                .flatMap(tuple2 -> transactBudgetRepository.transactCreateBudget(tuple2.getT1(), actorId, tuple2.getT2()))
                .doOnError(error -> LOGGER.error(error.getMessage(), error));
    }

    private double getBudgetFromPreviousYear(List<PresupuestoItemDto> items){
        return items.stream().filter(item -> item.getNombre().equals("EXCENDENTES AÑOS ANTERIORES"))
                .findFirst()
                .map(PresupuestoItemDto::getPresupuesto)
                .orElse(0.0);
    }
}
