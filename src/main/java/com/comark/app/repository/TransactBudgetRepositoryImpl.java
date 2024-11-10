package com.comark.app.repository;

import com.comark.app.mapper.BudgetItemMapper;
import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.db.*;
import com.comark.app.model.dto.budget.BudgetItemTaskDto;
import com.comark.app.model.dto.budget.ImmutableBudgetItemTaskDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.model.enums.ActivityStatus;
import com.comark.app.model.enums.ActivityType;
import com.comark.app.model.enums.TaskStatus;
import lombok.NonNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TransactBudgetRepositoryImpl implements TransactBudgetRepository {
    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final BudgetItemTaskRepository budgetItemTaskRepository;
    private final ActivityRepository activityRepository;
    private final BudgetItemMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactBudgetRepositoryImpl.class);
    private final String DATE_FORMAT = "yyyy-MM-dd";


    public TransactBudgetRepositoryImpl(BudgetRepository budgetRepository, BudgetItemRepository budgetItemRepository, BudgetItemTaskRepository budgetItemTaskRepository, ActivityRepository activityRepository, BudgetItemMapper mapper) {
        this.budgetRepository = budgetRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetItemTaskRepository = budgetItemTaskRepository;
        this.activityRepository = activityRepository;
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

    @Override
    public Mono<List<BudgetItemTaskDto>> getAllBudgetItemTasks(@NonNull Integer budgetId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return Mono.zip(budgetItemRepository.getAllByBudgetId(budgetId).collectList(), budgetItemTaskRepository.getAllByBudgetId(budgetId).collectList())
                .flatMap(results -> {
                    // Create a map of budgetItemId to budgetItemTasks
                    Map<String, List<BudgetItemTask>> budgetItemTaskMap = new HashMap<>();
                    Map<String, BudgetItem> budgetItemMap = new HashMap<>();
                    //get tuples
                    List<BudgetItem> budgetItems = (List<BudgetItem>) results.get(0);
                    List<BudgetItemTask> budgetItemTasks = (List<BudgetItemTask>) results.get(1);
                    // Map each budgetItemTask to its corresponding budgetItem
                    for (BudgetItem budgetItem : budgetItems) {
                        budgetItemMap.put(budgetItem.id(), budgetItem);
                    }
                    // Map each budgetItemTask to its corresponding budgetItem
                    for (BudgetItemTask task : budgetItemTasks) {
                        budgetItemTaskMap.computeIfAbsent(task.budgetItemId(), id -> new ArrayList<>()).add(task);
                    }
                    List<BudgetItemTaskDto> budgetItemTaskDtos = new ArrayList<>();
                    for (Map.Entry<String, BudgetItem> budgetItem : budgetItemMap.entrySet()) {
                        var tasks = budgetItemTaskMap.get(budgetItem.getKey());
                        tasks.forEach(task -> {
                            var scheduleDate = Instant.ofEpochMilli(task.scheduledDate())
                                    .atOffset(ZoneOffset.UTC)
                                    .toLocalDate();
                            var newTask = ImmutableBudgetItemTaskDto.builder()
                                    .id(task.id())
                                    .name(budgetItem.getValue().name())
                                    .details(budgetItem.getValue().detail())
                                    .actualAccountingAccount(task.actualAccountingAccount())
                                    .actualAmount(task.actualAmount())
                                    .status(task.status().name())
                                    .name(Optional.ofNullable(task.name()).orElse(""))
                                    .details(task.details())
                                    .expectedAccountingAccount(budgetItem.getValue().accountingAccount())
                                    .expectedAmount(budgetItem.getValue().expectedFrequencyAmount())
                                    .scheduledDate(scheduleDate.format(formatter))
                                    .build();
                            budgetItemTaskDtos.add(newTask);
                        });
                    }
                    return Mono.just(budgetItemTaskDtos);
                });
    }

    private Mono<Budget> createBudget(String actorId, Double budgetAmountFromPreviousYear) {
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
                .map(this::createActivity)
                .flatMap(activities -> activityRepository.saveAll(activities).collectList())
                .then(Mono.just(ImmutableSuccess.builder().success(true).build()));

    }

    private List<Activity> createActivity(List<BudgetItemTask> tasks) {
        return tasks.stream().map(task -> (Activity) ImmutableActivity.builder()
                .createdAt(task.scheduledDate())
                .id(UUID.randomUUID().toString())
                .activityType(ActivityType.PRESUPUESTO)
                .originId(task.id())
                .auxId(task.budgetId().toString())
                .assignedTo("ADMINISTRADOR")
                .scheduledDate(task.scheduledDate())
                .title(Optional.ofNullable(task.name()).orElse(""))
                .details(Optional.ofNullable(task.details()).orElse(""))
                .status(ActivityStatus.PENDIENTE)
                .build()).toList();
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
                    .status(TaskStatus.SCHEDULED)
                    .details(budgetItem.detail())
                    .name(budgetItem.name())
                    .scheduledDate(initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();
            tasks.add(task);
            initialDate = initialDate.plusMonths(incrementMonthsBy);
        }
        return tasks;
    }
}
