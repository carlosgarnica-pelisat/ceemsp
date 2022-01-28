package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;

import java.util.List;

public interface EmpresaUniformeElementoService {
    List<EmpresaUniformeElementoDto> obtenerElementosUniformePorEmpresaUuid(String empresaUuid, String uniformeUuid);
    EmpresaUniformeElementoDto guardarUniformeElemento(String empresaUuid, String uniformeUuid, String usuario, EmpresaUniformeElementoDto empresaUniformeDto);
}
