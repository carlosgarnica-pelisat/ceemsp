package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionEmpresaDto;

import java.util.List;

public interface NotificacionEmpresaService {
    List<NotificacionEmpresaDto> obtenerNotificacionesPorUsuario(String username);
    NotificacionEmpresaDto leerNotificacion(String uuid, String username);
}
