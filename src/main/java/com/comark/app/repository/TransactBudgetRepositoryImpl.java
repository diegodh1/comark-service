package com.comark.app.repository;

import com.comark.app.mapper.BudgetItemMapper;
import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.db.*;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.model.enums.TaskStatus;
import lombok.NonNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TransactBudgetRepositoryImpl implements TransactBudgetRepository {
    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final BudgetItemTaskRepository budgetItemTaskRepository;
    private final BudgetItemMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactBudgetRepositoryImpl.class);


    public TransactBudgetRepositoryImpl(BudgetRepository budgetRepository, BudgetItemRepository budgetItemRepository, BudgetItemTaskRepository budgetItemTaskRepository, BudgetItemMapper mapper) {
        this.budgetRepository = budgetRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetItemTaskRepository = budgetItemTaskRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Mono<Success> transactCreateBudget(List<PresupuestoItemDto> budgetItems, @NotBlank String actorId, @NonNull Double budgetAmountFromPreviousYear) {
        return createBudget(actorId, budgetAmountFromPreviousYear)
                .flatMap(budget -> batchInsertBudgetItem(budget, budgetItems))
                .flatMap(this::batchInsertBudgetItemTask)
                .doOnError(error -> LOGGER.error(error.getMessage(), error));
    }

    private Mono<Budget> createBudget(String actorId, Double budgetAmountFromPreviousYear){
        return budgetRepository.save(ImmutableBudget.builder()
                .id(LocalDate.now().getYear())
                .actorId(actorId)
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .budgetAmountFromPreviousYear(budgetAmountFromPreviousYear)
                .build());
    }

    private Mono<List<ImmutableBudgetItem>> batchInsertBudgetItem(Budget budget, List<PresupuestoItemDto> budgetItems) {
        return Mono.just(budgetItems
                        .stream()
                        .map(item -> ImmutableBudgetItem.copyOf(mapper.fromPresupuestoItemDto(item)).withBudgetId(budget.id()))
                        .collect(Collectors.toSet()))
                .flatMap(listItems -> Mono.defer(() -> budgetItemRepository.saveAll(listItems).collectList()));

    }

    private Mono<Success> batchInsertBudgetItemTask(List<ImmutableBudgetItem> budgetItems) {
        return Flux.fromIterable(budgetItems)
                .flatMapIterable(this::buildBudgetItemTasks)
                .collectList()
                .flatMap(tasks -> Mono.defer(() -> budgetItemTaskRepository.saveAll(tasks).collectList()))
                .then(Mono.just(ImmutableSuccess.builder().success(true).build()));

    }

    private List<BudgetItemTask> buildBudgetItemTasks(BudgetItem budgetItem) {
        Long currentDate = Instant.now().toEpochMilli();
        List<BudgetItemTask> tasks = new ArrayList<>();
        int frequency = budgetItem.frequency();
        var initialDate = Instant.ofEpochMilli(budgetItem.initialDate())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        int incrementMonthsBy = 12 / frequency;
        for (int i = 1; i <= frequency; i++) {
            var task = ImmutableBudgetItemTask.builder()
                    .id(UUID.randomUUID().toString())
                    .budgetId(budgetItem.budgetId())
                    .budgetItemId(budgetItem.id())
                    .actualAmount(0.0)
                    .createdAt(currentDate)
                    .updatedAt(currentDate)
                    .status(TaskStatus.PENDING)
                    .scheduledDate(initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();
            tasks.add(task);
            initialDate = initialDate.plusMonths(incrementMonthsBy);
        }
        return tasks;
    }
}
