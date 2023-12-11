package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum BusquedaFiltroEnum {
    EMPRESAS("EMPRESAS", "Busqueda por empresas"),
    CLIENTES("CLIENTES", "Arma larga"),
    PERSONAL("PERSONAL", "Arma larga"),
    CANES("CANES", "Arma larga"),
    VEHICULOS("VEHICULOS", "Arma larga"),
    ARMAS("ARMAS", "Arma larga"),
    ESCRITURAS("ESCRITURAS", "Arma larga"),
    INCIDENCIAS("INCIDENCIAS", "Arma larga");

    private String codigo;
    private String nombre;

    BusquedaFiltroEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
