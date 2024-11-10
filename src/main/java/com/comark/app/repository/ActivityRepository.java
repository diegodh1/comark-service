package com.comark.app.repository;

import com.comark.app.model.db.Activity;
import com.comark.app.model.db.BudgetItem;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ActivityRepository extends ReactiveCrudRepository<Activity, String> {
    @Modifying
    @Query("UPDATE activity SET closing_date = :closingDate, status = :status WHERE origin_id = :originId AND activity_type = :type")
    Mono<Integer> updateActivity(long closingDate, String status, String originId, String type);
}
