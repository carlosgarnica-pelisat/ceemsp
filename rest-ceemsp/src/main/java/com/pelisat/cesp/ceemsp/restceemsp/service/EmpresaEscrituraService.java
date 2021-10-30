package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;

import java.util.List;

public interface EmpresaEscrituraService {
    List<EmpresaEscrituraDto> obtenerEscriturasEmpresaPorUuid(String empresaUuid);

    EmpresaEscrituraDto obtenerEscrituraPorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);

    EmpresaEscrituraDto guardarEscritura(String empresaUuid, EmpresaEscrituraDto empresaEscrituraDto, String username);
}