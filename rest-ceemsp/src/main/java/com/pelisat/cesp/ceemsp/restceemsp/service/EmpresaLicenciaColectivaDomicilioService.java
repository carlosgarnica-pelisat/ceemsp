package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;

import java.util.List;

public interface EmpresaLicenciaColectivaDomicilioService {
    List<EmpresaDomicilioDto> obtenerDomiciliosPorLicenciaColectiva(String empresaUuid, String licenciaColectivaUuid);

    EmpresaDomicilioDto guardarDomicilioEnLicenciaColectiva(String empresaUuid, String licenciaColectivaUuid, String username, EmpresaDomicilioDto empresaDomicilioDto);
}
