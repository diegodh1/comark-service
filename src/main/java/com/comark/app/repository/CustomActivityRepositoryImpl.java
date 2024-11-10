package com.comark.app.repository;

import com.comark.app.mapper.PqrMapper;
import com.comark.app.model.db.Activity;
import com.comark.app.model.db.ImmutableActivity;
import com.comark.app.model.dto.activity.ActivityDto;
import com.comark.app.model.dto.activity.ActivityPageDto;
import com.comark.app.model.dto.activity.ImmutableActivityDto;
import com.comark.app.model.dto.activity.ImmutableActivityPageDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CustomActivityRepositoryImpl implements CustomActivityRepository {
    private final R2dbcEntityTemplate template;
    private final int PAGE_SIZE = 100; // Default page size, can be changed


    public CustomActivityRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<ActivityPageDto> getAllActivities(Optional<String> type, Optional<String> scheduleDate, Optional<String> status, Optional<Integer> page, Optional<Integer> pageSize){
        int currentPage = page.orElse(0);
        int offset = currentPage * pageSize.orElse(PAGE_SIZE);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        // Building criteria for query
        Criteria criteria = Criteria.empty();
        if (type.isPresent()) {
            criteria = criteria.and("activity_type").is(type.get());
        }
        if (scheduleDate.isPresent()) {
            LocalDate parsedDate = LocalDate.parse(scheduleDate.get(), dateFormatter);
            var epochTime = parsedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
            criteria = criteria.and("scheduled_date").is(epochTime);
        }
        if (status.isPresent()) {
            criteria = criteria.and("status").is(status.get());
        }
        if (scheduleDate.isEmpty()) {
            Instant tomorrow = Instant.now().plus(1, ChronoUnit.DAYS);
            criteria = criteria.and("created_at").lessThanOrEquals(tomorrow.toEpochMilli());
        }
        Query query = Query.query(criteria)
                .sort(Sort.by("created_at").descending())
                .limit(pageSize.orElse(PAGE_SIZE))
                .offset(offset);

        // Fetch paginated results and total count
        Mono<List<ImmutableActivity>> activities = template.select(ImmutableActivity.class)
                .matching(query)
                .all()
                .collectList();

        Mono<Long> totalCount = template.count(Query.query(criteria), Activity.class);

        return activities.zipWith(totalCount).map(tuple -> {
            List<ImmutableActivity> activityList = tuple.getT1();
            long totalRecords = tuple.getT2();

            // Calculating pagination links

            int totalPages = (int) Math.ceil((double) totalRecords / pageSize.orElse(PAGE_SIZE));
            var prevPage = currentPage > 0 ?  (currentPage - 1) : 0;
            var nextPage = currentPage < totalPages - 1 ? (currentPage + 1) : 0;
            return ImmutableActivityPageDto.builder().activities(activityList.stream().map(it -> ImmutableActivityDto.builder()
                            .assignedTo(it.assignedTo())
                            .auxId(it.auxId())
                            .details(it.details())
                            .origenId(it.originId())
                            .status(it.status().name())
                            .title(it.title())
                            .createdAt(getDate(it.createdAt()))
                            .scheduledDate(getDate(it.scheduledDate()))
                            .closingDate(Optional.ofNullable(it.closingDate()).map(this::getDate).orElse(""))
                            .durationInDays(getDaysBetween(it.scheduledDate(), Optional.ofNullable(it.closingDate()).orElse(Instant.now().toEpochMilli())))
                            .type(it.activityType().name())
                            .build()
                    ).toList())
                    .nextPage(nextPage)
                    .previousPage(prevPage)
                    .build();
        });
    }

    private String getDate(long date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var currentDate = Instant.ofEpochMilli(date)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        return currentDate.format(formatter);
    }
    private int getDaysBetween(Long createdDate, Long responseDate){
        var localDate1 =  Instant.ofEpochMilli(createdDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        var localDate2 =  Instant.ofEpochMilli(responseDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        return (int) Math.max(0, ChronoUnit.DAYS.between(localDate1, localDate2));
    }
}
