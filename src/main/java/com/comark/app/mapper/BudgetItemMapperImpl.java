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
        return ImmutableBudgetItem.builder()
                .id(UUID.randomUUID().toString())
                .type(presupuestoItemDto.getTipo().name())
                .name(presupuestoItemDto.getNombre())
                .accountingAccount(presupuestoItemDto.getCuentaContableId())
                .amount(presupuestoItemDto.getPresupuesto())
                .detail(presupuestoItemDto.getDetalle())
                .frequency(presupuestoItemDto.getFrecuencia().getValue())
                .initialDate(presupuestoItemDto.getFechaInicio().toInstant().toEpochMilli())
                .createdAt(date)
                .updatedAt(date)
                .build();
    }
}
