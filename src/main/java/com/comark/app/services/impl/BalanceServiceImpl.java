package com.comark.app.services.impl;

import com.comark.app.mapper.BalanceItemMapper;
import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.db.ImmutableActivity;
import com.comark.app.model.dto.balance.BalanceDto;
import com.comark.app.model.dto.balance.BalanceItemReportDto;
import com.comark.app.model.dto.balance.ImmutableBalanceDto;
import com.comark.app.model.enums.ActivityStatus;
import com.comark.app.model.enums.ActivityType;
import com.comark.app.repository.ActivityRepository;
import com.comark.app.repository.BuildingBalanceRepository;
import com.comark.app.services.BalanceService;
import com.comark.app.services.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class BalanceServiceImpl implements BalanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceServiceImpl.class);
    private final FileUtil fileUtil;
    private final BuildingBalanceRepository balanceRepository;
    private final BalanceItemMapper balanceItemMapper;
    private final ActivityRepository activityRepository;

    public BalanceServiceImpl(FileUtil fileUtil, BuildingBalanceRepository balanceRepository, BalanceItemMapper balanceItemMapper, ActivityRepository activityRepository) {
        this.fileUtil = fileUtil;
        this.balanceRepository = balanceRepository;
        this.balanceItemMapper = balanceItemMapper;
        this.activityRepository = activityRepository;
    }

    @Override
    public Mono<Success> upsertBalance(byte[] file, String actorId) {
        var localDateTime = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return fileUtil.loadBuildingBalanceFromFile(file)
                .flatMap(buildingBalances -> balanceRepository.saveAll(buildingBalances).collectList())
                .flatMap(ignore -> activityRepository.save(ImmutableActivity.builder()
                        .originId(String.format("building balance - %s", localDateTime.format(formatter)))
                        .auxId(localDateTime.format(formatter))
                        .scheduledDate(Instant.now().toEpochMilli())
                        .assignedTo("ADMINISTRADOR")
                        .closingDate(Instant.now().toEpochMilli())
                        .createdAt(Instant.now().toEpochMilli())
                        .id(UUID.randomUUID().toString())
                        .activityType(ActivityType.CARTERA)
                        .status(ActivityStatus.REALIZADO)
                        .title("archivo de cartera actualizado")
                        .details(String.format("se ha generado una nueva versión, fecha %s", localDateTime.format(formatter)))
                        .build()
                ))
                .then(Mono.just(ImmutableSuccess.builder().success(true).build()));
    }

    @Override
    public Mono<List<BuildingBalance>> getAllBalanceReports() {
        return balanceRepository.getAllApartments().cast(BuildingBalance.class).collectList();
    }

    @Override
    public Mono<List<BalanceItemReportDto>> getBalanceReportsByApartmentNumber(String ApartmentNumber) {
        return balanceRepository.getAllByApartmentNumber(ApartmentNumber)
                .flatMap(balanceList -> Mono.just(balanceItemMapper.fromBuildingBalanceListToBalanceItemReportDto(balanceList)))
                .collectList()
                .switchIfEmpty(Mono.just(List.of()));
    }

    @Override
    public Mono<BalanceDto> getMonthBalance(String apartmentNumber) {
        return balanceRepository.getLastBalanceByApartment(apartmentNumber)
                .flatMap(lastBalance -> Mono.just(balanceItemMapper.fromBuildingBalance(lastBalance)))
                .switchIfEmpty(Mono.just(emptyBalanceDto(apartmentNumber)))
                .cast(BalanceDto.class);
    }

    private BalanceDto emptyBalanceDto(String ApartmentNumber) {
        var date = LocalDate.now();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return ImmutableBalanceDto.builder()
                .apartmentNumber(ApartmentNumber)
                .month(String.valueOf(date.getMonthValue()))
                .lastBalance(currencyFormatter.format(0))
                .totalToPaid(currencyFormatter.format(0))
                .details(new ArrayList<>())
                .build();
    }
}
