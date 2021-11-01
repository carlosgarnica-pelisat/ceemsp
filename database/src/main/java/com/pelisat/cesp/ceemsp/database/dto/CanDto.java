package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.CanGeneroEnum;
import com.pelisat.cesp.ceemsp.database.type.CanOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import java.math.BigDecimal;

public class CanDto {
    private CanGeneroEnum genero;
    private CanRazaDto raza;
    private EmpresaDomicilioDto domicilioAsignado;
    private String fechaIngreso;
    private int edad;
    private BigDecimal peso;
    private String descripcion;
    private CanOrigenEnum origen;
    private CanStatusEnum status;
    private boolean chip;
    private boolean tatuaje;

    // TODO: Add pictures and training
}
