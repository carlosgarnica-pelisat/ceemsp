package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioTelefonoDto;

import java.util.List;

public interface EmpresaDomicilioTelefonoService {
    List<EmpresaDomicilioTelefonoDto> obtenerTelefonosPorDomicilio(String domicilioUuid);
    EmpresaDomicilioTelefonoDto obtenerTelefonoPorUuid(String domicilioUuid, String telefonoUuid);
    EmpresaDomicilioTelefonoDto guardarTelefono(String domicilioUuid, String username, EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto);
    EmpresaDomicilioTelefonoDto modificarTelefono(String domicilioUuid, String telefonoUuid, String username, EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto);
    EmpresaDomicilioTelefonoDto eliminarTelefono(String domicilioUuid, String telefonoUuid, String username);
}
