package com.comark.app.model.dto.residentialComplex;

import com.comark.app.model.enums.IdentificationType;
import com.comark.app.model.enums.ResidentialComplexItemEntityType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexItemEntityDto.class)
@JsonDeserialize(as = ImmutableResidentialComplexItemEntityDto.class)
public interface ResidentialComplexItemEntityDto {
    @Nullable
    String id();
    IdentificationType identificationType();
    String identificationNumber();
    ResidentialComplexItemEntityType type();
    String name();
    @Nullable
    String lastName();
    @Nullable
    String phoneNumber();
    String email();
    @Nullable
    String createdAt();
    @Nullable
    String updatedAt();
    @Nullable
    Boolean isRealStateAgency();
    @Nullable
    Boolean isActive();
}
