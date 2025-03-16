package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.VehiculoFotografiaMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaVehiculoFotografiaService {
    List<VehiculoFotografiaMetadata> mostrarVehiculoFotografias(String personalUuid);
    File descargarFotografiaVehiculo(String personalUuid, String fotografiaUuid);
    void guardarVehiculoFotografia(String vehiculoUuid, String username, MultipartFile multipartFile, VehiculoFotografiaMetadata metadata);
    void eliminarVehiculoFotografia(String vehiculoUuid, String fotografiaUuid, String username);
}
