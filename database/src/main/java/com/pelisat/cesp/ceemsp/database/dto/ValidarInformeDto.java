package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidarInformeDto {
    private EmpresaDto empresa;
    private String mesano;
    private String fechaCreacion;
    private String numero;
}
