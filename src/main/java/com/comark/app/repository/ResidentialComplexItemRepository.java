package com.comark.app.repository;

import com.comark.app.model.db.ImmutablePqr;
import com.comark.app.model.db.ImmutableResidentialComplexItem;
import com.comark.app.model.db.ResidentialComplex;
import com.comark.app.model.db.ResidentialComplexItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResidentialComplexItemRepository extends ReactiveCrudRepository<ResidentialComplexItem, String> {
    @Query("SELECT * FROM residential_complex_item WHERE residential_complex_id = :residentialComplexId")
    Flux<ImmutableResidentialComplexItem> findAllByResidentialComplexId(String residentialComplexId);
    @Query("SELECT * FROM residential_complex_item WHERE id = :id")
    Mono<ImmutableResidentialComplexItem> findResidentialComplexItemById(String id);
}
