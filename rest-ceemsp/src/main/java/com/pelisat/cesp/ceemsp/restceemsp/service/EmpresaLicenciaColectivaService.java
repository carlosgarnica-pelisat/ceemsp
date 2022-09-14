package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaLicenciaColectivaService {
    List<EmpresaLicenciaColectivaDto> obtenerLicenciasColectivasPorEmpresa(String empresaUuid);
    EmpresaLicenciaColectivaDto obtenerLicenciaColectivaPorUuid(String empresaUuid, String licenciaUuid, boolean soloEntidad);
    EmpresaLicenciaColectivaDto guardarLicenciaColectiva(String empresaUuid, String username, EmpresaLicenciaColectivaDto licenciaColectivaDto, MultipartFile multipartFile);
    File descargarLicenciaPdf(String empresaUuid, String licenciaUuid);
    EmpresaLicenciaColectivaDto modificarLicenciaColectiva(String empresaUuid, String licenciaUuid, String username, EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto, MultipartFile multipartFile);
    EmpresaLicenciaColectivaDto eliminarLicenciaColectiva(String empresaUuid, String licenciaUuid, String username, EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto, MultipartFile multipartFile);
}
