package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.VisitaArchivoMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface VisitaArchivoService {
    List<VisitaArchivoMetadata> obtenerArchivosPorVisita(String visitaUuid);
    File descargarArchivoVisita(String visitaUuid, String archivoUuid);
    VisitaArchivoMetadata guardarArchivo(String visitaUuid, String username, MultipartFile archivo, VisitaArchivoMetadata visitaArchivoMetadata);
    VisitaArchivoMetadata eliminarArchivo(String visitaUuid, String archivoUuid, String username);
}
