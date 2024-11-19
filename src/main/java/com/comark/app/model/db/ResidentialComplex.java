package com.comark.app.model.db;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("residential_complex")
@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplex.class)
@JsonDeserialize(as = ImmutableResidentialComplex.class)
public interface ResidentialComplex {
    @Id
    String id();
    Long createdAt();
    Long updatedAt();
}
