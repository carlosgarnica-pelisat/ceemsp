package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;

import java.util.List;

public interface EmpresaLicenciaColectivaDomicilioService {
    List<EmpresaDomicilioDto> obtenerDomiciliosPorLicenciaColectiva(String licenciaColectivaUuid);
    EmpresaDomicilioDto guardarDomicilioEnLicenciaColectiva(String licenciaColectivaUuid, String username, EmpresaDomicilioDto empresaDomicilioDto);
    EmpresaDomicilioDto eliminarDomicilioEnLicenciaColectiva(String licenciaColectivaUuid, String domicilioUuid, String username);
}
