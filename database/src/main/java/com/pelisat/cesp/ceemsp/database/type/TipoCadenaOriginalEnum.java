package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoCadenaOriginalEnum {
    CORREO_ALTA_EMPRESA("CORREO_ALTA_EMPRESA", "1");

    private String codigo;
    private String version;

    TipoCadenaOriginalEnum(String codigo, String version) {
        this.codigo = codigo;
    }
}
