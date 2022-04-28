package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CanService {
    List<CanDto> obtenerCanesPorEmpresa(String empresaUuid);
    CanDto obtenerCanPorUuid(String empresaUuid, String canUuid, boolean soloEntidad);
    CanDto obtenerCanPorId(int id);
    CanDto guardarCan(String empresaUuid, String username, CanDto canDto);
    CanDto modificarCan(String empresaUuid, String canUuid, String username, CanDto canDto);
    CanDto eliminarCan(String empresaUuid, String canUuid, String username, CanDto canDto, MultipartFile multipartFile);
}
