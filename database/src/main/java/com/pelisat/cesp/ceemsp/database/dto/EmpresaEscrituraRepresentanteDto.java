package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaEscrituraRepresentanteDto {
    private int id;
    private String uuid;
    private String nombres;
    private String apellidos;
    private String apellidoMaterno;
    private SexoEnum sexo;
    private String curp;
}
