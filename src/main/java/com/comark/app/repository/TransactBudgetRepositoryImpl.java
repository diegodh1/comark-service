package com.comark.app.repository;

import com.comark.app.mapper.BudgetItemMapper;
import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.db.Budget;
import com.comark.app.model.db.BudgetItem;
import com.comark.app.model.db.ImmutableBudget;
import com.comark.app.model.db.ImmutableBudgetItem;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import io.r2dbc.spi.ConnectionFactory;
import lombok.NonNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TransactBudgetRepositoryImpl implements TransactBudgetRepository {
    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final BudgetItemTaskRepository budgetItemTaskRepository;
    private final ConnectionFactory connectionFactory;
    private final BudgetItemMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactBudgetRepositoryImpl.class);
    private static final String INSERT_QUERY  = "INSERT INTO \"BudgetItem\" (" +
            "\"id\", \"budgetId\", \"type\", \"name\", \"detail\", \"amount\", " +
            "\"frequency\", \"expectedFrequencyAmount\", \"initialDate\", " +
            "\"accountingAccount\", \"createdAt\", \"updatedAt\") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    public TransactBudgetRepositoryImpl(BudgetRepository budgetRepository, BudgetItemRepository budgetItemRepository, BudgetItemTaskRepository budgetItemTaskRepository, ConnectionFactory connectionFactory, BudgetItemMapper mapper) {
        this.budgetRepository = budgetRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetItemTaskRepository = budgetItemTaskRepository;
        this.connectionFactory = connectionFactory;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Mono<Success> transactCreateBudget(List<PresupuestoItemDto> budgetItems, @NotBlank String actorId, @NonNull Double budgetAmountFromPreviousYear) {
        return createBudget(actorId, budgetAmountFromPreviousYear)
                .map(currentBudget -> budgetItems.stream()
                        .map(item -> ImmutableBudgetItem.copyOf(mapper.fromPresupuestoItemDto(item)).withBudgetId(currentBudget.id()))
                        .collect(Collectors.toSet()))
                .flatMap(listItems -> Mono.defer(() -> budgetItemRepository.saveAll(listItems).collectList()))
                .then(Mono.just(ImmutableSuccess.builder().build()));
    }

    private Mono<Budget> createBudget(String actorId, Double budgetAmountFromPreviousYear){
        return budgetRepository.save(ImmutableBudget.builder()
                .id(LocalDate.ofEpochDay(Instant.now().toEpochMilli()).getYear())
                .actorId(actorId)
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .budgetAmountFromPreviousYear(budgetAmountFromPreviousYear)
                .build());
    }

    // Batch insert method
    private Mono<Void> batchInsertBudgetItems(List<BudgetItem> budgetItems, DatabaseClient databaseClient) {
        return Mono.empty();
    }
}
