package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaEscrituraSocioDto {
    private String nombres;
    private String apellidos;
    private SexoEnum sexo;
}
