package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public interface EmpresaService {
    List<EmpresaDto> obtenerTodas();
    List<EmpresaDto> obtenerPorStatus(EmpresaStatusEnum status);
    EmpresaDto obtenerPorUuid(String uuid);
    EmpresaDto obtenerPorId(int id);
    File obtenerLogo(String uuid);
    File obtenerDocumentoRegistroFederal(String uuid);
    EmpresaDto crearEmpresa(EmpresaDto empresaDto, String username, MultipartFile multipartFile, MultipartFile logo) throws Exception;
    EmpresaDto modificarEmpresa(EmpresaDto empresaDto, String username, String uuid, MultipartFile licenciaColectiva, MultipartFile logo);
    EmpresaDto cambiarStatusEmpresa(EmpresaDto empresaDto, String username, String uuid);
    EmpresaDto eliminarEmpresa(String username, String uuid);
    void cambiarTipoTramiteEmpresa(EmpresaDto empresaDto, TipoTramiteEnum tramiteOrigen, TipoTramiteEnum tramiteDestino, String username);
    void cambiarVigenciaEmpresa(EmpresaDto empresaDto, LocalDate fechaInicio, LocalDate fechaFin, String username);
}
