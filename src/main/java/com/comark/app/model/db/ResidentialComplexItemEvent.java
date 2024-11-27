package com.comark.app.model.db;

import com.comark.app.model.enums.EventStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nullable;

@Table("residential_complex_item_event")
@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexItemEvent.class)
@JsonDeserialize(as = ImmutableResidentialComplexItemEvent.class)
public interface ResidentialComplexItemEvent {
    @Id
    String id();
    String name();
    String residentialComplexId();
    String residentialComplexItemId();
    String organizerId();
    String description();
    @Nullable
    String restrictions();
    Long startDateTime();
    Long endDateTime();
    Long createdAt();
    Long updatedAt();
    EventStatus eventStatus();
}
