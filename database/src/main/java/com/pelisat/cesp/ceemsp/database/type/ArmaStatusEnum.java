package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum ArmaStatusEnum {
    ACTIVA("ACTIVA", "El arma se encuentra activa"),
    DEPOSITO("DEPOSITO", "El arma se encuentra en deposito"),
    ASIGNADA("ASIGNADA", "El arma esta asignada a un elemento"),
    CUSTODIA("CUSTODIA", "El arma esta custodiada o resguardada por el MP");

    private String codigo;
    private String nombre;

    ArmaStatusEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
