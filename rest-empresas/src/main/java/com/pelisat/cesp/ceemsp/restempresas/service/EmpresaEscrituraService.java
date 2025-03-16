package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaEscrituraService {
    List<EmpresaEscrituraDto> obtenerEscrituras(String username);
    EmpresaEscrituraDto obtenerEscrituraPorUuid(String escrituraUuid, boolean soloEntidad);
    File obtenerEscrituraPdf(String escrituraUuid);
}
