package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ComunicadoGeneralDto;

import java.util.List;

public interface ComunicadoGeneralService {
    List<ComunicadoGeneralDto> obtenerComunicadosGenerales(String titulo, Integer mes, Integer ano);
    ComunicadoGeneralDto obtenerComunicadoPorUuid(String uuid);
    ComunicadoGeneralDto obtenerUltimoComunicado();
    ComunicadoGeneralDto guardarComunicado(String username, ComunicadoGeneralDto comunicadoGeneralDto);
    ComunicadoGeneralDto modificarComunicado(String uuid, String username, ComunicadoGeneralDto comunicadoGeneralDto);
    ComunicadoGeneralDto eliminarComunicado(String uuid, String username);
}
