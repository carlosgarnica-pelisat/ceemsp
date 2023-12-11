package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.BusquedaFiltroEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealizarBusquedaDto {
    private String palabraABuscar;
    private BusquedaFiltroEnum filtro;
}
