package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;

import java.io.File;
import java.util.List;

public interface EmpresaVisitaService {
    List<VisitaDto> obtenerVisitasPorEmpresa(String username);
    VisitaDto obtenerVisitaPorUuid(String visitaUuid);
    File descargarArchivoVisita(String visitaUuid, String archivoUuid);
}
