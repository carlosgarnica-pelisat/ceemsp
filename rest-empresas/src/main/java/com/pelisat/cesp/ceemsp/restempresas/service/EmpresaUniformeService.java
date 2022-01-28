package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;

import java.util.List;

public interface EmpresaUniformeService {
    List<EmpresaUniformeDto> obtenerUniformesPorEmpresaUuid(String empresaUuid);
    EmpresaUniformeDto obtenerUniformePorUuid(String empresaUuid, String uniformeUuid);
    EmpresaUniformeDto guardarUniforme(String empresaUsername, EmpresaUniformeDto empresaUniformeDto);
}
