package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IncidenciaService {
    List<IncidenciaDto> obtenerIncidenciasPorEmpresa(String uuidEmpresa);
    IncidenciaDto obtenerIncidenciaPorUuid(String empresaUuid, String incidenciaUuid);
    IncidenciaDto guardarIncidencia(String empresaUuid, String username, IncidenciaDto incidenciaDto, MultipartFile multipartFile);
    IncidenciaDto autoasignarIncidencia(String empresaUuid, String incidenciaUuid, String username);
    IncidenciaDto asignarIncidencia(String empresaUuid, String incidenciaUuid, UsuarioDto usuarioDto, String username);
    IncidenciaDto agregarComentario(String empresaUuid, String incidenciaUuid, String username, IncidenciaDto incidenciaDto);
    IncidenciaDto eliminarIncidencia(String empresaUuid, String incidenciaUuid, String username);
}
