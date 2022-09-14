package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CanConstanciaSaludDto {
    private int id;
    private String uuid;
    private String expedidoPor;
    private String cedula;
    private String fechaExpedicion;
    private boolean eliminado;
}
