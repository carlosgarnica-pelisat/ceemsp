package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;

public interface PublicService {
    NextRegisterDto findNextRegister(NextRegisterDto nextRegisterDto);
    ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto);
    ProximaVisitaDto buscarProximaVisita(ProximaVisitaDto proximaVisitaDto);
    String buscarProximoNumeroAcuse();
    String buscarProximoNumeroNotificacion();
    String buscarProximoNumeroReporte();
    ValidarAcuseDto validarAcusePorSello(String sello);
    ValidarInformeDto validarInformePorSello(String sello);
}
