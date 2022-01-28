package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ComunicadoGeneralDto;

import java.util.List;

public interface ComunicadoGeneralService {
    List<ComunicadoGeneralDto> obtenerComunicadosGenerales();
    ComunicadoGeneralDto obtenerComunicadoPorUuid(String uuid);
    ComunicadoGeneralDto obtenerUltimoComunicado();
}
