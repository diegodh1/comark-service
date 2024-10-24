package com.comark.app.services;

import com.comark.app.model.db.Pqr;
import com.comark.app.model.dto.pqr.PqrDto;
import reactor.core.publisher.Mono;

public interface PqrService {
    Mono<Void> savePqr(PqrDto pqr);
}
