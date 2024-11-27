package com.comark.app.model.dto.residentialComplex;

import com.comark.app.model.enums.EventStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexEventUpdateRequestDto.class)
@JsonDeserialize(as = ImmutableResidentialComplexEventUpdateRequestDto.class)
public interface ResidentialComplexEventUpdateRequestDto {
    EventStatus status();
}