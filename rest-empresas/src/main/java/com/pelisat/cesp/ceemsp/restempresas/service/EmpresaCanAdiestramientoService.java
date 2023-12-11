package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaCanAdiestramientoService {
    List<CanAdiestramientoDto> obtenerAdiestramientosPorCanUuid(String canUuid);
    CanAdiestramientoDto guardarCanAdiestramiento(String canUuid, String username, CanAdiestramientoDto canAdiestramientoDto, MultipartFile multipartFile);
    CanAdiestramientoDto modificarCanAdiestramiento(String canUuid, String adiestramientoUuid, String username, CanAdiestramientoDto canAdiestramientoDto, MultipartFile multipartFile);
    CanAdiestramientoDto eliminarCanAdiestramiento(String canUuid, String adiestramientoUuid, String username);
    File obtenerAdiestramientoArchivo(String canUuid, String adiestramientoUuid);
}
