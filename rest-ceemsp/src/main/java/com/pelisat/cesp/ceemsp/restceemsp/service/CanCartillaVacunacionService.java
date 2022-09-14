package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface CanCartillaVacunacionService {
    List<CanCartillaVacunacionDto> obtenerCartillasVacunacionPorCanUuid(String empresaUuid, String canUuid);
    List<CanCartillaVacunacionDto> obtenerTodasCartillasVacunacionPorCanUuid(String empresaUuid, String canUuid);
    CanCartillaVacunacionDto guardarCartillaVacunacion(String empresaUuid, String canUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo);
    File obtenerPdfCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid);
    CanCartillaVacunacionDto modificarCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo);
    CanCartillaVacunacionDto borrarCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid, String username);
}
