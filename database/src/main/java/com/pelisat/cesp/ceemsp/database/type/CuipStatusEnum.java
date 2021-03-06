package com.pelisat.cesp.ceemsp.database.type;

public enum CuipStatusEnum {
    EN_TRAMITE("EN_TRAMITE", "En tramite"),
    TRAMITADO("TRAMITADO", "Tramitado"),
    NA("NA", "No aplica. Para personal administrativo");

    private String codigo;
    private String nombre;

    CuipStatusEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
