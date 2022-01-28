package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;

import java.util.List;

public interface EmpresaDomicilioService {
    List<EmpresaDomicilioDto> obtenerDomicilios(String empresaUsername);
    EmpresaDomicilioDto obtenerDomicilioPorUuid(String empresaUsername, String domicilioUuid);
    EmpresaDomicilioDto guardarDomicilio(String empresaUsername, EmpresaDomicilioDto empresaDomicilioDto);
}
