package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaEscrituraRepresentanteService {
    List<EmpresaEscrituraRepresentanteDto> obtenerRepresentantesPorEscritura(String escrituraUuid);
    EmpresaEscrituraRepresentanteDto crearRepresentante(String escrituraUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto);
    EmpresaEscrituraRepresentanteDto modificarRepresentante(String escrituraUuid, String representanteUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto);
    EmpresaEscrituraRepresentanteDto eliminarRepresentante(String escrituraUuid, String representanteUuid, String username, EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto, MultipartFile multipartFile);
}
