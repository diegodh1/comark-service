package com.comark.app.services;

import com.comark.app.model.db.Pqr;
import com.comark.app.model.dto.pqr.PqrDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface PqrService {
    Mono<Void> savePqr(PqrDto pqr);
    Mono<List<PqrDto>> findAllPqr(String username, Optional<Integer> pageNumber, Optional<Integer> pageSize);
}
