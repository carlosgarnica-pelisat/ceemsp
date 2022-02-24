package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoArchivoEnum {
    FOTOGRAFIA_PERSONA("FOTOGRAFIA_PERSONA", "Fotografia de persona", "Fotografia de persona",
            "personas/fotografias/", "persona"),
    FOTOGRAFIA_VEHICULO("FOTOGRAFIA_VEHICULO", "Fotografia de vehiculo", "Fotografia de vehiculo",
            "vehiculos/fotografias/", "vehiculo"),
    FOTOGRAFIA_CAN("FOTOGRAFIA_CAN", "Fotografia de can", "Fotografia de can",
            "canes/fotografias/", "can"),
    LICENCIA_COLECTIVA("LICENCIA_COLECTIVA", "Licencia colectiva", "Licencia colectiva para uso o portacion de armas",
            "licencias-colectivas/", "licencia"),
    CLIENTE_CONTRATO_SERVICIOS("CLIENTE_CONTRATO_SERVICIOS", "Contrato de servicios del cliente", "Contrato que firma el cliente con la ESP",
            "clientes/contratos/", "contrato"),
    ESCRITURA("ESCRITURA", "Escritura", "Documento realizado por un fedatario que constituye una empresa o un movimiento",
            "escrituras/", "escritura"),
    DOCUMENTACION_PERSONAL("DOCUMENTACION_PERSONAL", "Documentacion de personal", "Documentacion de contratacion de personal de una empresa de seguridad privada",
            "personal/","documentacion-personal"),
    CONSTANCIA_SALUD_CAN("CONSTANCIA_SALUD_CAN", "Constancia de salud canina", "Certifica y avala la salud de un can registrado en una empresa de seguridad privada",
            "canes/constancias/", "can-constancia"),
    CARTILLA_VACUNACION_CAN("CARTILLA_VACUNACION_CAN", "Cartilla de vacunacion canina", "Muestra el historial de vacunas realizadas en un can",
            "canes/cartillas/", "can-cartilla");

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
