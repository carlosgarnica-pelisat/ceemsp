package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaDomicilioDto {
    private int id;
    private String uuid;
    private String nombre;
    private String domicilio1;
    private String numeroExterior;
    private String numeroInterior;
    private String domicilio2;
    private String domicilio3;
    private String domicilio4;
    private String estado;
    private String pais;
    private String codigoPostal;
    private boolean matriz;
    private String telefonoFijo;
    private String telefonoMovil;
    private String latitud;
    private String longitud;
}
