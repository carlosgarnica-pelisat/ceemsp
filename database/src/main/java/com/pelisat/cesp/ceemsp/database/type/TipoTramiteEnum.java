package com.pelisat.cesp.ceemsp.database.type;

public enum TipoTramiteEnum {
    SPSMD("SPSMD", "Registro estatal", "Tipo de tramite enfocado para la prestacion de servicios a nivel estatal"),
    EAFJAL("EAFJAL", "Registro federal", "Tipo de tramite enfocado para la prestacion de servicios a nivel federal"),
    RESPRO("RESPRO", "Registro de servicios propios", "Tipo de tramite enfocado para los servicios propios"),
    XXXXXX("XXXXXX", "Autorizacion provisional", "Autorizacion provisional, pendiente de definir siglas");

    private String codigo;
    private String nombre;
    private String descripcion;

    TipoTramiteEnum(String codigo, String nombre, String descripcion) {
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
