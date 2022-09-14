package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.AcuerdoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface AcuerdoService {
    List<AcuerdoDto> obtenerAcuerdosEmpresa(String uuid);
    AcuerdoDto obtenerAcuerdoPorUuid(String uuid, String acuerdoUuid);
    File obtenerArchivoAcuerdo(String uuid, String acuerdoUuid);
    AcuerdoDto guardarAcuerdo(String uuid, AcuerdoDto acuerdoDto, String username, MultipartFile multipartFile);
    AcuerdoDto modificarAcuerdo(String uuid, String acuerdoUuid, AcuerdoDto acuerdoDto, String username, MultipartFile multipartFile);
    AcuerdoDto eliminarAcuerdo(String uuid, String acuerdoUuid, String username, AcuerdoDto acuerdoDto, MultipartFile file);
}
