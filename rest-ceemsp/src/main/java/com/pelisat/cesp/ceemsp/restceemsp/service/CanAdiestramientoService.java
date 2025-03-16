package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface CanAdiestramientoService {
    List<CanAdiestramientoDto> obtenerAdiestramientosPorCanUuid(String empresaUuid, String canUuid);
    List<CanAdiestramientoDto> obtenerTodosAdiestramientosPorCanUuid(String empresaUuid, String canUuid);
    File obtenerAdiestramientoArchivo(String empresaUuid, String canUuid, String adiestramientoUuid);
    CanAdiestramientoDto guardarCanAdiestramiento(String empresaUuid, String canUuid, String username, CanAdiestramientoDto canAdiestramientoDto, MultipartFile multipartFile);
    CanAdiestramientoDto modificarCanAdiestramiento(String empresaUuid, String canUuid, String adiestramientoUuid, String username, CanAdiestramientoDto canAdiestramientoDto, MultipartFile multipartFile);
    CanAdiestramientoDto eliminarCanAdiestramiento(String empresaUuid, String canUuid, String adiestramientoUuid, String username);
}
