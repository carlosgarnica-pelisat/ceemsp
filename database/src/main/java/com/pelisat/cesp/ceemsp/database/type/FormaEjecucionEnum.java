package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum FormaEjecucionEnum {
    ARMAS("ARMAS", "Con portacion de armas de fuego", "Con portacion de armas de fuego"),
    CANES("CANES", "Con el auxilio de canes", "Con el auxilio de canes"),
    INSTRUMENTOS("INSTRUMENTOS", "Con portacion de instrumentos de apoyo para control", "Con portacion de instrumentos de apoyo para control"),
    TECNOLOGIA("TECNOLOGIA", "Con apoyo de tecnologia", "Con apoyo de tecnologia"),
    NA("NA", "No aplica", "No aplica forma de ejecucion");

    private String codigo;
    private String nombre;
    private String descripcion;

    FormaEjecucionEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
