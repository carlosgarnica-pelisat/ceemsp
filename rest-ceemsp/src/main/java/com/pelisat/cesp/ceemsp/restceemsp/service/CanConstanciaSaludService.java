package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import com.pelisat.cesp.ceemsp.database.dto.CanConstanciaSaludDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CanConstanciaSaludService {
    List<CanConstanciaSaludDto> obtenerConstanciasSaludPorCanUuid(String empresaUuid, String canUuid);
    CanConstanciaSaludDto guardarConstanciaSalud(String empresaUuid, String canUuid, String username, CanConstanciaSaludDto canConstanciaSaludDto, MultipartFile archivo);
    CanConstanciaSaludDto modificarConstanciaSalud(String empresaUuid, String canUuid, String constanciaUuid, String username, CanConstanciaSaludDto canConstanciaSaludDto, MultipartFile archivo);
    CanConstanciaSaludDto eliminarConstanciaSalud(String empresaUuid, String canUuid, String constanciaUuid, String username);
}
