package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaService {
    List<EmpresaDto> obtenerTodas();
    EmpresaDto obtenerPorUuid(String uuid);
    EmpresaDto obtenerPorId(int id);
    EmpresaDto crearEmpresa(EmpresaDto empresaDto, String username, MultipartFile multipartFile);
    EmpresaDto modificarEmpresa(EmpresaDto empresaDto, String username, String uuid);
    EmpresaDto cambiarStatusEmpresa(EmpresaDto empresaDto, String username, String uuid);
    EmpresaDto eliminarEmpresa(String username, String uuid);
}
