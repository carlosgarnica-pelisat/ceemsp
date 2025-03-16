package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.AcuerdoDto;

import java.io.File;
import java.util.List;

public interface EmpresaAcuerdoService {
    List<AcuerdoDto> obtenerAcuerdosEmpresa(String uuid);
    AcuerdoDto obtenerAcuerdoPorUuid(String acuerdoUuid);
    File obtenerArchivoAcuerdo(String acuerdoUuid);
}
