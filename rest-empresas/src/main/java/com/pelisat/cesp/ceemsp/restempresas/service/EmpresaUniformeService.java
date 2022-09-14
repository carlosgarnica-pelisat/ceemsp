package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;

import java.util.List;

public interface EmpresaUniformeService {
    List<EmpresaUniformeDto> obtenerUniformesPorEmpresaUuid(String username);
    EmpresaUniformeDto obtenerUniformePorUuid(String uniformeUuid);
    EmpresaUniformeDto guardarUniforme(String usuario, EmpresaUniformeDto empresaUniformeDto);
    EmpresaUniformeDto modificarUniforme(String uniformeUuid, String usuario, EmpresaUniformeDto empresaUniformeDto);
    EmpresaUniformeDto eliminarUniforme(String uniformeUuid, String usuario);
}
