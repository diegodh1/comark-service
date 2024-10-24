package com.comark.app.model.db;

import com.comark.app.model.enums.PqrType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nullable;

@Table("pqr")
@Value.Immutable
@JsonSerialize(as = ImmutablePqr.class)
@JsonDeserialize(as = ImmutablePqr.class)
public interface Pqr {
    @Id
    String id();
    Long date();
    String property();
    String userName();
    PqrType type();
    String dependency();
    String assignedTo();
    String description();
    @Nullable
    String response();
    @Nullable
    Long responseDate();
    @Nullable
    Long responseTime();
}
