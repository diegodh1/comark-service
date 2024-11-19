package com.comark.app.model.dto.email;

import com.comark.app.model.dto.pqr.ImmutablePqrDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEmailDto.class)
@JsonDeserialize(as = ImmutableEmailDto.class)
public interface EmailDto {
    @JsonProperty("email_to")
    String emailTo();
    String subject();
    String message();
}
