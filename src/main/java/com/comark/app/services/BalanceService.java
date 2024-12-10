package com.comark.app.services;

import com.comark.app.model.Success;
import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.dto.balance.BalanceDto;
import com.comark.app.model.dto.balance.BalanceItemReportDto;
import com.comark.app.model.dto.budget.ReportValueDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BalanceService {
    Mono<Success> upsertBalance(byte[] file, String actorId, String residentialComplexId);
    Mono<List<BuildingBalance>> getAllBalanceReports(String residentialComplexId);
    Mono<List<BalanceItemReportDto>> getBalanceReportsByApartmentNumber(String residentialComplexId,String ApartmentNumber);
    Mono<BalanceDto> getMonthBalance(String residentialComplexId,String apartmentNumber);
}
