package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum IncidenciaStatusEnum {
    ABIERTA("ABIERTA", "Abierta", "La incidencia esta abierta sin asignar"),
    ASIGNADA("ASIGNADA", "Asignada", "La incidencia esta asignada"),
    DOCUMENTO_PENDIENTE("DOCUMENTO_PENDIENTE", "Documentacion pendiente", "La incidencia requiere documentacion para continuar"),
    PROCEDENTE("PROCEDENTE", "Procedente", "La incidencia se ha cerrado con resolucion satisfactoria a la empresa"),
    IMPROCEDENTE("IMPROCEDENTE", "Improcedente", "La incidencia se ha cerrado con resolucion no satisfactoria a la empresa");

    private String codigo;
    private String nombre;
    private String descripcion;

    IncidenciaStatusEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
