package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmpresaUniformeDto {
    private int id;
    private String uuid;
    private String nombre;
    private String descripcion;
    private String ubicacionArchivo;
    private List<EmpresaUniformeElementoDto> elementos;
}
