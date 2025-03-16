package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaUniformeElementoService {
    List<EmpresaUniformeElementoDto> obtenerElementosUniformePorEmpresaUuid(String empresaUuid, String uniformeUuid);
    File obtenerArchivoUniforme(String empresaUuid, String uniformeUuid, String elementoUuid);
    EmpresaUniformeElementoDto guardarUniformeElemento(String empresaUuid, String uniformeUuid, String usuario, EmpresaUniformeElementoDto empresaUniformeDto, MultipartFile multipartFile);
    EmpresaUniformeElementoDto modificarUniformeElemento(String empresaUuid, String uniformeUuid, String elementoUuid, String usuario, EmpresaUniformeElementoDto empresaUniformeElementoDto, MultipartFile multipartFile);
    EmpresaUniformeElementoDto eliminarUniformeElemento(String empresaUuid, String uniformeUuid, String elementoUuid, String usuario);
}
