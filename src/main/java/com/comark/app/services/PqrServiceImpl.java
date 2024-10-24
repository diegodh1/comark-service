package com.comark.app.services;

import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.dto.pqr.PqrDto;
import com.comark.app.model.enums.PqrType;
import com.comark.app.repository.PqrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class PqrServiceImpl implements PqrService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PqrServiceImpl.class);
    private final PqrRepository repository;

    public PqrServiceImpl(PqrRepository repository) {
        this.repository = repository;
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
                .doOnError(error -> LOGGER.error(error.getMessage(), error))
                .then(Mono.empty());
    }
}
