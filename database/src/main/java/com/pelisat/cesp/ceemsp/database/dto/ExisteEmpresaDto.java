package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExisteEmpresaDto {
    private Boolean existe;
    private String rfc;
    private String curp;
    private EmpresaDto empresa;
}
