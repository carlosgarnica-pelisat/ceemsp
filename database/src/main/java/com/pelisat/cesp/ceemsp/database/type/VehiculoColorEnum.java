package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum VehiculoColorEnum {
    ROJO("ROJO", "Rojo", "Color rojo"),
    VERDE_CLARO("VERDE_CLARO", "Verde claro", "Color verde claro"),
    VERDE_OSCURO("VERDE_OSCURO", "Verde oscuro", "Color verde oscuro"),
    AZUL_CIELO("AZUL_CIELO", "Azul cielo", "Color azul cielo"),
    AZUL_MARINO("AZUL_MARINO", "Azul marino", "Color azul marino"),
    NARANJA("NARANJA", "Naranja", "Color naranja"),
    AMARILLO("AMARILLO", "Amarillo", "Color amarillo"),
    BLANCO("BLANCO", "Blanco", "Color blanco"),
    NEGRO("NEGRO", "Negro", "Color negro"),
    GRIS_CLARO("GRIS_CLARO", "Gris claro", "Color gris claro"),
    GRIS_OSCURO("GRIS_OSCURO", "Gris oscuro", "Color gris oscuro"),
    MORADO("MORADO", "Morado", "Color morado"),
    ROSA("ROSA", "Rosa", "Color rosa");

    private String codigo;
    private String nombre;
    private String descripcion;

    VehiculoColorEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
