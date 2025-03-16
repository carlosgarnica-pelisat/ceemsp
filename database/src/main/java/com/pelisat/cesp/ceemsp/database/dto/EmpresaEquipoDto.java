package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class EmpresaEquipoDto {
    private int id;
    private String uuid;
    private EquipoDto equipo;
    private BigDecimal cantidad;
    List<EmpresaEquipoMovimientoDto> movimientos;
}
