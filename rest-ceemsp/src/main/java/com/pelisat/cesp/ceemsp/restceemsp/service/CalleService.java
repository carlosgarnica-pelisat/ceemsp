package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CalleDto;

import java.util.List;

public interface CalleService {
    List<CalleDto> obtenerTodasLasCalles(Integer limit);
    List<CalleDto> obtenerCallesPorQuery(String query);
    CalleDto obtenerCallePorId(Integer id);
}
