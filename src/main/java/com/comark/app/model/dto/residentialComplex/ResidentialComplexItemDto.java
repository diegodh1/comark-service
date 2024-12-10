package com.comark.app.model.dto.residentialComplex;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexItemDto.class)
@JsonDeserialize(as = ImmutableResidentialComplexItemDto.class)
public interface ResidentialComplexItemDto {
    String name();
    @Nullable
    String description();
    String type();
    @Nullable
    String buildingNumber();
    @Nullable
    Double percentage();
    @Nullable
    Double rentPrice();
    @Nullable
    Integer capacity();
    @Nullable
    String restrictions();
}
