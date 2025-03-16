package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CanAdiestramientoDto {
    private int id;
    private String uuid;
    private String nombreInstructor;
    private String fechaConstancia;
    private CanTipoAdiestramientoDto canTipoAdiestramiento;
    private boolean eliminado;
}
