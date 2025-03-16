package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.CanFotografiaMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface CanFotografiaService {
    List<CanFotografiaMetadata> mostrarCanFotografias(String uuid, String canUuid);
    File descargarFotografiaCan(String uuid, String canUuid, String fotografiaUuid);
    void guardarCanFotografia(String uuid, String personalUuid, String username, MultipartFile multipartFile, CanFotografiaMetadata metadata);
    void eliminarCanFotografia(String uuid, String canUuid, String fotografiaUuid, String username);
}
