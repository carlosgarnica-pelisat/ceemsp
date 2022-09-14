package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmodalidadDto {
    private int id;
    private String uuid;
    private String nombre;
    private String descripcion;
    private Boolean canes;
    private Boolean armas;
}
