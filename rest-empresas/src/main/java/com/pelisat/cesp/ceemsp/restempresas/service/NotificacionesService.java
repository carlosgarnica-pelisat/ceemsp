package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDto;

import java.util.List;

public interface NotificacionesService {
    List<BuzonInternoDto> obtenerNotificacionesPorEmpresa(String username);
    BuzonInternoDto leerNotificacion(String username, String uuid);
}
