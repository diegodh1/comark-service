package com.comark.app.web;

import com.comark.app.model.db.Activity;
import com.comark.app.model.db.ImmutableActivity;
import com.comark.app.model.dto.activity.ActivityPageDto;
import com.comark.app.model.dto.budget.BudgetItemTaskDto;
import com.comark.app.model.enums.ActivityStatus;
import com.comark.app.model.enums.ActivityType;
import com.comark.app.repository.ActivityRepository;
import com.comark.app.repository.CustomActivityRepository;
import com.comark.app.repository.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityControllerIT extends IntegrationTestBase {
    private WebTestClient webTestClient;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private CustomActivityRepository customActivityRepository;

    @BeforeEach
    void setup(){
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @AfterEach
    void clean(){
        activityRepository.deleteAll().block();
    }

    @Test
    void getAllActivities(){
        for (int i = 0; i < 20; i++){
            activityRepository.save(ImmutableActivity.builder()
                    .id(UUID.randomUUID().toString())
                    .assignedTo("assignedTo")
                    .activityType(ActivityType.PRESUPUESTO)
                    .createdAt(Instant.now().toEpochMilli())
                    .auxId("2024")
                    .details("details")
                    .scheduledDate(Instant.now().toEpochMilli())
                    .status(ActivityStatus.REALIZADO)
                    .closingDate(Instant.now().toEpochMilli())
                    .title("title")
                    .originId(UUID.randomUUID().toString())
                    .build()
            ).block();
        }
        webTestClient.get()
                .uri("/activities")
                .exchange()
                .expectStatus()
                .isOk();
    }
}
