package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaFormaEjecucionDto {
    private int id;
    private String uuid;
    private FormaEjecucionEnum formaEjecucion;
}
