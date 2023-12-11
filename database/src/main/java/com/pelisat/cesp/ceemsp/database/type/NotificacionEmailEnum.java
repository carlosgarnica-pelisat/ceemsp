package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum NotificacionEmailEnum {
    EMPRESA_REGISTRADA("EMPRESA_REGISTRADA", "Su empresa se ha registrado con exito", "empresa-nueva.ftlh"),
    NOTIFICACION("NOTIFICACION", "Has recibido una nueva notificacion del Sistema Integral de Empresas de Seguridad Privada (SIESP)", "notificacion.ftlh"),
    NUEVA_INCIDENCIA("NUEVA_INCIDENCIA", "Se ha registrado una nueva incidencia", "incidencia-nueva.ftlh"),
    INCIDENCIA_ACCION_PENDIENTE("INCIDENCIA_ACCION_PENDIENTE", "Acción pendiente en la incidencia", "incidencia-accion-pendiente.ftlh"),
    INCIDENCIA_RESPONDIDA("INCIDENCIA_RESPONDIDA", "Incidencia contestada por la empresa", "incidencia-respondida.ftlh"),
    INCIDENCIA_PROCEDENTE("INCIDENCIA_PROCEDENTE", "Se ha marcado como PROCEDENTE la incidencia", "incidencia-procedente.ftlh"),
    INCIDENCIA_IMPROCEDENTE("INCIDENCIA_IMPROCEDENTE", "Aviso de notificación - Sistema Integral de Empresas de Seguridad Privada (SIESP)", "incidencia-improcedente.ftlh"),
    ACUSE_RECIBO_INCIDENCIA("ACUSE_RECIBO_INCIDENCIA", "Se ha generado un acuse de recibo de la incidencia", "incidencia-acuse-recibo.ftlh"),
    ACUSE_INFORME_MENSUAL("ACUSE_INFORME_MENSUAL", "Se ha generado el acuse de envio del informe mensual", "acuse-informe-mensual.ftlh"),
    EMPRESA_DATOS_ACTUALIZADOS("EMPRESA_DATOS_ACTUALIZADOS", "Se han actualizado los datos de la empresa", "empresa-datos-actualizados.ftlh");

    private String codigo;
    private String motivo;
    private String plantilla;

    NotificacionEmailEnum(String codigo, String motivo, String plantilla) {
        this.codigo = codigo;
        this.motivo = motivo;
        this.plantilla = plantilla;
    }
}
