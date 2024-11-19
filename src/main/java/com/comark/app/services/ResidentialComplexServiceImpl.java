package com.comark.app.services;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.db.*;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemEntityDto;
import com.comark.app.model.enums.ResidentialComplexType;
import com.comark.app.repository.ResidentialComplexAdministratorRepository;
import com.comark.app.repository.ResidentialComplexItemEntityRepository;
import com.comark.app.repository.ResidentialComplexItemRepository;
import com.comark.app.repository.ResidentialComplexRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

@Service
public class ResidentialComplexServiceImpl implements ResidentialComplexService {
    private final ResidentialComplexRepository residentialComplexRepository;
    private final ResidentialComplexItemRepository residentialComplexItemRepository;
    private final ResidentialComplexItemEntityRepository residentialComplexItemEntityRepository;
    private final ResidentialComplexAdministratorRepository residentialComplexAdministratorRepository;
    private final EmailService emailService;

    public ResidentialComplexServiceImpl(ResidentialComplexRepository residentialComplexRepository, ResidentialComplexItemRepository residentialComplexItemRepository, ResidentialComplexItemEntityRepository residentialComplexItemEntityRepository, ResidentialComplexAdministratorRepository residentialComplexAdministratorRepository, EmailService emailService) {
        this.residentialComplexRepository = residentialComplexRepository;
        this.residentialComplexItemRepository = residentialComplexItemRepository;
        this.residentialComplexItemEntityRepository = residentialComplexItemEntityRepository;
        this.residentialComplexAdministratorRepository = residentialComplexAdministratorRepository;
        this.emailService = emailService;
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
                                .isRealStateAgency(entity.isRealStateAgency())
                                .lastName(entity.lastName())
                                .name(entity.name())
                                .phoneNumber(entity.phoneNumber())
                                .identificationType(entity.identificationType())
                                .build()).toList())
                .flatMap(entityList -> residentialComplexItemEntityRepository.saveAll(entityList).collectList());
    }
}
