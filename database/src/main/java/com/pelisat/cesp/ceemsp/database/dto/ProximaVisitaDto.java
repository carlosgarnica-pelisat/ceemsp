package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProximaVisitaDto {
    private TipoVisitaEnum tipoVisita;
    private String numeroSiguiente;
}
