package com.comark.app.services.util;

import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FileUtil {
    Mono<List<PresupuestoItemDto>> loadBudgetFromFile(byte[] fileBytes);
    Mono<List<BuildingBalance>> loadBuildingBalanceFromFile(byte[] fileBytes);
}
