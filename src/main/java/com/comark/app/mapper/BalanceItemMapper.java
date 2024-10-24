package com.comark.app.mapper;

import com.comark.app.model.db.BudgetItem;
import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.dto.balance.BalanceDto;
import com.comark.app.model.dto.balance.BalanceItemReportDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;

import java.util.List;

public interface BalanceItemMapper {
    BalanceDto fromBuildingBalance(BuildingBalance balance);
    BalanceItemReportDto fromBuildingBalanceListToBalanceItemReportDto(BuildingBalance balance);
}
