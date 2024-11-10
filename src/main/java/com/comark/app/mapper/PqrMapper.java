package com.comark.app.mapper;

import com.comark.app.model.db.Pqr;
import com.comark.app.model.dto.pqr.ImmutablePqrDto;
import com.comark.app.model.dto.pqr.PqrDto;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class PqrMapper {

    public static PqrDto toPqrDto(Pqr pqr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var createdDate = Optional.ofNullable(pqr.date()).map(PqrMapper::getDate).orElse(LocalDate.now());
        var responseDate = Optional.ofNullable(pqr.responseDate()).map(PqrMapper::getDate).orElse(LocalDate.now());
        var daysBetween = Math.max(0, ChronoUnit.DAYS.between(createdDate, responseDate));
        return ImmutablePqrDto.builder()
                .id(pqr.id())
                .description(pqr.description())
                .userName(pqr.userName())
                .assignedTo(pqr.assignedTo())
                .dependency(pqr.dependency())
                .property(pqr.property())
                .response(pqr.response())
                .type(pqr.type().name())
                .createdAt(createdDate.format(formatter))
                .responseDate(Optional.ofNullable(pqr.responseDate()).map(date -> responseDate.format(formatter)).orElse(""))
                .status(pqr.response() == null? "PENDIENTE": "RESPONDIDO")
                .responseTimeInDays((int) daysBetween)
                .build();

    }

    private static LocalDate getDate(Long date){
        return Instant.ofEpochMilli(date)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
    }
}
