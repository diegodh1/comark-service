package com.comark.app.repository;

import com.comark.app.model.db.ImmutableResidentialComplexAdministrator;
import com.comark.app.model.db.ImmutableResidentialComplexItem;
import com.comark.app.model.db.ResidentialComplexAdministrator;
import com.comark.app.model.db.ResidentialComplexItemEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResidentialComplexAdministratorRepository extends ReactiveCrudRepository<ResidentialComplexAdministrator, String> {
    @Query("SELECT * FROM residential_complex_administrator WHERE email = :email")
    Flux<ImmutableResidentialComplexAdministrator> findAllResidentialComplexAdministratorByEmail(String email);
}
