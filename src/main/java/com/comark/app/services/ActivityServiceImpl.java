package com.comark.app.services;

import com.comark.app.model.dto.activity.ActivityPageDto;
import com.comark.app.repository.CustomActivityRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final CustomActivityRepository customActivityRepository;

    public ActivityServiceImpl(CustomActivityRepository customActivityRepository) {
        this.customActivityRepository = customActivityRepository;
    }

    @Override
    public Mono<ActivityPageDto> getAllActivities(Optional<String> type, Optional<String> scheduleDate, Optional<String> status, Optional<Integer> page, Optional<Integer> pageSize) {
        return customActivityRepository.getAllActivities(type, scheduleDate, status, page, pageSize);
    }
}
