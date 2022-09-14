package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VehiculoMarcaDto {
    private Integer id;
    private String uuid;
    private String nombre;
    private String descripcion;
    private VehiculoTipoEnum tipo;
    private List<VehiculoSubmarcaDto> submarcas;
}
