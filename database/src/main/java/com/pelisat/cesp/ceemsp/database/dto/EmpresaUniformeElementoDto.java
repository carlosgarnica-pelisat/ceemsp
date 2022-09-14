package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class EmpresaUniformeElementoDto {
    private int id;
    private String uuid;
    private UniformeDto elemento;
    private BigDecimal cantidad;
    private String ubicacionArchivo;
    List<EmpresaUniformeElementoMovimientoDto> movimientos;
}
