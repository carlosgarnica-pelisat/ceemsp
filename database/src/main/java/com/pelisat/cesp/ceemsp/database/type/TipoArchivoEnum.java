package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoArchivoEnum {
    FOTOGRAFIA_PERSONA("FOTOGRAFIA_PERSONA", "Fotografia de persona", "Fotografia de persona",
            "personas/fotografias/", "persona");

    private String codigo;
    private String nombre;
    private String descripcion;
    private String rutaCarpeta;
    private String prefijoArchivo;

    TipoArchivoEnum(String codigo, String nombre, String descripcion, String rutaCarpeta, String prefijoArchivo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rutaCarpeta = rutaCarpeta;
        this.prefijoArchivo = prefijoArchivo;
    }
}
