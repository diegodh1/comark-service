package com.comark.app.model.dto.activity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableActivityPageDto.class)
@JsonDeserialize(as = ImmutableActivityPageDto.class)
public interface ActivityPageDto {
    List<ActivityDto> getActivities();
    int nextPage();
    int previousPage();
}
