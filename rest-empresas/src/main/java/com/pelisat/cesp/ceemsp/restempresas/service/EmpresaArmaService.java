package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;

import java.util.List;

public interface EmpresaArmaService {
    List<ArmaDto> obtenerArmasPorEmpresa(String username);
    List<ArmaDto> obtenerArmasCortasPorEmpresa(String username);
    List<ArmaDto> obtenerArmasLargasPorEmpresa(String username);
    ArmaDto obtenerArmaPorId(Integer id);
}
