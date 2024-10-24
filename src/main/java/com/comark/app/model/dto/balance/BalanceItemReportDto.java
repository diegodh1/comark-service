package com.comark.app.model.dto.balance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(as = ImmutableBalanceItemReportDto.class)
@JsonDeserialize(as =ImmutableBalanceItemReportDto.class)
public interface BalanceItemReportDto {
    String apartment();
    String fecha();
    String saldoAdmon();
    String admonMes();
    String porcentajeInteres();
    String saldoInteres();
    String cuotaExtra();
    String multaSanciones();
    String juridico();
    String saldoAnterior();
    String otros();
    String pagar();
    String descuentos();
    String pagos();
    String saldoFinal();
}
