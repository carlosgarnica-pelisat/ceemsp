package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.IncidenciaArchivoMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface EmpresaIncidenciaArchivoService {
    File descargarArchivoIncidencia(String incidenciaUUid, String archivoUuid);
    IncidenciaArchivoMetadata agregarArchivoIncidencia(String incidenciaUuid, String username, MultipartFile multipartFile);
    IncidenciaArchivoMetadata eliminarArchivoIncidencia(String incidenciaUuid, String archivoIncidenciaUuid, String username);
}
