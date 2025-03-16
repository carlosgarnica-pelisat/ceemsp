package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaUniformeElementoService {
    List<EmpresaUniformeElementoDto> obtenerElementosUniformePorEmpresaUuid(String uniformeUuid);
    File obtenerArchivoUniforme(String uniformeUuid, String elementoUuid);
    EmpresaUniformeElementoDto guardarUniformeElemento(String uniformeUuid, String usuario, EmpresaUniformeElementoDto empresaUniformeDto, MultipartFile multipartFile);
    EmpresaUniformeElementoDto modificarUniformeElemento(String uniformeUuid, String elementoUuid, String usuario, EmpresaUniformeElementoDto empresaUniformeElementoDto, MultipartFile multipartFile);
    EmpresaUniformeElementoDto eliminarUniformeElemento(String uniformeUuid, String elementoUuid, String usuario);
}
