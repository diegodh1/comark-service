package com.comark.app.model.dto.balance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(as = ImmutableBalanceItemDto.class)
@JsonDeserialize(as = ImmutableBalanceItemDto.class)
public interface BalanceItemDto {
    String title();
    String value();
}
