package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ExisteArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.ExistePersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;

public interface ValidacionService {
    ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto);
    ExistePersonaDto buscarExistenciaPersona(ExistePersonaDto existePersonaDto);
    ExisteEscrituraDto buscarEscrituraDto(ExisteEscrituraDto existeEscrituraDto);
    ExisteArmaDto buscarArma(ExisteArmaDto existeArmaDto);
}
