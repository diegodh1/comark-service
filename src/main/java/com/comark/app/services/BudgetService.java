package com.comark.app.services;

import com.comark.app.model.Success;
import reactor.core.publisher.Mono;

public interface BudgetService {
    Mono<Success> upsertBudget(byte[] file, String actorId);
}
