package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaEscrituraConsejoDto {
    private String nombres;
    private String apellidos;
    private SexoEnum sexo;
    private String puesto;
}
