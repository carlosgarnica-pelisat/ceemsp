package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;

import java.util.List;

public interface EmpresaEquipoService {
    List<EmpresaEquipoDto> obtenerEquiposPorEmpresaUuid(String empresaUuid);
    EmpresaEquipoDto obtenerEquipoPorUuid(String empresaUuid, String equipoUuid);
    EmpresaEquipoDto guardarEquipo(String empresaUuid, String usuario, EmpresaEquipoDto empresaEquipoDto);
}
