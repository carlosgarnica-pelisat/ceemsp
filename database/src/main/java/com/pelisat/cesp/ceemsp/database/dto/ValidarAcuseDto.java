package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidarAcuseDto {
    private EmpresaDto empresa;
    private String numeroAcuse;
    private IncidenciaDto incidencia;
}
