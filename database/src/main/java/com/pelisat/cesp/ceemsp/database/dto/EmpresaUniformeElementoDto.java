package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmpresaUniformeElementoDto {
    private int id;
    private String uuid;
    private UniformeDto elemento;
    private BigDecimal cantidad;
}
