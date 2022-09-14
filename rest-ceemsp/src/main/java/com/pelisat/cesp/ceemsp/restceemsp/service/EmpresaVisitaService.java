package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;

import java.util.List;

public interface EmpresaVisitaService {
    List<VisitaDto> obtenerVisitasPorEmpresa(String uuid);
}
