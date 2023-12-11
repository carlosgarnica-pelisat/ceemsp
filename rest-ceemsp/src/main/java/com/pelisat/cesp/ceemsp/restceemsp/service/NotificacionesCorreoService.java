package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.NotificacionCorreoDto;

public interface NotificacionesCorreoService {
    void generarCorreoElectronico(String username, NotificacionCorreoDto correoDto);
}
