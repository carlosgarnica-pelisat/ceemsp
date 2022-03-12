package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface PersonalFotografiaService {
    List<PersonalFotografiaMetadata> mostrarPersonalFotografias(String uuid, String personalUuid);
    File descargarFotografiaPersona(String uuid, String personalUuid, String fotografiaUuid);
    void guardarPersonalFotografia(String uuid, String personalUuid, String username, MultipartFile multipartFile, PersonalFotografiaMetadata metadata);
    PersonalFotografiaMetadata eliminarPersonalFotografia(String uuid, String personalUuid, String fotografiaUuid, String username);
}
