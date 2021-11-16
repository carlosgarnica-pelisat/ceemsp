package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;

import java.util.List;

public interface EmpresaEscrituraRepresentanteService {
    List<EmpresaEscrituraRepresentanteDto> obtenerRepresentantesPorEscritura(String empresaUuid, String escrituraUuid);
    EmpresaEscrituraRepresentanteDto obtenerRepresentantePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    EmpresaEscrituraRepresentanteDto crearRepresentante(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto);
}
