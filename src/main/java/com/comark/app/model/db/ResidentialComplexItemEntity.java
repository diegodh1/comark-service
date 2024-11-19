package com.comark.app.model.db;

import com.comark.app.model.enums.IdentificationType;
import com.comark.app.model.enums.ResidentialComplexItemEntityType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nullable;

@Table("residential_complex_item_entity")
@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexItemEntity.class)
@JsonDeserialize(as = ImmutableResidentialComplexItemEntity.class)
public interface ResidentialComplexItemEntity {
    @Id
    String id();
    String residentialComplexItemId();
    IdentificationType identificationType();
    String identificationNumber();
    ResidentialComplexItemEntityType type();
    String name();
    @Nullable
    String lastName();
    String phoneNumber();
    String email();
    Long createdAt();
    Long updatedAt();
    Boolean isRealStateAgency();
    Boolean isActive();
}
