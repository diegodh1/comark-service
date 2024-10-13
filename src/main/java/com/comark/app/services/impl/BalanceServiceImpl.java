package com.comark.app.services.impl;

import com.comark.app.model.ImmutableSuccess;
import com.comark.app.model.Success;
import com.comark.app.model.db.BuildingBalance;
import com.comark.app.repository.BuildingBalanceRepository;
import com.comark.app.services.BalanceService;
import com.comark.app.services.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BalanceServiceImpl implements BalanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceServiceImpl.class);
    private final FileUtil fileUtil;
    private final BuildingBalanceRepository balanceRepository;

    public BalanceServiceImpl(FileUtil fileUtil, BuildingBalanceRepository balanceRepository) {
        this.fileUtil = fileUtil;
        this.balanceRepository = balanceRepository;
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
}
