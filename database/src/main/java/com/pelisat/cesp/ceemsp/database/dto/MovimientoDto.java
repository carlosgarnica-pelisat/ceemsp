package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoMovimientoUsuarioEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoOperacionUsuarioEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoDto {
    private int id;
    private String uuid;
    private TipoOperacionUsuarioEnum tipoOperacion;
    private String nombreEnte;
    private String perteneceA;
    private String fechaMovimiento;
    private String metadatos;
}
