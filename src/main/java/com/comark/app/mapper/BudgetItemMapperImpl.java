package com.comark.app.mapper;

import com.comark.app.model.db.BudgetItem;
import com.comark.app.model.db.ImmutableBudgetItem;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class BudgetItemMapperImpl implements BudgetItemMapper{
    @Override
    public BudgetItem fromPresupuestoItemDto(PresupuestoItemDto presupuestoItemDto) {
        Long date = Instant.now().toEpochMilli();
        int frequency = presupuestoItemDto.getFrecuencia().getValue();
        if(frequency == 0){ throw new IllegalArgumentException("Frequency must not be zero"); }
        double amount = presupuestoItemDto.getPresupuesto();
        double expectedAmount = amount / frequency;
        return ImmutableBudgetItem.builder()
                .id(UUID.randomUUID().toString())
                .budgetId("")
                .type(presupuestoItemDto.getTipo().name())
                .name(presupuestoItemDto.getNombre())
                .accountingAccount(presupuestoItemDto.getCuentaContableId())
                .amount(presupuestoItemDto.getPresupuesto())
                .expectedFrequencyAmount(expectedAmount)
                .detail(presupuestoItemDto.getDetalle())
                .frequency(presupuestoItemDto.getFrecuencia().getValue())
                .initialDate(presupuestoItemDto.getFechaInicio().toInstant().toEpochMilli())
                .createdAt(date)
                .updatedAt(date)
                .build();
    }
}
