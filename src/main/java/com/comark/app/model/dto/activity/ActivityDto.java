package com.comark.app.model.dto.activity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;


@Value.Immutable
@JsonSerialize(as = ImmutableActivityDto.class)
@JsonDeserialize(as = ImmutableActivityDto.class)
public interface ActivityDto {
    String origenId();
    String type();
    String auxId();
    String title();
    String details();
    String assignedTo();
    String createdAt();
    String scheduledDate();
    String closingDate();
    String status();
    Integer durationInDays();
}
