package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.model.ClienteModalidad;
import com.pelisat.cesp.ceemsp.database.type.TipoPersonaEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private String fechaCreacion;
    private String fechaActualizacion;
    private List<ClienteDomicilioDto> domicilios;
    private List<ClienteAsignacionPersonalDto> asignaciones;
    private List<ClienteModalidadDto> modalidades;
    private List<ClienteFormaEjecucionDto> formasEjecucion;
    private boolean eliminado;
    private int numeroSucursales;
    private int numeroElementosAsignados;

    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;
    private EmpresaDto empresa;

    private boolean domicilioCapturado;
    private boolean asignacionCapturada;
    private boolean modalidadCapturada;
    private boolean formaEjecucionCapturada;
}
