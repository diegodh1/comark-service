package com.comark.app.model.db;

import com.comark.app.model.enums.ActivityStatus;
import com.comark.app.model.enums.ActivityType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nullable;
import java.io.Serializable;

@Table("activity")
@Value.Immutable
@JsonSerialize(as = ImmutableActivity.class)
@JsonDeserialize(as = ImmutableActivity.class)
public interface Activity {
    @Id
    String id();
    String residentialComplexId();
    String originId();
    ActivityType activityType();
    String auxId();
    String title();
    String details();
    String assignedTo();
    Long createdAt();
    Long scheduledDate();
    @Nullable
    Long closingDate();
    ActivityStatus status();
}

