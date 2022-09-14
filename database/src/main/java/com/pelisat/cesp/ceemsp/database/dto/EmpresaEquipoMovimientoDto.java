package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaEquipoMovimientoDto {
    private int id;
    private String uuid;
    private Integer altas;
    private Integer bajas;
    private Integer cantidadActual;
    private String observaciones;
}
