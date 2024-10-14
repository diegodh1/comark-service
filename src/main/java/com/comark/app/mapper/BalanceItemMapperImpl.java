package com.comark.app.mapper;

import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.dto.balance.BalanceDto;
import com.comark.app.model.dto.balance.BalanceItemDto;
import com.comark.app.model.dto.balance.ImmutableBalanceDto;
import com.comark.app.model.dto.balance.ImmutableBalanceItemDto;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class BalanceItemMapperImpl implements BalanceItemMapper {

    @Override
    public BalanceDto fromBuildingBalance(BuildingBalance balance) {
        var date = Instant.ofEpochMilli(balance.date())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));


        return ImmutableBalanceDto.builder()
                .apartmentNumber(balance.apartmentNumber())
                .month(String.valueOf(date.getMonthValue()))
                .lastBalance(currencyFormatter.format(balance.lastBalance()))
                .totalToPaid(currencyFormatter.format(balance.finalCharge()))
                .details(getDetails(balance, currencyFormatter))
                .build();
    }

    private List<BalanceItemDto> getDetails(BuildingBalance balance, NumberFormat currencyFormatter){
        List<BalanceItemDto> details = new ArrayList<>();
        details.add(ImmutableBalanceItemDto.builder().title("Saldo Administración").value(currencyFormatter.format(balance.administrationCharge())).build());
        details.add(ImmutableBalanceItemDto.builder().title("Administración Mes").value(currencyFormatter.format(balance.monthCharge())).build());
        details.add(ImmutableBalanceItemDto.builder().title("Interes Mes").value(currencyFormatter.format(balance.interestBalance())).build());
        details.add(ImmutableBalanceItemDto.builder().title("Cuota Extra").value(currencyFormatter.format(balance.additionalCharge())).build());
        details.add(ImmutableBalanceItemDto.builder().title("Multas/Sanciones").value(currencyFormatter.format(balance.penaltyCharge())).build());
        details.add(ImmutableBalanceItemDto.builder().title("Juridico").value(currencyFormatter.format(balance.legalCharge())).build());
        details.add(ImmutableBalanceItemDto.builder().title("Otros").value(currencyFormatter.format(balance.otherCharge())).build());
        return details;
    }
}
