package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ExisteUsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;
import com.pelisat.cesp.ceemsp.database.dto.NextRegisterDto;
import com.pelisat.cesp.ceemsp.database.dto.ProximaVisitaDto;

public interface PublicService {
    NextRegisterDto findNextRegister(NextRegisterDto nextRegisterDto);
    ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto);
    ProximaVisitaDto buscarProximaVisita();
}
