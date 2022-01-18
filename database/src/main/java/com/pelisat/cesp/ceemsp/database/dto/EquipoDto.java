package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipoDto {
    private String uuid;
    private String nombre;
    private String descripcion;
    private FormaEjecucionEnum formaEjecucion;
}
