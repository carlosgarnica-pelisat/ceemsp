package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaCanCartillaVacunacionService {
    List<CanCartillaVacunacionDto> obtenerCartillasVacunacionPorCanUuid(String canUuid);
    CanCartillaVacunacionDto guardarCartillaVacunacion(String canUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo);
    File obtenerPdfCartillaVacunacion(String canUuid, String cartillaUuid);
    CanCartillaVacunacionDto modificarCartillaVacunacion(String canUuid, String cartillaUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo);
    CanCartillaVacunacionDto borrarCartillaVacunacion(String canUuid, String cartillaUuid, String username);
}
