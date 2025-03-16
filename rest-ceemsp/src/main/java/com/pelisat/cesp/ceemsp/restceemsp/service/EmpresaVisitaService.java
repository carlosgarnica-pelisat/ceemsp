package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;

import java.io.File;
import java.util.List;

public interface EmpresaVisitaService {
    List<VisitaDto> obtenerVisitasPorEmpresa(String uuid);
    VisitaDto obtenerVisitaPorUuid(String uuid, String visitaUuid);
    File descargarArchivoVisita(String uuid, String visitaUuid, String archivoUuid);
}
