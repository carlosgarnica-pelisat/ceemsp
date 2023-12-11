package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum ReporteArgosStatusEnum {
    PENDIENTE("PENDIENTE", "Pendiente", "Pendiente"),
    PROCESANDO("PROCESANDO", "Procesando", "Procesando"),
    ERROR("ERROR", "Error", "Error"),
    COMPLETADO("COMPLETADO", "Completado", "Completado");

    private String codigo;
    private String nombre;
    private String descripcion;

    ReporteArgosStatusEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

}
