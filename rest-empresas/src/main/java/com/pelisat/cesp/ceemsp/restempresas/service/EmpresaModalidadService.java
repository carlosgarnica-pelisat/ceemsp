package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;

import java.util.List;

public interface EmpresaModalidadService {
    List<EmpresaModalidadDto> obtenerModalidadesEmpresa(String username);
    EmpresaModalidadDto obtenerEmpresaModalidadPorId(int id);
    EmpresaModalidadDto guardarModalidad(String username, EmpresaModalidadDto empresaModalidadDto);
    EmpresaModalidadDto obtenerModalidadPorUuid(String modalidadUuid, boolean soloEntidad);
    EmpresaModalidadDto eliminarModalidadPorUuid(String modalidadUuid, String username);
}
