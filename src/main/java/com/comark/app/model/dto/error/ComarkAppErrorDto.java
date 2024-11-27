package com.comark.app.model.dto.error;

import com.comark.app.model.db.ImmutableResidentialComplexItemEvent;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.relational.core.mapping.Table;

@Value.Immutable
@JsonSerialize(as = ImmutableComarkAppErrorDto.class)
@JsonDeserialize(as = ImmutableComarkAppErrorDto.class)
public interface ComarkAppErrorDto {
    String message();
    Integer code();
}
