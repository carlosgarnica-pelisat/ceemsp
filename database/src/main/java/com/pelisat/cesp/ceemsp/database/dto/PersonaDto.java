package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.EstadoCivilEnum;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoSangreEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaDto {
    private PersonalNacionalidadDto nacionalidad;
    private String curp;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private SexoEnum sexo;
    private String fechaDeNacimiento;
    private TipoSangreEnum tipoSangre;
    private EstadoCivilEnum estadoCivil;
    private String domicilio1;
    private String domicilio2;
    private String domicilio3;
    private String domicilio4;
    private String estado;
    private String pais;
    private String codigoPostal;
    private String telefono;
    private String correoElectronico;
}
