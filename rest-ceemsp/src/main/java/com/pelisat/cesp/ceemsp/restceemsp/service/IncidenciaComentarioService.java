package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaComentarioDto;

import java.util.List;

public interface IncidenciaComentarioService {
    List<IncidenciaComentarioDto> obtenerComentariosPorIncidenciaUuid(String uuid, String incidenciaUuid);
    IncidenciaComentarioDto modificarComentario(String uuid, String incidenciaUuid, String comentarioUuid, String username, IncidenciaComentarioDto comentarioDto);
    IncidenciaComentarioDto eliminarComentario(String uuid, String incidenciaUuid, String comentarioUuid, String username);
}
