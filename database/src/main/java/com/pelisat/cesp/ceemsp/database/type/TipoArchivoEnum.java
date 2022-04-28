package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum TipoArchivoEnum {
    FOTOGRAFIA_PERSONA("FOTOGRAFIA_PERSONA", "Fotografia de persona", "Fotografia de persona",
            "personal/fotografias/", "persona"),
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
            "personal/documentacion","documentacion-personal"),
    CERTIFICACION_PERSONAL("CERTIFICACION_PERSONAL", "Certificacion del personal", "Constancia de certificacion del personal",
            "personal/certificacion/", "certificacion"),
    CONSTANCIA_SALUD_CAN("CONSTANCIA_SALUD_CAN", "Constancia de salud canina", "Certifica y avala la salud de un can registrado en una empresa de seguridad privada",
            "canes/constancias/", "can-constancia"),
    CARTILLA_VACUNACION_CAN("CARTILLA_VACUNACION_CAN", "Cartilla de vacunacion canina", "Muestra el historial de vacunas realizadas en un can",
            "canes/cartillas/", "can-cartilla"),
    VISITA_ARCHIVO("VISITA_ARCHIVO", "Visita archivo", "Evidencias de una visita hecha registradas en archivos de cualquier tipo",
            "visitas/", "visita"),
    DOCUMENTO_FUNDATORIO_BAJA_DOMICILIO("DOCUMENTO_FUNDATORIO_BAJA_DOMICILIO", "Documento fundatorio de baja de domicilio", "Documento que verifica o refuerza la razon de baja de un domicilio. No obligatorio",
            "domicilios/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_CAN("DOCUMENTO_FUNDATORIO_BAJA_CAN", "Documento fundatorio de baja de can", "Documento que verifica o refuerza la razon de baja de un can. No obligatorio",
            "canes/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_VEHICULO("DOCUMENTO_FUNDATORIO_BAJA_VEHICULO", "Documento fundatorio de baja de vehiculo", "Documento que verifica o refuerza la razon de baja de un vehiculo. No obligatorio",
            "vehiculos/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_CLIENTE("DOCUMENTO_FUNDATORIO_BAJA_CLIENTE", "Documento fundatorio de baja de cliente", "Documento que verifica o refuerza la razon de baja de un cliente. No obligatorio",
            "clientes/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_PERSONAL("DOCUMENTO_FUNDATORIO_BAJA_PERSONAL", "Documento fundatorio de baja de personal", "Documento que verifica o refuerza la razon de baja de una persona. No obligatorio",
            "personal/documentos-fundatorios/", "fundatorio");

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
