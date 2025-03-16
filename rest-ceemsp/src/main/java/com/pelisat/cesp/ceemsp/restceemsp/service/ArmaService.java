package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface ArmaService {
    List<ArmaDto> obtenerArmasPorEmpresaUuid(String uuid);
    List<ArmaDto> obtenerArmasCortasPorEmpresaUuid(String uuid);
    List<ArmaDto> obtenerArmasLargasPorEmpresaUuid(String uuid);
    List<ArmaDto> obtenerArmasPorLicenciaColectivaUuid(String empresaUuid, String licenciaColectivaUuid);
    List<ArmaDto> obtenerTodasArmasPorLicenciaColectivaUuid(String empresaUuid, String licenciaColectivaUuid);
    List<ArmaDto> obtenerTodasArmasDeposito(String empresaUuid);
    ArmaDto obtenerArmaPorUuid(String uuid, String armaUuid);
    File descargarDocumentoFundatorio(String uuid, String armawUuid);
    ArmaDto obtenerArmaPorId(String uuid, Integer armaId);
    ArmaDto guardarArma(String uuid, String licenciaColectivaUuid, String username, ArmaDto armaDto);
    ArmaDto modificarArma(String uuid, String licenciaColectivaUuid, String armaUuid, String username, ArmaDto armaDto);
    ArmaDto eliminarArma(String uuid, String licenciaColectivaUuid, String armaUuid, String username, ArmaDto arma, MultipartFile documentoFundatorio);
    ArmaDto cambiarStatusCustodia(String uuid, String licenciaColectivaUuid, String armaUuid, String username, String relatoHechos, MultipartFile documentoFundatorio);
    List<PersonalArmaDto> obtenerMovimientosArma(String uuid, String licenciaColectivaUuid, String armaUuid);
    List<ArmaDomicilioDto> obtenerMovimientosDomiciliosArma(String uuid, String licenciaColectivaUuid, String armaUuid);
    List<IncidenciaDto> obtenerIncidenciasPorArma(String uuid, String armaUuid);
}
