package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;

import java.util.List;

public interface ArmaService {
    List<ArmaDto> obtenerArmasPorEmpresaUuid(String uuid);
    List<ArmaDto> obtenerArmasPorLicenciaColectivaUuid(String empresaUuid, String licenciaColectivaUuid);
    ArmaDto obtenerArmaPorUuid(String uuid, String armaUuid);
    ArmaDto obtenerArmaPorId(String uuid, Integer armaId);
    ArmaDto guardarArma(String uuid, String licenciaColectivaUuid, String username, ArmaDto armaDto);
    ArmaDto modificarArma(String uuid, String licenciaColectivaUuid, String armaUuid, String username, ArmaDto armaDto);
    ArmaDto eliminarArma(String uuid, String licenciaColectivaUuid, String armaUuid, String username);
}
