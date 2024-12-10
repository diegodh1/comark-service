package com.comark.app.model.db;

import com.comark.app.model.enums.AmenityType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("residential_complex_item_amenity")
@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexItemAmenity.class)
@JsonDeserialize(as = ImmutableResidentialComplexItemAmenity.class)
public interface ResidentialComplexItemAmenity {
    @Id
    String id();
    String residentialComplexItemId();
    String amenityId();
    Double percentage();
    AmenityType amenityType();
}
