package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraConsejoDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraRepresentanteDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraSocioDto;

import java.util.List;

public interface EmpresaEscrituraConsejoService {
    List<EmpresaEscrituraConsejoDto> obtenerConsejosPorEscritura(String empresaUuid, String escrituraUuid);
    EmpresaEscrituraConsejoDto obtenerConsejoPorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    EmpresaEscrituraConsejoDto crearConsejo(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto);
    EmpresaEscrituraConsejoDto actualizarConsejo(String empresaUuid, String escrituraUuid, String consejoUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto);
    EmpresaEscrituraConsejoDto eliminarConsejo(String empresaUuid, String escrituraUuid, String consejoUuid, String username);
}
