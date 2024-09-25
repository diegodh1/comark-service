package com.comark.app.model.enums;

public enum PresupuestoTipo {
    INGRESOS,
    RUBROS_DE_LEY,
    GASTOS_DIVERSOS,
    HONORARIOS,
    SERVICIOS,
    GASTOS_FINANCIEROS,
    MANTENIMIENTOS_REPARACIONES;

    public static PresupuestoTipo getPresupuestoTipo(String type) {
        try {
            return PresupuestoTipo.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("PresupuestoTipo is not valid");
        }
    }
}
