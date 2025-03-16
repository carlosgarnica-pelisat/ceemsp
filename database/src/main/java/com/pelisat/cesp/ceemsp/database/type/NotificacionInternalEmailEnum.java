package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum NotificacionInternalEmailEnum {

    ACUERDOS_VENCIMIENTO_15_DIAS("ACUERDOS_VENCIMIENTO_15_DIAS", "Acuerdos a vencer en los proximos 15 dias", "notificaciones-internas/acuerdos-vencimiento-15-dias.ftlh"),
    APODERADOS_VENCIMIENTO_15_DIAS("APODERADOS_VENCIMIENTO_15_DIAS", "Apoderados a vencer en los proximos 15 dias", "notificaciones-internas/apoderados-vencimiento-15-dias.ftlh"),
    EMPRESAS_LICENCIA_COLECTIVA_VENCIMIENTO_15_DIAS("EMPRESAS_LICENCIA_COLECTIVA_VENCIMIENTO_15_DIAS", "Licencias colectivas a vencer en los proximos 15 dias", "notificaciones-internas/licencias-colectivas-vencimiento-15-dias.ftlh"),
    EMPRESAS_LICENCIA_FEDERAL_VENCIMIENTO_15_DIAS("EMPRESAS_LICENCIA_FEDERAL_VENCIMIENTO_15_DIAS", "Licencias federales a vencer en los proximos 15 dias", "notificaciones-internas/licencias-federales-vencimiento-15-dias.ftlh"),
    REQUERIMIENTOS_VENCIMIENTO_15_DIAS("REQUERIMIENTOS_VENCIMIENTO_15_DIAS", "Requerimientos a vencer en los proximos 15 dias", "notificaciones-internas/requerimientos-vencimiento-15-dias.ftlh");

    private String codigo;
    private String motivo;
    private String plantilla;

    NotificacionInternalEmailEnum(String codigo, String motivo, String plantilla) {
        this.codigo = codigo;
        this.motivo = motivo;
        this.plantilla = plantilla;
    }
}
