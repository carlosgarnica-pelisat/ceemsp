package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalVehiculoDto {
    private VehiculoDto vehiculo;
    private PersonaDto persona;
    private String observaciones;
    private String motivoBajaAsignacion;
    private String fechaCreacion;
    private String fechaActualizacion;
    private boolean eliminado;
}
