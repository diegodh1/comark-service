package com.comark.app.repository;

import com.comark.app.model.dto.activity.ActivityDto;
import com.comark.app.model.dto.activity.ActivityPageDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface CustomActivityRepository {
    Mono<ActivityPageDto>  getAllActivities(Optional<String> type, Optional<String> scheduleDate, Optional<String> status, Optional<Integer> page, Optional<Integer> pageSize);

}
