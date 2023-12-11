package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface CanService {
    List<CanDto> obtenerCanesPorEmpresa(String empresaUuid);
    List<CanDto> obtenerCanesEliminadosPorEmpresa(String empresaUuid);
    List<CanDto> obtenerCanesEnInstalacionesPorEmpresa(String empresaUuid);
    CanDto obtenerCanPorUuid(String empresaUuid, String canUuid, boolean soloEntidad);
    File descargarDocumentoFundatorio(String empresaUuid, String canUuid);
    CanDto obtenerCanPorId(int id);
    CanDto guardarCan(String empresaUuid, String username, CanDto canDto);
    CanDto modificarCan(String empresaUuid, String canUuid, String username, CanDto canDto);
    CanDto eliminarCan(String empresaUuid, String canUuid, String username, CanDto canDto, MultipartFile multipartFile);
    List<PersonalCanDto> obtenerMovimientosCan(String uuid, String canUuid);
    List<CanDomicilioDto> obtenerMovimientosDomicilioCan(String uuid, String canUuid);
}
