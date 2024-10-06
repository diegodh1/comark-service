package com.comark.app.services;

import com.comark.app.model.db.BudgetItem;
import com.comark.app.model.db.BudgetItemTask;
import com.comark.app.model.dto.budget.BudgetItemTaskDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.model.enums.PresupuestoTipo;
import com.comark.app.repository.BudgetItemRepository;
import com.comark.app.repository.BudgetItemTaskRepository;
import com.comark.app.repository.BudgetRepository;
import com.comark.app.repository.TransactBudgetRepositoryImpl;
import com.comark.app.services.util.BudgetUtil;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
    private final BudgetItemTaskRepository budgetItemTaskRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final BudgetRepository budgetRepository;

    public ReportServiceImpl(BudgetItemTaskRepository budgetItemTaskRepository, BudgetItemRepository budgetItemRepository, BudgetRepository budgetRepository) {
        this.budgetItemTaskRepository = budgetItemTaskRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetRepository = budgetRepository;
    }


    @Override
    public Mono<Map<String, Map<Integer, String>>> getReport(@NonNull Integer budgetId) {
        return Mono.zip(getAllBudgetItems(budgetId).collectList(), getAllBudgetItemTasks(budgetId).collectList(), budgetRepository.getBudgetById(budgetId))
                .flatMap(tuple -> Mono.just(getReportHelper(tuple.getT1(), tuple.getT2())))
                .doOnError(error -> LOGGER.error(error.getMessage(), error));
    }

    public Flux<BudgetItem> getAllBudgetItems(Integer budgetId) {
        return budgetItemRepository.getAllByBudgetId(budgetId).cast(BudgetItem.class);
    }
    public Flux<BudgetItemTask> getAllBudgetItemTasks(Integer budgetId) {
        return budgetItemTaskRepository.getAllByBudgetId(budgetId).cast(BudgetItemTask.class);
    }

    private Map<String, Map<Integer, String>> getReportHelper(List<BudgetItem> budgetItems, List<BudgetItemTask> budgetItemTask){
        Map<String, Map<Integer, String>> response = new HashMap<>();
        Map<String, BudgetItem> mapExpectedValues = new HashMap<>();
        List<BudgetItemTask> incomeTasks = new ArrayList<>();
        List<BudgetItemTask> expenseTasks = new ArrayList<>();
        for (BudgetItem budgetItem : budgetItems) {
            mapExpectedValues.put(budgetItem.id(), budgetItem);
        }
        for (BudgetItemTask task : budgetItemTask) {
            boolean isIncome = mapExpectedValues.get(task.budgetItemId()).type().equals(PresupuestoTipo.INGRESOS.name());
            if(isIncome){
                incomeTasks.add(task);
            } else{
                expenseTasks.add(task);
            }
        }
        //Expected income vs actual income

        var incomeResponse = calculateTaskValuesByMonth(incomeTasks, mapExpectedValues);
        response.put("EXPECTED_INCOME", incomeResponse.getT1());
        response.put("ACTUAL_INCOME", incomeResponse.getT2());
        var expenseResponse = calculateTaskValuesByMonth(expenseTasks, mapExpectedValues);
        response.put("EXPECTED_EXPENSE", expenseResponse.getT1());
        response.put("ACTUAL_EXPENSE", expenseResponse.getT2());
        return response;
    }

    private Tuple2<Map<Integer, String>, Map<Integer, String>> calculateTaskValuesByMonth(List<BudgetItemTask> tasks, Map<String, BudgetItem> mapExpectedValues){
        tasks = tasks.stream().sorted(Comparator.comparingLong(BudgetItemTask::scheduledDate)).collect(Collectors.toList());
        DecimalFormat decimalFormat = new DecimalFormat("#");
        LocalDate lastDate = null;
        double expectedValue = 0.0;
        double actualValue = 0.0;
        Map<Integer, String> expectedValuesMap = new LinkedHashMap<>();
        Map<Integer, String> actualValuesMap = new LinkedHashMap<>();
        for (BudgetItemTask task : tasks) {
            var currentDate = Instant.ofEpochMilli(task.scheduledDate()).atOffset(ZoneOffset.UTC).toLocalDate();
            lastDate = lastDate == null? currentDate: lastDate;
            if(currentDate.getMonthValue() != lastDate.getMonthValue()){
                long monthsBetween = ChronoUnit.MONTHS.between(lastDate, currentDate);
                int lastMonth = lastDate.getMonthValue();
                for (int i = 0; i < monthsBetween; i++) {
                    int monthValue = (lastMonth + i) % 13;
                    expectedValuesMap.put(monthValue, decimalFormat.format(expectedValue));
                    actualValuesMap.put(monthValue, decimalFormat.format(actualValue));
                }
                lastDate = currentDate;
            }
            actualValue += task.actualAmount();
            expectedValue += mapExpectedValues.get(task.budgetItemId()).expectedFrequencyAmount();
        }
        if(lastDate != null){
            expectedValuesMap.put(lastDate.getMonthValue(), decimalFormat.format(expectedValue));
            actualValuesMap.put(lastDate.getMonthValue(), decimalFormat.format(actualValue));
        }
        return Tuples.of(expectedValuesMap, actualValuesMap);
    }
}
