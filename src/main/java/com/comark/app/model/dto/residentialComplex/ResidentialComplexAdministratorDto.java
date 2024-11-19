package com.comark.app.model.dto.residentialComplex;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexAdministratorDto.class)
@JsonDeserialize(as = ImmutableResidentialComplexAdministratorDto.class)
public interface ResidentialComplexAdministratorDto {
    String email();
    @Nullable
    String residentialComplexId();
}
