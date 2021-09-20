package com.pelisat.cesp.ceemsp.database.type;

public enum TipoPersonaEnum {
    FISICA("FISICA", "Persona fisica", "Persona fisica que representa la empresa"),
    MORAL("MORAL", "Persona moral", "Constituida por una escritura publica");

    private String codigo;
    private String nombre;
    private String descripcion;

    TipoPersonaEnum(String codigo, String nombre, String descripcion) {
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
