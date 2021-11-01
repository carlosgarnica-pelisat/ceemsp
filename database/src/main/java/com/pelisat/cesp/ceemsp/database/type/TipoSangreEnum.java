package com.pelisat.cesp.ceemsp.database.type;

public enum TipoSangreEnum {
    O_POSITIVO("O_POSITIVO", "O+", "O Positivo"),
    O_NEGATIVO("O_NEGATIVO", "O-", "O Negativo"),
    A_POSITIVO("A_POSITIVO", "A+", "A Positivo"),
    A_NEGATIVO("A_NEGATIVO", "A-", "A Negativo"),
    B_POSITIVO("B_POSITIVO", "B+", "B Positivo"),
    B_NEGATIVO("B_NEGATIVO", "B-", "B Negativo"),
    AB_POSITIVO("AB_POSITIVO", "AB+", "AB Positivo"),
    AB_NEGATIVO("AB_NEGATIVO", "AB-", "AB Negativo");

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
