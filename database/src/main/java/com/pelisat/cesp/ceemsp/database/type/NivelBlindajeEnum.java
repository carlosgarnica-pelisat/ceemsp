package com.pelisat.cesp.ceemsp.database.type;

public enum NivelBlindajeEnum {
    NIVEL_1("NIVEL_1", "Blindaje nivel 1", "Blindaje nivel 1"),
    NIVEL_2("NIVEL_2", "Blindaje nivel 2", "Blindaje nivel 2"),
    NIVEL_3("NIVEL_3", "Blindaje nivel 3", "Blindaje nivel 3"),
    NIVEL_4("NIVEL_4", "Blindaje nivel 4", "Blindaje nivel 4"),
    NIVEL_5("NIVEL_5", "Blindaje nivel 5", "Blindaje nivel 5"),
    NIVEL_6("NIVEL_6", "Blindaje nivel 6", "Blindaje nivel 6"),
    NIVEL_7("NIVEL_7", "Blindaje nivel 7", "Blindaje nivel 7");

    private final String codigo;
    private final String nombre;
    private final String descripcion;

    NivelBlindajeEnum(String codigo, String nombre, String descripcion) {
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
