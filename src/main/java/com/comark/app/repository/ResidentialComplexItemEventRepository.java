package com.comark.app.repository;

import com.comark.app.model.db.ImmutableBudgetItemTask;
import com.comark.app.model.db.ImmutableResidentialComplexItemEvent;
import com.comark.app.model.db.ResidentialComplexItemEvent;
import com.comark.app.model.enums.EventStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResidentialComplexItemEventRepository extends ReactiveCrudRepository<ResidentialComplexItemEvent, String> {
    @Query("SELECT * FROM residential_complex_item_event WHERE event_status = :status AND start_date_time >= :startDateTime AND end_date_time <= :endDateTime")
    Flux<ImmutableResidentialComplexItemEvent> findAllResidentialComplexItemEventsByStatusAndDateRange(String status, Long startDateTime, Long endDateTime);

    @Query("SELECT * FROM residential_complex_item_event WHERE event_status = :status AND residential_complex_id = :residentialComplexId")
    Flux<ImmutableResidentialComplexItemEvent> findAllResidentialComplexItemEventsByStatus(String status, String residentialComplexId);

    @Query("update residential_complex_item_event set event_status = :eventStatus where id = :id")
    Mono<ImmutableResidentialComplexItemEvent> updateStatus(String eventStatus, String id);
}