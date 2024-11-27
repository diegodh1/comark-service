package com.comark.app.model.dto.residentialComplex;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableResidentialComplexEventDto.class)
@JsonDeserialize(as = ImmutableResidentialComplexEventDto.class)
public interface ResidentialComplexEventDto {
    @Nullable
    String place();
    @Nullable
    String id();
    String name();
    @JsonProperty("start_date_time")
    String startDateTime();
    @JsonProperty("end_date_time")
    String endDateTime();
    @Nullable
    String description();
    @Nullable
    String restriction();
    @JsonProperty("organizer_id")
    String organizerId();
}
