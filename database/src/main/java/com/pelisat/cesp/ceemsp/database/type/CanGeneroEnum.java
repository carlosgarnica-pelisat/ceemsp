package com.pelisat.cesp.ceemsp.database.type;

public enum CanGeneroEnum {
    MACHO("MACHO", "Can de genero macho"),
    HEMBRA("HEMBRA", "Can de genero hembra");

    private String codigo;
    private String nombre;

    CanGeneroEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
