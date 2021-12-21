package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;

import java.util.List;

public interface EmpresaDomicilioService {
    List<EmpresaDomicilioDto> obtenerPorEmpresaId(int empresaId);

    List<EmpresaDomicilioDto> obtenerPorEmpresaUuid(String empresaUuid);

    EmpresaDomicilioDto obtenerPorId(int id);

    EmpresaDomicilioDto obtenerPorUuid(String uuid, String domicilioUuid);

    EmpresaDomicilioDto guardar(String empresaUuid, String username, EmpresaDomicilioDto empresaDomicilioDto);
}
