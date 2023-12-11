package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.ReporteArgosStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoReporteArgosEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteArgosDto {
    private int id;
    private String uuid;
    private TipoReporteArgosEnum tipo;
    private ReporteArgosStatusEnum status;
    private String razon;
    private String fechaCreacion;
    private String fechaActualizacion;
    private String fechaInicio;
    private String fechaFin;
}
