package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;

import java.util.List;

public interface IncidenciaService {
    List<IncidenciaDto> obtenerIncidenciasPorEmpresa(String uuidEmpresa);
    IncidenciaDto obtenerIncidenciaPorUuid(String empresaUuid, String incidenciaUuid);
    IncidenciaDto guardarIncidencia(String empresaUuid, String username, IncidenciaDto incidenciaDto);
    IncidenciaDto autoasignarIncidencia(String empresaUuid, String incidenciaUuid, String username);
    IncidenciaDto asignarIncidencia(String empresaUuid, String incidenciaUuid, UsuarioDto usuarioDto, String username);
    IncidenciaDto agregarComentario(String empresaUuid, String incidenciaUuid, String username, IncidenciaDto incidenciaDto);
}
