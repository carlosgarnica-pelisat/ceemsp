package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum NotificacionEmailEnum {
    EMPRESA_REGISTRADA("EMPRESA_REGISTRADA", "Su empresa se ha registrado con exito", "empresa-nueva.ftlh"),
    NOTIFICACION("NOTIFICACION", "Has recibido una nueva notificacion en SIESP", "notificacion.ftlh"),
    NUEVA_INCIDENCIA("NUEVA_INCIDENCIA", "Se ha registrado una nueva incidencia", "incidencia-nueva.ftlh"),
    ACUSE_RECIBO_INCIDENCIA("ACUSE_RECIBO_INCIDENCIA", "Se ha generado un acuse de recibo de la incidencia", "incidencia-acuse-recibo.ftlh"),
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
