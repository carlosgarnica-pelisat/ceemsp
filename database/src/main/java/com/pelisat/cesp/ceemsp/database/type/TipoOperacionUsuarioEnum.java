package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoOperacionUsuarioEnum {
    ALTA("ALTA", "Alta"),
    MODIFICACION("MODIFICACION", "Modificacion"),
    ELIMINACION("ELIMINACION", "Eliminacion");

    private String codigo;
    private String nombre;

    TipoOperacionUsuarioEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
