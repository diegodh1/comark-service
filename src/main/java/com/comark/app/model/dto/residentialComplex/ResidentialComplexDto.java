package com.comark.app.model.dto.residentialComplex;

import com.comark.app.model.dto.pqr.ImmutablePqrDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexDto.class)
@JsonDeserialize(as = ImmutableResidentialComplexDto.class)
public interface ResidentialComplexDto {
    String id();
}
