package com.pelisat.cesp.ceemsp.database.type;

public enum SexoEnum {
    MASCULINO("MASCULINO", "Sexo Masculino"),
    FEMENINO("FEMENINO", "Sexo femenino"),
    NA("NA", "No aplica. Ejemplo personas morales");

    private String codigo;
    private String nombre;

    SexoEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
