package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoUsoDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;

import java.util.List;

public interface VisitaService {
    List<VisitaDto> obtenerTodas();
    VisitaDto obtenerPorUuid(String uuid);
    VisitaDto obtenerPorId(Integer id);
    VisitaDto crearNuevo(VisitaDto visitaDto, String username);
}
