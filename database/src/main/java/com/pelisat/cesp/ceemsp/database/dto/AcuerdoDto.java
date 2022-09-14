package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcuerdoDto {
    private Integer id;
    private String uuid;
    private String rutaArchivo;
    private String fecha;
    private String observaciones;
    private boolean eliminado;
    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;
}
