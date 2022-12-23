package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalVehiculoDto {
    private VehiculoDto vehiculo;
    private String observaciones;
}
