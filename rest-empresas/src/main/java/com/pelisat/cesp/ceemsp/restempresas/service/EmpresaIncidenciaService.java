package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.IncidenciaDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaIncidenciaService {
    List<IncidenciaDto> obtenerIncidenciasPorEmpresa(String username);
    IncidenciaDto obtenerIncidenciaPorUuid(String username, String uuid);
    IncidenciaDto guardarIncidencia(String username, IncidenciaDto incidenciaDto, MultipartFile archivo);
    IncidenciaDto agregarComentario(String incidenciaUuid, String username, IncidenciaDto incidenciaDto, MultipartFile file);
}
