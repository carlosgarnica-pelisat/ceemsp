package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.IncidenciaArchivoMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IncidenciaArchivoService {
    File descargarArchivoIncidencia(String empresaUuid, String incidenciaUUid, String archivoUuid);
    IncidenciaArchivoMetadata agregarArchivoIncidencia(String empresaUuid, String incidenciaUuid, String username, MultipartFile multipartFile);
    IncidenciaArchivoMetadata eliminarArchivoIncidencia(String empresaUuid, String incidenciaUuid, String archivoIncidenciaUuid, String username);
}
