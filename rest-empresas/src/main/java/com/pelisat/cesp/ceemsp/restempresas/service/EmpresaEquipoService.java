package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEquipoDto;

import java.util.List;

public interface EmpresaEquipoService {
    List<EmpresaEquipoDto> obtenerEquiposPorEmpresa(String username);
    EmpresaEquipoDto obtenerEquipoPorUuid(String equipoUuid, String username);
    EmpresaEquipoDto guardarEquipo(EmpresaEquipoDto empresaEquipoDto, String usuario);
    EmpresaEquipoDto modificarEquipo(String equipoUuid, String usuario, EmpresaEquipoDto empresaEquipoDto);
    EmpresaEquipoDto eliminarEquipo(String equipoUuid, String usuario);
}
