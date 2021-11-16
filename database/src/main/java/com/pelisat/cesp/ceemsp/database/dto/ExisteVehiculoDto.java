package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExisteVehiculoDto {
    private Boolean existe;
    private String placas;
    private String numeroSerie;
    private VehiculoDto vehiculo;
}
