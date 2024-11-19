package com.comark.app.repository;

import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.db.ImmutableResidentialComplex;
import com.comark.app.model.db.Pqr;
import com.comark.app.model.db.ResidentialComplex;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ResidentialComplexRepository extends ReactiveCrudRepository<ResidentialComplex, String> {
    @Query("SELECT * FROM residential_complex WHERE id =:id")
    Mono<ImmutableResidentialComplex> getResidentialComplexById(String id);
}
