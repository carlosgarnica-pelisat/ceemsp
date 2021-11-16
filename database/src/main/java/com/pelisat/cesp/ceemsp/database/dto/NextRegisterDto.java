package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NextRegisterDto {
    private String numeroSiguiente;
    private TipoTramiteEnum tipo;
}
