package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum EmpresaStatusEnum {
    ACTIVA("ACTIVA", "Activa", "La empresa se encuentra activa y en funcionamiento"),
    SUSPENDIDA("SUSPENDIDA", "Suspendida", "La empresa se encuentra suspendida por tiempo determinado"),
    REVOCADA("REVOCADA", "Revocada", "La empresa ha sido revocada"),
    CLAUSURADA("CLAUSURADA", "Clausurada", "La empresa ha sido clausurada"),
    PERDIDA_EFICACIA("PERDIDA_EFICACIA", "Perdida de eficacia", "La empresa ha perdido su eficacia");

    private String codigo;
    private String nombre;
    private String descripcion;

    EmpresaStatusEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
