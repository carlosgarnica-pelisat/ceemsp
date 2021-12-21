package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum ArmaTipoEnum {
    CORTA("CORTA", "Arma corta"),
    LARGA("LARGA", "Arma larga");

    private String codigo;
    private String nombre;

    ArmaTipoEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
