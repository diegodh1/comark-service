package com.comark.app.services;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.db.*;
import com.comark.app.model.dto.residentialComplex.ImmutableResidentialComplexEventDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexEventDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemEntityDto;
import com.comark.app.model.enums.EventStatus;
import com.comark.app.model.enums.ResidentialComplexType;
import com.comark.app.repository.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.comark.app.model.enums.ResidentialComplexType.ZONA_COMUN;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

@Service
public class ResidentialComplexServiceImpl implements ResidentialComplexService {
    private final ResidentialComplexRepository residentialComplexRepository;
    private final ResidentialComplexItemRepository residentialComplexItemRepository;
    private final ResidentialComplexItemEntityRepository residentialComplexItemEntityRepository;
    private final ResidentialComplexAdministratorRepository residentialComplexAdministratorRepository;
    private final EmailService emailService;
    private final ResidentialComplexItemEventRepository residentialComplexItemEventRepository;

    public ResidentialComplexServiceImpl(ResidentialComplexRepository residentialComplexRepository, ResidentialComplexItemRepository residentialComplexItemRepository, ResidentialComplexItemEntityRepository residentialComplexItemEntityRepository, ResidentialComplexAdministratorRepository residentialComplexAdministratorRepository, EmailService emailService, ResidentialComplexItemEventRepository residentialComplexItemEventRepository) {
        this.residentialComplexRepository = residentialComplexRepository;
        this.residentialComplexItemRepository = residentialComplexItemRepository;
        this.residentialComplexItemEntityRepository = residentialComplexItemEntityRepository;
        this.residentialComplexAdministratorRepository = residentialComplexAdministratorRepository;
        this.emailService = emailService;
        this.residentialComplexItemEventRepository = residentialComplexItemEventRepository;
    }

    @Override
    public Mono<Void> createResidentialComplex(String residentialId) {
        return Mono.just(ImmutableResidentialComplex.builder()
                        .id(residentialId)
                        .createdAt(Instant.now().toEpochMilli())
                        .updatedAt(Instant.now().toEpochMilli())
                        .build())
                .flatMap(residentialComplexRepository::save)
                .onErrorResume(DuplicateKeyException.class, error -> Mono.error(new ComarkAppException(BAD_REQUEST.code(), "duplicated item, already exist")))
                .then();
    }

    @Override
    public Mono<Void> addResidentialComplexAdministrator(String residentialId, String email) {
        return residentialComplexRepository.getResidentialComplexById(residentialId)
                .switchIfEmpty(Mono.error(new ComarkAppException(NOT_FOUND.code(), NOT_FOUND.reasonPhrase())))
                .map(residentialComplex -> ImmutableResidentialComplexAdministrator.builder().residentialComplexId(residentialComplex.id())
                                .email(email)
                                .isActive(true)
                                .id(UUID.randomUUID().toString())
                                .createdAt(Instant.now().toEpochMilli())
                                .updatedAt(Instant.now().toEpochMilli())
                                .build())
                .flatMap(residentialComplexAdministratorRepository::save)
                .flatMap(ignore -> emailService.sendEmail(email, "Creación de cuenta", "Bienvenido a Comarkapp, puede registrar su cuenta en el siguiente link http://localhost:8081"))
                .then();
    }

    @Override
    public Mono<List<ResidentialComplexItem>> addResidentialComplexItems(String residentialId, List<ResidentialComplexItemDto> items) {
        return residentialComplexRepository.getResidentialComplexById(residentialId)
                .map(residentialComplex -> items.stream()
                        .map(item -> (ResidentialComplexItem) ImmutableResidentialComplexItem
                                .builder()
                                .residentialComplexId(residentialComplex.id())
                                .id(UUID.randomUUID().toString())
                                .buildingNumber(item.buildingNumber())
                                .name(item.name())
                                .capacity(item.capacity())
                                .parkingNumber(item.parkingNumber())
                                .description(item.description())
                                .type(ResidentialComplexType.valueOf(item.type()))
                                .rentPrice(item.rentPrice())
                                .storageRoomNumber(item.storageRoomNumber())
                                .restrictions(item.restrictions())
                                .build()).toList())
                .flatMap(itemList -> residentialComplexItemRepository.saveAll(itemList).collectList())
                .switchIfEmpty(Mono.error(new ComarkAppException(NOT_FOUND.code(), NOT_FOUND.reasonPhrase())));
    }

