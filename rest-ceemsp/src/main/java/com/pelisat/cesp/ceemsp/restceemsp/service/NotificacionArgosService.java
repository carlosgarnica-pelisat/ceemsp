package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionArgosDto;

import java.util.List;

public interface NotificacionArgosService {
    List<NotificacionArgosDto> obtenerNotificacionesPorUsuario(String username);
    NotificacionArgosDto leerNotificacion(String uuid, String username);
}
