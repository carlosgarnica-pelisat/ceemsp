package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaCanService {
    List<CanDto> obtenerCanesPorEmpresa(String username);
    List<CanDto> obtenerCanesEnInstalacionesPorEmpresa(String username);
    CanDto obtenerCanPorUuid(String canUuid, boolean soloEntidad);
    CanDto obtenerCanPorId(int id);
    CanDto guardarCan(String username, CanDto canDto);
    CanDto modificarCan(String canUuid, String username, CanDto canDto);
    CanDto eliminarCan(String canUuid, String username, CanDto canDto, MultipartFile multipartFile);
}
