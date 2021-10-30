package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;

import java.util.List;

public interface EmpresaLicenciaColectivaService {
    List<EmpresaLicenciaColectivaDto> obtenerLicenciasColectivasPorEmpresa(String empresaUuid);

    EmpresaLicenciaColectivaDto obtenerLicenciaColectivaPorUuid(String empresaUuid, String licenciaUuid, boolean soloEntidad);

    EmpresaLicenciaColectivaDto guardarLicenciaColectiva(String empresaUuid, String username, EmpresaLicenciaColectivaDto licenciaColectivaDto);
}
