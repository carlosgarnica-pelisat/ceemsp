package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalArmaDto {
    private ArmaDto arma;
    private PersonaDto persona;
    private String observaciones;
    private String motivoBajaAsignacion;
    private String fechaCreacion;
    private String fechaActualizacion;
    private boolean eliminado;
}
