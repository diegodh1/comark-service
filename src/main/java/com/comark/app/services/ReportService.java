package com.comark.app.services;

import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ReportService {
    Mono<Map<String, Map<Integer, String>>> getReport(@NonNull Integer budgetId);
}
