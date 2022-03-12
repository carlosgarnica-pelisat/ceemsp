package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExisteEscrituraDto {
    private Boolean existe;
    private String numero;
    private EmpresaEscrituraDto escritura;
}
