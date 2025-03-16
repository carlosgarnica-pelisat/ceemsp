package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaUniformeService {
    List<EmpresaUniformeDto> obtenerUniformesPorEmpresaUuid(String username);
    EmpresaUniformeDto obtenerUniformePorUuid(String uniformeUuid);
    File descargarFotoUniforme(String uniformeUuid);
    EmpresaUniformeDto guardarUniforme(String usuario, EmpresaUniformeDto empresaUniformeDto, MultipartFile multipartFile);
    EmpresaUniformeDto modificarUniforme(String uniformeUuid, String usuario, EmpresaUniformeDto empresaUniformeDto);
    EmpresaUniformeDto eliminarUniforme(String uniformeUuid, String usuario);
}
