package com.comark.app.model.dto.residentialComplex;

import com.comark.app.model.db.ImmutableResidentialComplexItem;
import com.comark.app.model.enums.IdentificationType;
import com.comark.app.model.enums.ResidentialComplexItemEntityType;
import com.comark.app.model.enums.ResidentialComplexType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexItemOwnerDto.class)
@JsonDeserialize(as = ImmutableResidentialComplexItemOwnerDto.class)
public interface ResidentialComplexItemOwnerDto {
    String buildingNumber();
    Double residentialComplexCoefficient();
    @Nullable
    String description();
    ResidentialComplexType type();
    String parkingNumber();
    Double parkingNumberCoefficient();
    String storageRoomNumber ();
    Double coefficientStorageRoomNumber();
    Double rentPrice();
    Integer capacity();
    @Nullable
    String restrictions();
    String identificationNumber();
    IdentificationType identificationType();
    String ownerName();
    @Nullable
    String ownerLastName();
    @Nullable
    String ownerPhoneNumber();
    String ownerEmail();
}
