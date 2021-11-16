package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoPersonaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDto {
    private int id;
    private String uuid;
    private TipoPersonaEnum tipoPersona;
    private String rfc;
    private String nombreComercial;
    private String razonSocial;
    private boolean canes;
    private boolean armas;
    private String fechaInicio;
    private String fechaFin;
    private String rutaArchivoContrato;
}