    @Override
    public Mono<List<ResidentialComplexItem>> getAllResidentialComplexItemsByResidentialComplexId(String residentialId, Optional<String> apartmentNumber) {
        return residentialComplexItemRepository.findAllByResidentialComplexId(residentialId, apartmentNumber.orElse(""))
                .cast(ResidentialComplexItem.class)
                .collectList();
    }

    @Override
    public Mono<List<ResidentialComplexItem>> getAllResidentialComplexItemsTypeEqualsToZonaComun(String residentialId) {
        return residentialComplexItemRepository.findAllResidentialComplexItemByItemType(residentialId, ZONA_COMUN.name())
                .cast(ResidentialComplexItem.class)
                .collectList();
    }

    @Override
    public Mono<List<ResidentialComplexItem>> getAllResidentialComplexItemsByResidentialComplexIdAndEmail(String residentialId, String email) {
        return residentialComplexItemRepository.findAllByComplexIdAndEmail(residentialId, email)
                .cast(ResidentialComplexItem.class)
                .collectList();
    }

    @Override
    public Mono<List<ResidentialComplexItemEntity>> addResidentialComplexItemEntities(String residentialId, List<ResidentialComplexItemEntityDto> entities) {
        return residentialComplexItemRepository.findResidentialComplexItemById(residentialId)
                .map(item -> entities.stream().map(entity ->
                        (ResidentialComplexItemEntity) ImmutableResidentialComplexItemEntity.builder()
                                .residentialComplexItemId(item.id())
                                .id(UUID.randomUUID().toString())
                                .updatedAt(Instant.now().toEpochMilli())
                                .createdAt(Instant.now().toEpochMilli())
                                .email(entity.email())
                                .type(entity.type())
                                .identificationNumber(entity.identificationNumber())
                                .isActive(true)
                                .isRealStateAgency(Optional.ofNullable(entity.isRealStateAgency()).orElse(false))
                                .lastName(entity.lastName())
                                .name(entity.name())
                                .phoneNumber(entity.phoneNumber())
                                .identificationType(entity.identificationType())
                                .build()).toList())
                .flatMap(entityList -> residentialComplexItemEntityRepository.saveAll(entityList).collectList())
                .flatMap(savedEntities -> {
                    // Send emails asynchronously
                    Flux.fromIterable(entities)
                            .flatMap(entity ->
                                    emailService.sendEmail(
                                            entity.email(),
                                            "Creación de cuenta",
                                            "Bienvenido a Comarkapp, puede registrar su cuenta en el siguiente link http://localhost:8081"
                                    )
                            )
                            .subscribe(); // Trigger sending emails asynchronously
                    return Mono.just(savedEntities);
                });
    }

    @Override
    public Mono<List<ResidentialComplexItemEntity>> getAllResidentialItemEntitiesByResidentialItemId(String residentialItemId) {
        return residentialComplexItemEntityRepository.findAllByResidentialComplexItemId(residentialItemId)
                .cast(ResidentialComplexItemEntity.class)
                .collectList();
    }

