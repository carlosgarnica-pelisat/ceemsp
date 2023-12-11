package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaEscrituraSocioService {
    List<EmpresaEscrituraSocioDto> obtenerSociosPorEscritura(String escrituraUuid);
    EmpresaEscrituraSocioDto crearSocio(String escrituraUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto);
    EmpresaEscrituraSocioDto modificarSocio(String escrituraUuid, String socioUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto);
    EmpresaEscrituraSocioDto eliminarSocio(String escrituraUuid, String socioUuid, String username, EmpresaEscrituraSocioDto empresaEscrituraSocioDto, MultipartFile multipartFile);
}
