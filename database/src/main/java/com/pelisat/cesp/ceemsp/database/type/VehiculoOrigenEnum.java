package com.pelisat.cesp.ceemsp.database.type;

public enum VehiculoOrigenEnum {
    PROPIO("PROPIO", "Propio", "Vehiculo de origen propio"),
    COMODATO("COMODATO", "Comodato", "Vehiculo por contrato de comodato"),
    ARRENDAMIENTO("ARRENDAMIENTO", "Arrendamiento", "Vehiculo por contrato de arrendamiento");

    private final String codigo;
    private final String nombre;
    private final String descripcion;

    VehiculoOrigenEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
