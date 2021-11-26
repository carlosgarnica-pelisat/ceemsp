package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalSubpuestoDeTrabajoDto {
    private Integer id;
    private String uuid;
    private String nombre;
    private String descripcion;
    private boolean portacion;
    private boolean cuip;
}
