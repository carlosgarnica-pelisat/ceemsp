package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.VehiculoFotografiaMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface VehiculoFotografiaService {
    List<VehiculoFotografiaMetadata> mostrarVehiculoFotografias(String uuid, String personalUuid);
    File descargarFotografiaVehiculo(String uuid, String personalUuid, String fotografiaUuid);
    void guardarVehiculoFotografia(String uuid, String personalUuid, String username, MultipartFile multipartFile, VehiculoFotografiaMetadata metadata);
}
