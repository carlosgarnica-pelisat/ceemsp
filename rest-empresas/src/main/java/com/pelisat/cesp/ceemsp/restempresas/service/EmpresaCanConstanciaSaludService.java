package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanConstanciaSaludDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaCanConstanciaSaludService {
    List<CanConstanciaSaludDto> obtenerConstanciasSaludPorCanUuid(String canUuid);
    File obtenerPdfConstanciaSalud(String canUuid, String constanciaSaludUuid);
    CanConstanciaSaludDto guardarConstanciaSalud(String canUuid, String username, CanConstanciaSaludDto canConstanciaSaludDto, MultipartFile archivo);
    CanConstanciaSaludDto modificarConstanciaSalud(String canUuid, String constanciaUuid, String username, CanConstanciaSaludDto canConstanciaSaludDto, MultipartFile archivo);
    CanConstanciaSaludDto eliminarConstanciaSalud(String canUuid, String constanciaUuid, String username);
}
