package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CanCartillaVacunacionService {
    List<CanCartillaVacunacionDto> obtenerCartillasVacunacionPorCanUuid(String empresaUuid, String canUuid);
    CanCartillaVacunacionDto guardarCartillaVacunacion(String empresaUuid, String canUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo);
    CanCartillaVacunacionDto modificarCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto);
    CanCartillaVacunacionDto borrarCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid, String username);
}
