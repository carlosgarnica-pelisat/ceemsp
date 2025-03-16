package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;

public interface PublicService {
    ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto);
    String buscarProximoNumeroAcuse();
    String buscarProximoNumeroReporte();
    String buscarProximoNumeroNotificacion();
}
