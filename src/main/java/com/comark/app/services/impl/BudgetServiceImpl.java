package com.comark.app.services.impl;

import com.comark.app.model.Success;
import com.comark.app.model.db.BudgetItemTask;
import com.comark.app.model.dto.budget.BudgetItemTaskDto;
import com.comark.app.model.dto.budget.CompleteTaskDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.model.enums.TaskStatus;
import com.comark.app.repository.BudgetItemTaskRepository;
import com.comark.app.repository.TransactBudgetRepositoryImpl;
import com.comark.app.services.BudgetService;
import com.comark.app.services.util.FileUtil;
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
    private final FileUtil fileUtil;
    private final TransactBudgetRepositoryImpl transactBudgetRepository;
    private final BudgetItemTaskRepository budgetItemTaskRepository;

    public BudgetServiceImpl(FileUtil fileUtil, TransactBudgetRepositoryImpl transactBudgetRepository, BudgetItemTaskRepository budgetItemTaskRepository) {
        this.fileUtil = fileUtil;
        this.transactBudgetRepository = transactBudgetRepository;
        this.budgetItemTaskRepository = budgetItemTaskRepository;
    }

    @Override
    public Mono<Success> upsertBudget(byte[] file, String actorId) {
        return fileUtil.loadBudgetFromFile(file)
                .flatMap(budgetList -> Mono.just(Tuples.of(
                        budgetList.stream().filter(it -> !it.getNombre().equals("EXCENDENTES AÑOS ANTERIORES")).collect(Collectors.toList()),
                        getBudgetFromPreviousYear(budgetList))))
                .flatMap(tuple2 -> transactBudgetRepository.transactCreateBudget(tuple2.getT1(), actorId, tuple2.getT2()))
                .doOnError(error -> LOGGER.error(error.getMessage(), error));
    }

    @Override
    public Mono<List<BudgetItemTaskDto>> getAllBudgetItemTasks(Integer budgetId) {
        return transactBudgetRepository.getAllBudgetItemTasks(budgetId);
    }

    @Override
    public Mono<BudgetItemTask> completeTask(CompleteTaskDto completeTaskDto) {
        return budgetItemTaskRepository.updateBudgetItemTask(
                TaskStatus.COMPLETED.name(),
                completeTaskDto.amount(),
                completeTaskDto.actualAccountingAccount(),
                completeTaskDto.billId(),
                completeTaskDto.id()
        ).cast(BudgetItemTask.class);
    }

    private double getBudgetFromPreviousYear(List<PresupuestoItemDto> items){
        return items.stream().filter(item -> item.getNombre().equals("EXCENDENTES AÑOS ANTERIORES"))
                .findFirst()
                .map(PresupuestoItemDto::getPresupuesto)
                .orElse(0.0);
    }
}
