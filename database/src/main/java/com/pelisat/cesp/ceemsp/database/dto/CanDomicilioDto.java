package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CanDomicilioDto {
    private int id;
    private String uuid;
    private boolean eliminado;
    private EmpresaDomicilioDto domicilioAnterior;
    private EmpresaDomicilioDto domicilioActual;
    private String fechaCreacion;
    private String fechaActualizacion;
}
