package com.pelisat.cesp.ceemsp.database.type;

public enum EstadoCivilEnum {
    SOLTERO("SOLTERO", "Soltero", "Estado civil soltero"),
    CASADO("CASADO", "Casado", "Estado civil casado"),
    DIVORCIADO("DIVORCIADO", "Divorciado", "Estado civil divorciado"),
    VIUDO("VIUDO", "Viudo", "Estado civil viudo"),
    UNION_LIBRE("UNION_LIBRE", "Union libre", "Estado civil union libre"),
    SEPARADO("SEPARADO", "Separado", "Estado civil separado");

    private String codigo;
    private String nombre;
    private String descripcion;

    EstadoCivilEnum(String codigo, String nombre, String descripcion) {
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
