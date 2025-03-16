package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaEscrituraRepresentanteService {
    List<EmpresaEscrituraRepresentanteDto> obtenerRepresentantesPorEscritura(String empresaUuid, String escrituraUuid);
    List<EmpresaEscrituraRepresentanteDto> obtenerTodosRepresentantesPorEscritura(String empresaUuid, String escrituraUuid);
    EmpresaEscrituraRepresentanteDto obtenerRepresentantePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    EmpresaEscrituraRepresentanteDto crearRepresentante(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto);
    EmpresaEscrituraRepresentanteDto modificarRepresentante(String empresaUuid, String escrituraUuid, String representanteUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto);
    EmpresaEscrituraRepresentanteDto eliminarRepresentante(String empresaUuid, String escrituraUuid, String representanteUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto, MultipartFile multipartFile);
    File obtenerDocumentoFundatorioBajaRepresentante(String empresaUuid, String escrituraUuid, String representanteUuid);
}
