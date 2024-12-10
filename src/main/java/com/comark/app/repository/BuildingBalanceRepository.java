package com.comark.app.repository;

import com.comark.app.model.db.Budget;
import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.db.ImmutableBudget;
import com.comark.app.model.db.ImmutableBuildingBalance;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BuildingBalanceRepository extends ReactiveCrudRepository<BuildingBalance, Integer> {

    @Query("SELECT * FROM building_balance where residential_complex_id = :residentialComplexId")
    Flux<ImmutableBuildingBalance> getAllApartmentsByResidentialComplexId(String residentialComplexId);

    @Query("SELECT * FROM building_balance where residential_complex_id = :residentialComplexId AND apartment_number = :apartmentNumber")
    Flux<ImmutableBuildingBalance> getAllByApartmentNumber(String residentialComplexId, String apartmentNumber);

    @Query("SELECT * FROM building_balance WHERE residential_complex_id = :residentialComplexId AND apartment_number = :apartmentNumber ORDER BY date DESC LIMIT 1")
    Mono<ImmutableBuildingBalance> getLastBalanceByApartment(String residentialComplexId, String apartmentNumber);
}
