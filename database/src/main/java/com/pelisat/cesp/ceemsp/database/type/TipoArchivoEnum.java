package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoArchivoEnum {
    FOTOGRAFIA_PERSONA("FOTOGRAFIA_PERSONA", "Fotografia de persona", "Fotografia de persona",
            "personas/fotografias/", "persona"),
    LICENCIA_COLECTIVA("LICENCIA_COLECTIVA", "Licencia colectiva", "Licencia colectiva para uso o portacion de armas",
            "licencias-colectivas/", "licencia"),
    CLIENTE_CONTRATO_SERVICIOS("CLIENTE_CONTRATO_SERVICIOS", "Contrato de servicios del cliente", "Contrato que firma el cliente con la ESP",
            "clientes/contratos/", "contrato"),
    ESCRITURA("ESCRITURA", "Escritura", "Documento realizado por un fedatario que constituye una empresa o un movimiento",
            "escrituras", "escritura");

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
