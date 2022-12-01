package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaEscrituraSocioService {
    List<EmpresaEscrituraSocioDto> obtenerSociosPorEscritura(String empresaUuid, String escrituraUuid);
    List<EmpresaEscrituraSocioDto> obtenerTodosSociosPorEscritura(String empresaUuid, String escrituraUuid);
    EmpresaEscrituraSocioDto obtenerSocioPorUuid(String empresaUuid, String escrituraUuid, String socioUuid, boolean soloEntidad);
    EmpresaEscrituraSocioDto crearSocio(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto);
    EmpresaEscrituraSocioDto modificarSocio(String empresaUuid, String escrituraUuid, String socioUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto);
    EmpresaEscrituraSocioDto eliminarSocio(String empresaUuid, String escrituraUuid, String socioUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto, MultipartFile multipartFile);
    File obtenerDocumentoFundatorioBajaSocio(String empresaUuid, String escrituraUuid, String socioUuid);
}
