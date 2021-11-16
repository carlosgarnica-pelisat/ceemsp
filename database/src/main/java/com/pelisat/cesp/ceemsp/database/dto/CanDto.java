package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.CanGeneroEnum;
import com.pelisat.cesp.ceemsp.database.type.CanOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;

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
    private String razonSocial;
    private String fechaInicio;
    private String fechaFin;
    private PersonaDto elementoAsignado;
    private ClienteDto clienteAsignado;
    private ClienteDomicilioDto clienteDomicilioDto;
    private String motivos;

    // TODO: Add pictures and training
}
