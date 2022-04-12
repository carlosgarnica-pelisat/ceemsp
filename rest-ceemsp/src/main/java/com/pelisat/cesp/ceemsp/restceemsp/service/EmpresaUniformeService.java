package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;

import java.util.List;
public interface EmpresaUniformeService {
    List<EmpresaUniformeDto> obtenerUniformesPorEmpresaUuid(String empresaUuid);
    EmpresaUniformeDto obtenerUniformePorUuid(String empresaUuid, String uniformeUuid);
    EmpresaUniformeDto guardarUniforme(String empresaUuid, String usuario, EmpresaUniformeDto empresaUniformeDto);
    EmpresaUniformeDto modificarUniforme(String empresaUuid, String uniformeUuid, String usuario, EmpresaUniformeDto empresaUniformeDto);
    EmpresaUniformeDto eliminarUniforme(String empresaUuid, String uniformeUuid, String usuario);
}
