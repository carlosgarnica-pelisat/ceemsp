package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;

import java.util.List;

public interface EmpresaModalidadService {
    List<EmpresaModalidadDto> obtenerModalidadesEmpresa(String empresaUuid);

    EmpresaModalidadDto guardarModalidad(String empresaUuid, String username, EmpresaModalidadDto empresaModalidadDto);

    EmpresaModalidadDto obtenerModalidadPorUuid(String empresaUuid, String modalidadUuid, boolean soloEntidad);
}
