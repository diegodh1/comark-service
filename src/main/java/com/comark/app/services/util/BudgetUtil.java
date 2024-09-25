package com.comark.app.services.util;

import com.comark.app.model.dto.budget.PresupuestoItemDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BudgetUtil {
    Mono<List<PresupuestoItemDto>> loadBudgetFromFile(byte[] fileBytes);
}
