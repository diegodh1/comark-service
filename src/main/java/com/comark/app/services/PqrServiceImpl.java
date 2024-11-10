package com.comark.app.services;

import com.comark.app.mapper.PqrMapper;
import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.db.ImmutableActivity;
import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.db.Pqr;
import com.comark.app.model.dto.pqr.PqrDto;
import com.comark.app.model.enums.ActivityStatus;
import com.comark.app.model.enums.ActivityType;
import com.comark.app.model.enums.PqrType;
import com.comark.app.repository.ActivityRepository;
import com.comark.app.repository.PqrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class PqrServiceImpl implements PqrService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PqrServiceImpl.class);
    private final PqrRepository repository;
    private final ActivityRepository activityRepository;
    private final static int DAYS = 15;

    public PqrServiceImpl(PqrRepository repository, ActivityRepository activityRepository) {
        this.repository = repository;
        this.activityRepository = activityRepository;
    }

    @Override
    public Mono<Void> savePqr(PqrDto pqr) {
        return Mono.just(pqr)
                .map(pqrDto -> ImmutablePqr.builder()
                        .date(Instant.now().toEpochMilli())
                        .id(UUID.randomUUID().toString())
                        .assignedTo(pqrDto.assignedTo())
                        .property(pqrDto.property())
                        .dependency(pqrDto.dependency())
                        .description(pqrDto.description())
                        .type(PqrType.valueOf(pqrDto.type()))
                        .userName(pqrDto.userName())
                        .build()
                )
                .flatMap(repository::save)
                .flatMap(pqrDb -> activityRepository.save(ImmutableActivity.builder()
                        .originId(pqrDb.id())
                        .auxId(pqrDb.id())
                        .scheduledDate(addBusinessDays(pqrDb.date()))
                        .assignedTo("ADMINISTRADOR")
                        .createdAt(Instant.now().toEpochMilli())
                        .id(UUID.randomUUID().toString())
                        .activityType(ActivityType.PQRSF)
                        .status(ActivityStatus.PENDIENTE)
                        .title(pqrDb.type().name())
                        .details(pqrDb.description())
                        .build()
                ))
                .doOnError(error -> LOGGER.error(error.getMessage(), error))
                .then(Mono.empty());
    }

    private long addBusinessDays(long day) {
        LocalDate date = Instant.ofEpochMilli(day)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        int addedDays = 0;

        while (addedDays < DAYS) {
            date = date.plusDays(1);
            addedDays++;
        }

        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    @Override
    public Mono<Success> updatePqr(String id, String response) {
        long responseDate = Instant.now().toEpochMilli();

        return repository.getPqrByID(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("PQR no registrada en el sistema")))
                .flatMap(pqr -> repository.updatePqrResponse(pqr.id(), response, responseDate, getDaysBetween(pqr.date(), responseDate)))
                .flatMap(ignore -> activityRepository.updateActivity(Instant.now().toEpochMilli(), ActivityStatus.REALIZADO.name(), id, ActivityType.PQRSF.name()))
                .then(Mono.just(ImmutableSuccess.builder().success(true).build()))
                .cast(Success.class)
                .doOnError(error -> LOGGER.error(error.getMessage(), error));
    }


    @Override
    public Mono<List<PqrDto>> findAllPqr(String username, Optional<Integer> optionalPageNumber, Optional<Integer> optionalPageSize) {
        int pageNumber = optionalPageNumber.orElse(1);
        int pageSize = optionalPageSize.orElse(10);
        int offset = (pageNumber - 1) * pageSize;
        return repository.getAllPqrs(username, pageSize, offset)
                .cast(Pqr.class)
                .map(PqrMapper::toPqrDto)
                .collectList();
    }

    private int getDaysBetween(Long createdDate, Long responseDate) {
        var localDate1 = Instant.ofEpochMilli(createdDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        var localDate2 = Instant.ofEpochMilli(responseDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        return (int) Math.max(0, ChronoUnit.DAYS.between(localDate1, localDate2));
    }


}
