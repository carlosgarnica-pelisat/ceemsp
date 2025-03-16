package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;

public interface ValidacionService {
    ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto);
    ExistePersonaDto buscarExistenciaPersona(ExistePersonaDto existePersonaDto);
    ExisteEmpresaDto buscarExistenciaEmpresa(ExisteEmpresaDto existeEmpresaDto);
    ExisteEscrituraDto buscarEscrituraDto(ExisteEscrituraDto existeEscrituraDto);
    ExisteUsuarioDto buscarUsuario(ExisteUsuarioDto existeUsuarioDto);
    ExisteArmaDto buscarArma(ExisteArmaDto existeArmaDto);
}
