package com.comark.app.web;

import com.comark.app.model.dto.activity.ActivityPageDto;
import com.comark.app.services.ActivityService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ActivityPageDto>> getAllActivities(
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> scheduleDate,
            @RequestParam Optional<String> status,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> pageSize) {

        return activityService.getAllActivities(type, scheduleDate, status, page, pageSize)
                .map(activityPageDto -> ResponseEntity.ok().body(activityPageDto))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
