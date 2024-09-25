package com.comark.app.model.enums;

public enum Frecuencia {
    CADA_MES(12),
    CADA_DOS_MESES(6),
    CADA_TRES_MESES(4),
    CADA_CUATRO_MESES(3),
    CADA_SEIS_MESES(2),
    CADA_DOCE_MESES(1);

    private final int value;
    Frecuencia(int value) {
        this.value = value;
    }
    public static Frecuencia fromValue(int value) {
        return switch (value) {
            case 12 -> CADA_MES;
            case 6 -> CADA_DOS_MESES;
            case 4 -> CADA_TRES_MESES;
            case 3 -> CADA_CUATRO_MESES;
            case 2 -> CADA_SEIS_MESES;
            case 1 -> CADA_DOCE_MESES;
            default -> throw new IllegalArgumentException("frequency value is not valid");
        };
    }
    public int getValue() { return  value;}
}
