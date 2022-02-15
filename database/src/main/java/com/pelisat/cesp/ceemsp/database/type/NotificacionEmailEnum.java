package com.pelisat.cesp.ceemsp.database.type;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum NotificacionEmailEnum {
    EMPRESA_REGISTRADA("EMPRESA_REGISTRADA", "Su empresa se ha registrado con exito", "Bienvenido, su empresa se ha registrado con exito"),
    /*REQUERIMIENTO(),
    SANCION()*/;

    private String codigo;
    private String motivo;
    private String cuerpo;

    NotificacionEmailEnum(String codigo, String motivo, String cuerpo) {
        this.codigo = codigo;
        this.motivo = motivo;
        this.cuerpo = cuerpo;
    }
}
