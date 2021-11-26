package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonalPuestoDeTrabajoDto {
    private Integer id;
    private String uuid;
    private String nombre;
    private String descripcion;
    private List<PersonalSubpuestoDeTrabajoDto> subpuestos;
}
