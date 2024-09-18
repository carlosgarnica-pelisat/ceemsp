package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;

@Getter
public enum PersonalRazonBajaEnum {
    RENUNCIA_VOLUNTARIA("RENUNCIA_VOLUNTARIA", "Renuncia voluntaria"),
    ABANDONO_EMPLEO("ABANDONO_EMPLEO", "Abandono de empleo"),
    ABANDONO_SERVICIO("ABANDONO_SERVICIO", "Abandono del servicio"),
    FALTAS_INJUSTIFICADAS("FALTAS_INJUSTIFICADAS", "Faltas injustificadas"),
    PERDIDA_CONFIANZA("PERDIDA_CONFIANZA", "Hechos que generan la perdida de la confianza"),
    RESCISION_CONTRATO("RESCISION_CONTRATO", "Rescision del contrato"),
    RECORTE_PERSONAL("RECORTE_PERSONAL", "Recorte de personal"),
    TERMINO_CONTRATO("TERMINO_CONTRATO", "Termino de contrato"),
    EN_PROCESO_JUDICIAL("EN_PROCESO_JUDICIAL", "Por estar en proceso judicial"),
    POSIBLE_COMISION_DELITO("POSIBLE_COMISION_DELITO", "Posible comision de delito"),
    INCAPACIDAD_FISICA("INCAPACIDAD_FISICA", "Incapacidad fisica permanente"),
    CESANTIA_EDAD_AVANZADA("CESANTIA_EDAD_AVANZADA", "Cesantia en edad avanzada"),
    JUBILACION("JUBILACION", "Jubilacion"),
    MUERTE_NATURAL("MUERTE_NATURAL", "Muerte natural"),
    ACCIDENTE("ACCIDENTE", "Accidente"),
    LIQUIDACION("LIQUIDACION", "Liquidacion"),
    CIERRE_EMPRESA("CIERRE_EMPRESA", "Cierre de la empresa"),
    DEFUNCION("DEFUNCION", "Defuncion"),
    PENSION("PENSION", "Pension"),
    OTRO("OTRO", "Otro (favor de especificar lo mejor posible)"),
    CAMBIO_STATUS("CAMBIO_STATUS", "Cambio de status"),
    DOCUMENTACION_APOCRIFA("DOCUMENTACION_APOCRIFA", "Presentar documentacion apocrifa");

    private String codigo;
    private String nombre;

    PersonalRazonBajaEnum(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
