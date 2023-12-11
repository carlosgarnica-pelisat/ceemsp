package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum NotificacionCorreoEnum {
    ACUERDOS_VENCER_15_DIAS("ACUERDOS_VENCER_15_DIAS", "Acuerdos a vencer en los siguientes 15 dias o vencidos", "Acuerdos a vencer en los siguientes 15 dias o vencidos"),
    REQUERIMIENTOS_VENCER_15_DIAS("REQUERIMIENTOS_VENCER_15_DIAS", "Requerimientos a vencer en los siguientes 15 dias o vencidos", "Requerimientos a vencer en los siguientes 15 dias o vencidos"),
    LICENCIAS_FEDERALES_VENCER_15_DIAS("LICENCIAS_FEDERALES_VENCER_15_DIAS", "Licencias federales a vencer en los siguientes 15 dias o vencidos", "Licencias federales a vencer en los siguientes 15 dias o vencidos."),
    LICENCIAS_PARTICULARES_COLECTIVAS_VENCER_15_DIAS("LICENCIAS_PARTICULARES_COLECTIVAS_VENCER_15_DIAS", "Licencias particulares colectivas a vencer en los siguientes 15 dias o vencidos.", "Licencias particulares colectivas a vencer en los siguientes 15 dias o vencidos."),
    APODERADOS_LEGALES_VENCER_15_DIAS("APODERADOS_LEGALES_VENCER_15_DIAS", "Apoderados legales a vencer en los siguientes 15 dias o vencidos.", "Apoderados legales a vencer en los siguientes 15 dias o vencidos."),
    EMPRESAS_INFORMES_MENSUALES_NO_PRESENTADOS("EMPRESAS_INFORMES_MENSUALES_NO_PRESENTADOS", "Empresas con informes mensuales no presentados en el mes actual.", "Empresas con informes mensuales no presentados en el mes actual.");

    private final String codigo;
    private final String nombre;
    private final String descripcion;

    NotificacionCorreoEnum(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

}
