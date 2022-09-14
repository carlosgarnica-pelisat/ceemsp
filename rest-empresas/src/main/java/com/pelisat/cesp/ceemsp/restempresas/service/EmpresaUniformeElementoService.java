package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;

import java.util.List;

public interface EmpresaUniformeElementoService {
    List<EmpresaUniformeElementoDto> obtenerElementosUniformePorEmpresaUuid(String uniformeUuid);
    EmpresaUniformeElementoDto guardarUniformeElemento(String uniformeUuid, String usuario, EmpresaUniformeElementoDto empresaUniformeDto);
    EmpresaUniformeElementoDto modificarUniformeElemento(String uniformeUuid, String elementoUuid, String usuario, EmpresaUniformeElementoDto empresaUniformeElementoDto);
    EmpresaUniformeElementoDto eliminarUniformeElemento(String uniformeUuid, String elementoUuid, String usuario);
}
