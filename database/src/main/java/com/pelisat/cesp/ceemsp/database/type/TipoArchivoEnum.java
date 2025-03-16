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
    FOTO_UNIFORME_ELEMENTO("FOTO_UNIFORME_ELEMENTO", "Fotografia del uniforme de elemento", "Fotografia para mostrar el elemento del uniforme",
            "uniformes/elementos/", "uniforme-elemento"),
    FOTO_UNIFORME("FOTO_UNIFORME", "Fotografia del uniforme general", "Fotografia para mostrar el uniforme completo",
            "uniformes/", "uniforme"),
    DOCUMENTACION_PERSONAL("DOCUMENTACION_PERSONAL", "Documentacion de personal", "Documentacion de contratacion de personal de una empresa de seguridad privada",
            "personal/documentacion","documentacion-personal"),
    CERTIFICACION_PERSONAL("CERTIFICACION_PERSONAL", "Certificacion del personal", "Constancia de certificacion del personal",
            "personal/certificacion/", "certificacion"),
    CONSTANCIA_SALUD_CAN("CONSTANCIA_SALUD_CAN", "Constancia de salud canina", "Certifica y avala la salud de un can registrado en una empresa de seguridad privada",
            "canes/constancias/", "can-constancia"),
    CARTILLA_VACUNACION_CAN("CARTILLA_VACUNACION_CAN", "Cartilla de vacunacion canina", "Muestra el historial de vacunas realizadas en un can",
            "canes/cartillas/", "can-cartilla"),
    CONSTANCIA_ADIESTRAMIENTO_CAN("CONSTANCIA_ADIESTRAMIENTO_CAN", "Constancia de adiestramiento canino", "Muestra las constancias de adiestramiento realizadas en un can",
            "canes/constancias-adiestramiento/", "can-constancia-adiestramiento"),
    VISITA_ARCHIVO("VISITA_ARCHIVO", "Visita archivo", "Evidencias de una visita hecha registradas en archivos de cualquier tipo",
            "visitas/", "visita"),
    DOCUMENTO_FUNDATORIO_BAJA_DOMICILIO("DOCUMENTO_FUNDATORIO_BAJA_DOMICILIO", "Documento fundatorio de baja de domicilio", "Documento que verifica o refuerza la razon de baja de un domicilio. No obligatorio",
            "domicilios/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_SOCIO("DOCUMENTO_FUNDATORIO_BAJA_SOCIO", "Documento fundatorio de baja de socio", "Documento que verifica o refuerza la razon de baja de un socio. No obligatorio",
            "escrituras/socios/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_APODERADO("DOCUMENTO_FUNDATORIO_BAJA_APODERADO", "Documento fundatorio de baja de apoderado", "Documento que verifica o refuerza la razon de baja de un apoderado. No obligatorio",
            "escrituras/apoderados/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_REPRESENTANTE("DOCUMENTO_FUNDATORIO_BAJA_REPRESENTANTE", "Documento fundatorio de baja de representante", "Documento que verifica o refuerza la razon de baja de un representante. No obligatorio",
            "escrituras/representantes/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_CONSEJO("DOCUMENTO_FUNDATORIO_BAJA_CONSEJO", "Documento fundatorio de baja de consejo", "Documento que verifica o refuerza la razon de baja de un consejo. No obligatorio",
            "escrituras/consejos/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_ACUERDO("DOCUMENTO_FUNDATORIO_BAJA_ACUERDO", "Documento fundatorio de baja de acuerdo", "Documento que verifica o refuerza la razon de baja de un acuerdo. No obligatorio",
            "acuerdos/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_LICENCIA("DOCUMENTO_FUNDATORIO_BAJA_LICENCIA", "Documento fundatorio de baja de licencia", "Documento que verifica o refuerza la razon de baja de un licencia. No obligatorio",
            "licencias-colectivas/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_CAN("DOCUMENTO_FUNDATORIO_BAJA_CAN", "Documento fundatorio de baja de can", "Documento que verifica o refuerza la razon de baja de un can. No obligatorio",
            "canes/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_VEHICULO("DOCUMENTO_FUNDATORIO_BAJA_VEHICULO", "Documento fundatorio de baja de vehiculo", "Documento que verifica o refuerza la razon de baja de un vehiculo. No obligatorio",
            "vehiculos/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_CLIENTE("DOCUMENTO_FUNDATORIO_BAJA_CLIENTE", "Documento fundatorio de baja de cliente", "Documento que verifica o refuerza la razon de baja de un cliente. No obligatorio",
            "clientes/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_PERSONAL("DOCUMENTO_FUNDATORIO_BAJA_PERSONAL", "Documento fundatorio de baja de personal", "Documento que verifica o refuerza la razon de baja de una persona. No obligatorio",
            "personal/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_INCIDENCIA("DOCUMENTO_FUNDATORIO_INCIDENCIA", "Documento fundatorio de incidencia", "Documento que verifica o refuerza la naturaleza de la incidencia. No obligatorio",
            "incidencias/documentos-fundatorios/", "fundatorio"),
    DOCUMENTO_FUNDATORIO_BAJA_ARMA("DOCUMENTO_FUNDATORIO_BAJA_ARMA", "Documento fundatorio de baja de arma", "Documento que verifica o refuerza la baja del arma. No obligatorio",
            "armas/documentos-fundatorios/", "fundatorio"),
    CONSTANCIA_BLINDAJE_VEHICULO("CONSTANCIA_BLINDAJE_VEHICULO", "Constancia de blindaje de vehiculo", "Documento que valida el blindaje de un vehiculo. No obligatorio",
            "vehiculos/constancias-blindaje/", "constancia-blindaje"),
    EMPRESA_ACUERDO("EMPRESA_ACUERDO", "Acuerdo","Documento que respalda el acuerdo de registro de una empresa de seguridad privada",
            "acuerdos/", "acuerdo"),
    EMPRESA_REGISTRO_FEDERAL("EMPRESA_REGISTRO_FEDERAL", "Documento de registro federal", "Documento que avala el registro federal de una empresa de seguridad privada",
            "registros-federales/", "regsitro-federal"),
    VOLANTE_CUIP("VOLANTE_CUIP", "Volante CUIP", "Documento que consta la solicitud de un CUIP",
            "personal/volantes-cuip/", "volante-cuip"),
    LOGO_EMPRESA("LOGO_EMPRESA", "Logo empresa", "Logotipo de la empresa",
            "empresas/logos/", "empresa-logo"),
    REPORTE_ARGOS("REPORTE_ARGOS", "Reporte argos", "Reporte asincrono de argos",
            "reportes/", "reporte-argos");

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