    @Override
    public Mono<Void> createResidentialComplexItemEvent(String residentialComplexId, String residentialItemId, String eventName, String description, String restriction, String startDateTime, String endDateTime, String organizerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime startLocalDateTime = LocalDateTime.parse(startDateTime, formatter);
        LocalDateTime endLocalDateTime = LocalDateTime.parse(endDateTime, formatter);
        var tuple1 = Tuples.of(startLocalDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli(), endLocalDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli());
        var tupleRange = getDateTimeRange(tuple1.getT1());
        return residentialComplexItemEventRepository.findAllResidentialComplexItemEventsByStatusAndDateRange(EventStatus.APROVADO.name(), tupleRange.getT1(), tupleRange.getT2())
                .map(event -> isOverlappingEvent(tuple1, Tuples.of(event.startDateTime(), event.endDateTime())))
                .filter(isOverlapping -> isOverlapping)
                .collectList()
                .filter(response -> !response.isEmpty())
                .flatMap(ignore -> Mono.error(new ComarkAppException(BAD_REQUEST.code(), "Horario no disponible")))
                .switchIfEmpty(residentialComplexItemEventRepository.save(ImmutableResidentialComplexItemEvent.builder()
                        .id(UUID.randomUUID().toString())
                        .name(eventName)
                        .residentialComplexId(residentialComplexId)
                        .residentialComplexItemId(residentialItemId)
                        .eventStatus(EventStatus.PENDIENTE)
                        .description(description)
                        .restrictions(restriction)
                        .startDateTime(tuple1.getT1())
                        .endDateTime(tuple1.getT2())
                        .organizerId(organizerId)
                        .createdAt(Instant.now().toEpochMilli())
                        .updatedAt(Instant.now().toEpochMilli())
                        .build()))
                .then();
    }

    public Mono<List<ResidentialComplexEventDto>> getAllPendingEvents(String residentialComplexId) {
        return residentialComplexItemEventRepository
                .findAllResidentialComplexItemEventsByStatus(EventStatus.PENDIENTE.name(), residentialComplexId)
                .flatMap(item ->
                        residentialComplexItemRepository.findResidentialComplexItemById(item.residentialComplexItemId())
                                .map(complexItem -> ImmutableResidentialComplexEventDto
                                        .builder()
                                        .id(item.id())
                                        .description(item.description())
                                        .name(item.name())
                                        .restriction(item.restrictions())
                                        .organizerId(item.organizerId())
                                        .place(complexItem.name())
                                        .endDateTime(convertEpochToStringWithoutTimeZone(item.endDateTime()))
                                        .startDateTime(convertEpochToStringWithoutTimeZone(item.startDateTime()))
                                        .build()
                                )
                )
                .cast(ResidentialComplexEventDto.class)
                .collectList();
    }

    @Override
    public Mono<Void> updateResidentialComplexItemEventStatus(String eventId, EventStatus status) {
        return residentialComplexItemEventRepository.updateStatus(status.name(), eventId)
                .then();
    }

    public static String convertEpochToStringWithoutTimeZone(long epochTime) {
        // Convert epoch time to Instant
        LocalDateTime date = Instant.ofEpochMilli(epochTime)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();

        // Create a formatter with the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'hora' HH:mm:ss");
        System.out.println("hora; " + date.getHour());
        // Format the LocalDateTime as a string
        return date.format(formatter);
    }

    private Tuple2<Long, Long> getDateTimeRange(Long startDateTime) {
        var startDateWithoutTime = Instant.ofEpochMilli(startDateTime)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();

        LocalDateTime startOfDay = startDateWithoutTime.atTime(0, 0, 0);
        LocalDateTime endOfDay = startDateWithoutTime.atTime(23, 59, 59);
        return Tuples.of(startOfDay.toInstant(ZoneOffset.UTC).toEpochMilli(), endOfDay.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    private boolean isOverlappingEvent(Tuple2<Long, Long> tuple1, Tuple2<Long, Long> tuple2) {
        var minTupleDate = tuple1.getT1() < tuple2.getT1() ? tuple1 : tuple2;
        var maxTupleDate = tuple1.getT1() >= tuple2.getT1() ? tuple1 : tuple2;
        return minTupleDate.getT2() >= maxTupleDate.getT1();
    }
}
