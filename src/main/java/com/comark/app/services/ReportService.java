package com.comark.app.services;

import com.comark.app.model.dto.budget.ReportValueDto;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ReportService {
    Mono<Map<String, List<ReportValueDto>>> getReport(@NonNull String budgetId);
}
