package com.pelisat.cesp.ceemsp.database.type;

public enum TipoFedatarioEnum {
    CORREDOR("CORREDOR", "Corredor público"),
    NOTARIO("NOTARIO", "Notario público");

    private String codigo;
    private String nombre;

    TipoFedatarioEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
