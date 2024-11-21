package com.comark.app.model.db;

import com.comark.app.model.enums.ResidentialComplexType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nullable;

@Table("residential_complex_item")
@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexItem.class)
@JsonDeserialize(as = ImmutableResidentialComplexItem.class)
public interface ResidentialComplexItem {
    @Id
    String id();
    String residentialComplexId();
    String name();
    @Nullable
    String description();
    ResidentialComplexType type();
    @Nullable
    String buildingNumber();
    @Nullable
    String parkingNumber();
    @Nullable
    String storageRoomNumber ();
    @Nullable
    Double rentPrice();
    @Nullable
    Integer capacity();
    @Nullable
    String restrictions();
}
