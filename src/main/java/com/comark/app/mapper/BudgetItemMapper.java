package com.comark.app.mapper;

import com.comark.app.model.db.BudgetItem;
import com.comark.app.model.dto.budget.PresupuestoItemDto;

public interface BudgetItemMapper {
    BudgetItem fromPresupuestoItemDto(PresupuestoItemDto presupuestoItemDto);
}
