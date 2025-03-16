package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.CanFotografiaMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaCanFotografiaService {
    List<CanFotografiaMetadata> mostrarCanFotografias(String canUuid);
    File descargarFotografiaCan(String canUuid, String fotografiaUuid);
    void guardarCanFotografia(String canUuid, String username, MultipartFile multipartFile, CanFotografiaMetadata metadata);
    void eliminarCanFotografia(String canUuid, String fotografiaUuid, String username);
}
