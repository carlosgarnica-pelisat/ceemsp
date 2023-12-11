package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoVisitaEnum {
    ORDINARIA("ORDINARIA", "Visita ordinaria", "Visita ordinaria"),
    EXTRAORDINARIA("EXTRAORDINARIA", "Visita extraordinaria", "Visita extraordinaria"),
    INICIAL("INICIAL", "Inicial", "Inicial"),
    IMPACTO("IMPACTO", "De impacto", "De impacto");

    private String codigo;
    private String nombre;
    private String descripcion;

    TipoVisitaEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
