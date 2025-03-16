package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaEscrituraService {
    List<EmpresaEscrituraDto> obtenerEscriturasEmpresaPorUuid(String empresaUuid);
    EmpresaEscrituraDto obtenerEscrituraPorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    EmpresaEscrituraDto guardarEscritura(String empresaUuid, EmpresaEscrituraDto empresaEscrituraDto, String username, MultipartFile multipartFile);
    EmpresaEscrituraDto modificarEscritura(String empresaUuid, String escrituraUuid, EmpresaEscrituraDto empresaEscrituraDto, MultipartFile multipartFile, String username);
    EmpresaEscrituraDto eliminarEscritura(String empresaUuid, String escrituraUuid, String username);
    File obtenerEscrituraPdf(String empresaUuid, String escrituraUuid);
}
