package com.comark.app.services.impl;

import com.comark.app.mapper.BalanceItemMapper;
import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.dto.balance.BalanceDto;
import com.comark.app.model.dto.balance.ImmutableBalanceDto;
import com.comark.app.repository.BuildingBalanceRepository;
import com.comark.app.services.BalanceService;
import com.comark.app.services.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class BalanceServiceImpl implements BalanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceServiceImpl.class);
    private final FileUtil fileUtil;
    private final BuildingBalanceRepository balanceRepository;
    private final BalanceItemMapper balanceItemMapper;

    public BalanceServiceImpl(FileUtil fileUtil, BuildingBalanceRepository balanceRepository, BalanceItemMapper balanceItemMapper) {
        this.fileUtil = fileUtil;
        this.balanceRepository = balanceRepository;
        this.balanceItemMapper = balanceItemMapper;
    }

    @Override
    public Mono<Success> upsertBalance(byte[] file, String actorId) {
        return fileUtil.loadBuildingBalanceFromFile(file)
                .flatMap(buildingBalances -> balanceRepository.saveAll(buildingBalances).collectList())
                .then(Mono.just(ImmutableSuccess.builder().success(true).build()));
    }

    @Override
    public Mono<List<BuildingBalance>> getAllBalanceReports() {
        return balanceRepository.getAllApartments().cast(BuildingBalance.class).collectList();
    }

    @Override
    public Mono<List<BuildingBalance>> getBalanceReportsByApartmentNumber(String ApartmentNumber) {
        return balanceRepository.getAllByApartmentNumber(ApartmentNumber).cast(BuildingBalance.class).collectList();
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
