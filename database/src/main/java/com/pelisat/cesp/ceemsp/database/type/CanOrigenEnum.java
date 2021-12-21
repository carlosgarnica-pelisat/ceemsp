package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum CanOrigenEnum {
    PROPIO("PROPIO", "Origen propio", "Consiste en que el can es de un origen propio"),
    COMODATO("COMODATO", "Origen por contrato de comodato", "Consiste en el "),
    ARRENDAMIENTO("ARRENDAMIENTO", "Origen por contrato de arrendamiento", "Consiste en arrendamiento"),
    PRESTACION_SERVICIOS("PRESTACION_SERVICIOS", "Origen por contrato de prestacion de servicios", "");

    private String codigo;
    private String nombre;
    private String descripcion;

    CanOrigenEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
