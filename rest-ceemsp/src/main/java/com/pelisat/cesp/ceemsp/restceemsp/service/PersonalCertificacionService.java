package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface PersonalCertificacionService {
    List<PersonalCertificacionDto> obtenerCertificacionesPorPersona(String empresaUuid, String personaUUid);
    File obtenerPdfCertificacion(String empresaUuid, String personaUuid, String certificaionUuid);
    PersonalCertificacionDto guardarCertificacion(String empresaUuid, String personaUuid, String username, PersonalCertificacionDto personalCertificacionDto, MultipartFile multipartFile);
    PersonalCertificacionDto modificarCertificacion(String empresaUuid, String personaUuid, String certificacionUuid, String username, PersonalCertificacionDto personalCertificacionDto, MultipartFile multipartFile);
    PersonalCertificacionDto eliminarCertificacion(String empresaUuid, String personaUuid, String certificacionUuid, String username);
}
