package com.comark.app.repository;

import com.comark.app.model.db.ImmutableResidentialComplexItem;
import com.comark.app.model.db.ImmutableResidentialComplexItemEntity;
import com.comark.app.model.db.ResidentialComplexItem;
import com.comark.app.model.db.ResidentialComplexItemEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ResidentialComplexItemEntityRepository extends ReactiveCrudRepository<ResidentialComplexItemEntity, String> {
    @Query("SELECT * FROM residential_complex_item_entity WHERE residential_complex_item_id = :residentialComplexItemId")
    Flux<ImmutableResidentialComplexItemEntity> findAllByResidentialComplexItemId(String residentialComplexItemId);
}
