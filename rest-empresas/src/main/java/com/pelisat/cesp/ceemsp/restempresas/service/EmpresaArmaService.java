package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;

import java.util.List;

public interface EmpresaArmaService {
    List<ArmaDto> obtenerArmasPorLicenciaColectivaUuid(String licenciaColectivaUuid);
    ArmaDto obtenerArmaPorId(Integer armaId);
    ArmaDto guardarArma(String licenciaColectivaUuid, String username, ArmaDto armaDto);
    ArmaDto modificarArma(String licenciaColectivaUuid, String armaUuid, String username, ArmaDto armaDto);
    ArmaDto eliminarArma(String licenciaColectivaUuid, String armaUuid, String username);
}
