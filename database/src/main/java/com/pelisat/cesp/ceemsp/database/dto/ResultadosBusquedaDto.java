package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.BusquedaFiltroEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ResultadosBusquedaDto {
    private String palabraABuscar;
    private BusquedaFiltroEnum filtro;
    private List<EmpresaDto> resultadosBusquedaEmpresas;
    private List<ClienteDto> resultadosBusquedaClientes;
    private List<PersonaDto> resultadosBusquedaPersona;
    private List<CanDto> resultadosBusquedaCan;
    private List<VehiculoDto> resultadosBusquedaVehiculo;
    private List<ArmaDto> resultadosBusquedaArma;
    private List<EmpresaEscrituraDto> resultadosBusquedaEscrituras;
    private List<IncidenciaDto> resultadosBusquedaIncidencias;
}
