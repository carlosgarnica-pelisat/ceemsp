package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum CanGeneroEnum {
    MACHO("MACHO", "Can de genero macho"),
    HEMBRA("HEMBRA", "Can de genero hembra");

    private String codigo;
    private String nombre;

    CanGeneroEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
