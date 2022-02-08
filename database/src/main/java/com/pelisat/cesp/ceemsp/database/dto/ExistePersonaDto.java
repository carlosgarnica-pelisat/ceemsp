package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExistePersonaDto {
    private Boolean existe;
    private String curp;
    private String rfc;
    private PersonaDto persona;
    private EmpresaDto empresa;
}
