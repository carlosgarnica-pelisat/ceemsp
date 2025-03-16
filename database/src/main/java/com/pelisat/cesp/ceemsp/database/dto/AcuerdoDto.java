package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.AcuerdoTipoEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AcuerdoDto {
    private Integer id;
    private String uuid;
    private String rutaArchivo;
    private String fecha;
    private String observaciones;
    private AcuerdoTipoEnum tipo;
    private String fechaInicio;
    private String fechaFin;
    private BigDecimal multaUmas;
    private BigDecimal multaPesos;
    private boolean eliminado;
    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;
    private EmpresaDto empresa;
    private String fechaCreacion;
    private String fechaActualizacion;
}
