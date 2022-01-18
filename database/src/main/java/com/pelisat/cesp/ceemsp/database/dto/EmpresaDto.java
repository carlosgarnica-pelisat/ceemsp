package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoPersonaEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmpresaDto {
    private Integer id;
    private String uuid;
    private String razonSocial;
    private String nombreComercial;
    private TipoTramiteEnum tipoTramite;
    private String registro;
    private TipoPersonaEnum tipoPersona;
    private String rfc;
    private String curp;
    private SexoEnum sexo;
    private String correoElectronico;
    private String telefono;
    private EmpresaStatusEnum status;
    private String observaciones;
    private List<EmpresaModalidadDto> modalidades;
    private List<EmpresaFormaEjecucionDto> formasEjecucion;
}
