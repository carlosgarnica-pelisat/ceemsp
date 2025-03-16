package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum VehiculoTipoEnum {

    AUTOMOVIL("AUTOMOVIL", "Automovil", "Automovil"),
    MOTOCICLETA("MOTOCICLETA", "Motocicleta", "Motocicleta"),
    AUTOBUS("AUTOBUS", "Autobus", "Autobus");

    private String codigo;
    private String nombre;
    private String descripcion;

    VehiculoTipoEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
