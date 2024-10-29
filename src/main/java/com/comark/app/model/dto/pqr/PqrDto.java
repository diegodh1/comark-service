package com.comark.app.model.dto.pqr;

import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.enums.PqrType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutablePqrDto.class)
@JsonDeserialize(as = ImmutablePqrDto.class)
public interface PqrDto {
    String property();
    String userName();
    String type();
    String dependency();
    String assignedTo();
    String description();
    @Nullable
    String status();
    @Nullable
    String createdAt();
    @Nullable
    String responseDate();
    @Nullable
    String response();
    @Nullable
    Integer responseTimeInDays();
}