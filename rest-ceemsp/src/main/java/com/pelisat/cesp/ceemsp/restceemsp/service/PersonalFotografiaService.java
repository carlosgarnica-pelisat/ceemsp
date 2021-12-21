package com.pelisat.cesp.ceemsp.restceemsp.service;

import org.springframework.web.multipart.MultipartFile;

public interface PersonalFotografiaService {
    void mostrarPersonalFotografias(String uuid, String personalUuid);

    void guardarPersonalFotografia(String uuid, String personalUuid, String username, MultipartFile multipartFile);
}
