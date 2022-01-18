package com.pelisat.cesp.ceemsp.database.type;

public enum TipoSangreEnum {
    O_P("O_POSITIVO", "O+", "O Positivo"),
    O_N("O_NEGATIVO", "O-", "O Negativo"),
    A_P("A_POSITIVO", "A+", "A Positivo"),
    A_N("A_NEGATIVO", "A-", "A Negativo"),
    B_P("B_POSITIVO", "B+", "B Positivo"),
    B_N("B_NEGATIVO", "B-", "B Negativo"),
    AB_P("AB_POSITIVO", "AB+", "AB Positivo"),
    AB_N("AB_NEGATIVO", "AB-", "AB Negativo");

    private String codigo;
    private String nombre;
    private String descripcion;

    TipoSangreEnum(String codigo, String nombre, String descripcion) {
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
