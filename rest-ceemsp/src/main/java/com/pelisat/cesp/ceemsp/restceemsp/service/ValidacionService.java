package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ExisteEmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.ExistePersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;

public interface ValidacionService {
    ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto);
    ExistePersonaDto buscarExistenciaPersona(ExistePersonaDto existePersonaDto);
    ExisteEmpresaDto buscarExistenciaEmpresa(ExisteEmpresaDto existeEmpresaDto);
    ExisteEscrituraDto buscarEscrituraDto(ExisteEscrituraDto existeEscrituraDto);
}
