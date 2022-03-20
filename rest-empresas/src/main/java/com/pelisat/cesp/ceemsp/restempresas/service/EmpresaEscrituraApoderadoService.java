package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;

import java.util.List;

public interface EmpresaEscrituraApoderadoService {
    List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(String escrituraUuid);
    EmpresaEscrituraApoderadoDto crearApoderado(String escrituraUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto);
    EmpresaEscrituraApoderadoDto modificarApoderado(String escrituraUuid, String apoderadoUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto);
    EmpresaEscrituraApoderadoDto eliminarApoderado(String escrituraUuid, String apoderadoUuid, String username);
}
