package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaPersonalFotografiasService {
    List<PersonalFotografiaMetadata> mostrarPersonalFotografias(String personalUuid);
    File descargarFotografiaPersona(String personalUuid, String fotografiaUuid);
    void guardarPersonalFotografia(String personalUuid, String username, MultipartFile multipartFile, PersonalFotografiaMetadata metadata);
    PersonalFotografiaMetadata eliminarPersonalFotografia(String personalUuid, String fotografiaUuid, String username);
}
