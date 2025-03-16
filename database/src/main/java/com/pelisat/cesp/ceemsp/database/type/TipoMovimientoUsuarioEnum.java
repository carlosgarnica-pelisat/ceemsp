package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoMovimientoUsuarioEnum {
    EMPRESA("EMPRESA", "Empresa"),
    EMPRESA_FORMAS_EJECUCION("EMPRESA_FORMAS_EJECUCION", "Empresa formas ejecucion"),
    ESCRITURAS("ESCRITURAS", "Escrituras"),
    SOCIOS("SOCIOS", "Socios"),
    APODERADOS("APODERADOS", "Apoderados"),
    REPRESENTANTES("REPRESENTANTES", "Representantes"),
    CONSEJO("CONSEJO", "Consejo");

    private String codigo;
    private String nombre;

    TipoMovimientoUsuarioEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
