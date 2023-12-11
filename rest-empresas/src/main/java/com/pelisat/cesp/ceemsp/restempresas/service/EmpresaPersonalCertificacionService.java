package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaPersonalCertificacionService {
    List<PersonalCertificacionDto> obtenerCertificacionesPorPersona(String personaUUid);
    File obtenerPdfCertificacion(String personaUuid, String certificaionUuid);
    PersonalCertificacionDto guardarCertificacion(String personaUuid, String username, PersonalCertificacionDto personalCertificacionDto, MultipartFile file);
    PersonalCertificacionDto modificarCertificacion(String personaUuid, String certificacionUuid, String username, PersonalCertificacionDto personalCertificacionDto);
    PersonalCertificacionDto eliminarCertificacion(String personaUuid, String certificacionUuid, String username);
}
