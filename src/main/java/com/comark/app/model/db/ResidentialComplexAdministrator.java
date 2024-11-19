package com.comark.app.model.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("residential_complex_administrator")
@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexAdministrator.class)
@JsonDeserialize(as = ImmutableResidentialComplexAdministrator.class)
public interface ResidentialComplexAdministrator {
    @Id
    String id();
    String residentialComplexId();
    String email();
    @JsonProperty("is_active")
    Boolean isActive();
    Long createdAt();
    Long updatedAt();
}
