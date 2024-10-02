package com.comark.app.model.dto.budget;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(as = ImmutableCompleteTaskDto.class)
@JsonDeserialize(as = ImmutableCompleteTaskDto.class)
public interface CompleteTaskDto {
    String id();
    String actualAccountingAccount();
    String billId();
    Double amount();
}
