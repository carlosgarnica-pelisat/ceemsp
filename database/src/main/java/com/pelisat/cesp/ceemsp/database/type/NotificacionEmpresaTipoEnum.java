package com.pelisat.cesp.ceemsp.database.type;

public enum NotificacionEmpresaTipoEnum {
    GENERAL("GENERAL", "Notificacion para todos los usuarios internos"),
    INDIVIDUAL("ACUERDOS_VENCIMIENTO_15_DIAS", "Acuerdos a vencer en los proximos 15 dias");

    private String codigo;
    private String descripcion;

    NotificacionEmpresaTipoEnum(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }
}
