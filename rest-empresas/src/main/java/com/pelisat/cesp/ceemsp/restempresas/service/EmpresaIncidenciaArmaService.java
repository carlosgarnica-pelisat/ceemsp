package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;

public interface EmpresaIncidenciaArmaService {
    ArmaDto agregarArmaIncidencia(String incidenciaUuid, String username, ArmaDto armaDto);
    ArmaDto eliminarArmaIncidencia(String incidenciaUuid, String armaIncidenciaUuid, String username);
}
