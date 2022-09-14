package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaEscrituraApoderadoService {
    List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(String empresaUuid, String escrituraUuid);
    List<EmpresaEscrituraApoderadoDto> obtenerTodosApoderadosPorEscritura(String empresaUuid, String escrituraUuid);
    EmpresaEscrituraApoderadoDto obtenerRepresentantePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    EmpresaEscrituraApoderadoDto crearApoderado(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto);
    EmpresaEscrituraApoderadoDto modificarApoderado(String empresaUuid, String escrituraUuid, String apoderadoUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto);
    EmpresaEscrituraApoderadoDto eliminarApoderado(String empresaUuid, String escrituraUuid, String apoderadoUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto, MultipartFile multipartFile);
}
