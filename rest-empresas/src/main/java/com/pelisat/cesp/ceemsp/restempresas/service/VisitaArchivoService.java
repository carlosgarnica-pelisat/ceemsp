package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.VisitaArchivoMetadata;

import java.io.File;
import java.util.List;

public interface VisitaArchivoService {
    List<VisitaArchivoMetadata> obtenerArchivosPorVisita(String visitaUuid);
    File descargarArchivoVisita(String visitaUuid, String archivoUuid);
}
