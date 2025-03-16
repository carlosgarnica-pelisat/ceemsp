package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioTelefonoDto;

import java.util.List;

public interface EmpresaDomicilioTelefonoService {
    List<EmpresaDomicilioTelefonoDto> obtenerTelefonosPorDomicilio(String empresaUuid, String domicilioUuid);
    EmpresaDomicilioTelefonoDto obtenerTelefonoPorUuid(String empresaUuid, String domicilioUuid, String telefonoUuid);
    EmpresaDomicilioTelefonoDto guardarTelefono(String empresaUuid, String domicilioUuid, String username, EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto);
    EmpresaDomicilioTelefonoDto modificarTelefono(String empresaUuid, String domicilioUuid, String telefonoUuid, String username, EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto);
    EmpresaDomicilioTelefonoDto eliminarTelefono(String empresaUuid, String domicilioUuid, String telefonoUuid, String username);
}
