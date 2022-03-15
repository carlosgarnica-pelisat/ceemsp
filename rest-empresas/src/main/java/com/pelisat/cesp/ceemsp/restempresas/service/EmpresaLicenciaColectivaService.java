package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaLicenciaColectivaService {
    List<EmpresaLicenciaColectivaDto> obtenerLicenciasColectivasPorEmpresa(String username);
    EmpresaLicenciaColectivaDto obtenerLicenciaColectivaPorUuid(String licenciaUuid, boolean soloEntidad);
    EmpresaLicenciaColectivaDto guardarLicenciaColectiva(String username, EmpresaLicenciaColectivaDto licenciaColectivaDto, MultipartFile multipartFile);
    File descargarLicenciaPdf(String licenciaUuid);
    EmpresaLicenciaColectivaDto modificarLicenciaColectiva(String licenciaUuid, String username, EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto, MultipartFile multipartFile);
    EmpresaLicenciaColectivaDto eliminarLicenciaColectiva(String licenciaUuid, String username);
}
