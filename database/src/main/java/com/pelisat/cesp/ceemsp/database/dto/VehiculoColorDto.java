package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.VehiculoColorEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculoColorDto {
    private VehiculoColorEnum color;
    private String descripcion;
}
