package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteModalidadDto {
    private int id;
    private String uuid;
    private EmpresaModalidadDto modalidad;
}
