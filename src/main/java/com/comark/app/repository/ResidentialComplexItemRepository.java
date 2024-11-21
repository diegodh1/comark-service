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
    @Query("SELECT * FROM residential_complex_item WHERE residential_complex_id = :residentialComplexId AND building_number LIKE CONCAT(:buildingNumber, '%')")
    Flux<ImmutableResidentialComplexItem> findAllByResidentialComplexId(String residentialComplexId, String buildingNumber);
    @Query("""
    SELECT DISTINCT residential_complex_item.*
    FROM residential_complex_item
    INNER JOIN residential_complex_item_entity 
    ON residential_complex_item_entity.residential_complex_item_id = residential_complex_item.id
    WHERE residential_complex_item.residential_complex_id = :residentialComplexId
    AND residential_complex_item_entity.email = :email
""")
    Flux<ImmutableResidentialComplexItem> findAllByComplexIdAndEmail(String residentialComplexId, String email);
    @Query("SELECT * FROM residential_complex_item WHERE id = :id")
    Mono<ImmutableResidentialComplexItem> findResidentialComplexItemById(String id);
}