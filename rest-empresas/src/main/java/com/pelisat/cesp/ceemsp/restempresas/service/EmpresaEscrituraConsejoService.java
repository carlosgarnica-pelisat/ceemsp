package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraConsejoDto;

import java.util.List;

public interface EmpresaEscrituraConsejoService {
    List<EmpresaEscrituraConsejoDto> obtenerConsejosPorEscritura(String escrituraUuid);
    EmpresaEscrituraConsejoDto crearConsejo(String escrituraUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto);
    EmpresaEscrituraConsejoDto actualizarConsejo(String escrituraUuid, String consejoUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto);
    EmpresaEscrituraConsejoDto eliminarConsejo(String escrituraUuid, String consejoUuid, String username);
}
