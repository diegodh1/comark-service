package com.comark.app.model.dto.balance;


import com.comark.app.model.dto.budget.ImmutableBudgetItemTaskDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(as = ImmutableBalanceDto.class)
@JsonDeserialize(as = ImmutableBalanceDto.class)
public interface BalanceDto {
    String apartmentNumber();
    String month();
    String lastBalance();
    String totalToPaid();
    List<BalanceItemDto> details();
}
