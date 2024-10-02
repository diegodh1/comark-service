package com.comark.app.services;

import com.comark.app.model.db.BudgetItemTask;
import com.comark.app.model.db.ImmutableBudgetItemTask;
import com.comark.app.model.enums.TaskStatus;
import com.comark.app.repository.BudgetItemTaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Profile("default")
public class BudgetItemTaskService {
    private final BudgetItemTaskRepository budgetItemTaskRepository;

    public BudgetItemTaskService(BudgetItemTaskRepository budgetItemTaskRepository) {
        this.budgetItemTaskRepository = budgetItemTaskRepository;
    }

    // Schedule the task to run every 5 minutes
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void checkAndUpdateTasks() {
         // Get current timestamp
        // Convert TaskStatus enum to String and fetch tasks
        // TODO GET LAST BUDGET ID
        budgetItemTaskRepository
                .findByStatusIsScheduled(2024)
                .collectList()
                .flatMap(this::updateTaskStatus)
                .subscribe();
    }

    // Update task status to PENDING
    private Mono<Void> updateTaskStatus(List<ImmutableBudgetItemTask> tasks) {
        Long currentDate = Instant.now().toEpochMilli();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        tasks.stream().filter(task -> currentDate - task.scheduledDate() >= oneDayInMillis)
                        .forEach(task -> budgetItemTaskRepository.updateStatus(TaskStatus.PENDING.name(), task.id()).subscribe());
        return Mono.empty();
    }
}
