package com.comark.app.repository;

import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.db.ImmutableBuildingBalance;
import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.db.Pqr;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface PqrRepository extends ReactiveCrudRepository<Pqr, String> {
    @Query("SELECT * FROM pqr WHERE user_name = :username ORDER BY date DESC LIMIT :limit OFFSET :offset")
    Flux<ImmutablePqr> getAllPqrs(String username, int limit, int offset);

    @Query("SELECT * FROM pqr WHERE id =:id")
    Mono<ImmutablePqr> getPqrByID(String id);

    @Modifying
    @Query("UPDATE pqr SET response = :response, response_date = :responseDate, response_time = :responseTime WHERE id = :id")
    Mono<Integer> updatePqrResponse(String id, String response, long responseDate, int responseTime);
}
