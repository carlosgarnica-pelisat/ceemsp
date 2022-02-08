package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitaDto {
    private int id;
    private String uuid;
    private EmpresaDto empresa;
    private TipoVisitaEnum tipo;
    private String numeroRegistro;
    private String numeroOrden;
    private String fechaVisita;
    private boolean requerimiento;
    private String observaciones;
    private String fechaTermino;
    private UsuarioDto responsable;
    private String domicilio1;
    private String numeroExterior;
    private String numeroInterior;
    private String domicilio2;
    private String domicilio3;
    private String domicilio4;
    private String estado;
    private String pais;
    private String codigoPostal;
}
