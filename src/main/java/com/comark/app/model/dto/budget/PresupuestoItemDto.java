package com.comark.app.model.dto.budget;

import com.comark.app.model.enums.Frecuencia;
import com.comark.app.model.enums.PresupuestoTipo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Date;


@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(as = ImmutablePresupuestoItemDto.class)
@JsonDeserialize(as = ImmutablePresupuestoItemDto.class)
public interface PresupuestoItemDto {
    PresupuestoTipo getTipo();
    String getNombre();
    String getDetalle();
    Double getPresupuesto();
    Frecuencia getFrecuencia();
    Date getFechaInicio();
    String getCuentaContableId();
}
