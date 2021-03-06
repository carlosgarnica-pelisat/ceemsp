package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;

import java.util.List;

public interface PersonalCertificacionService {
    List<PersonalCertificacionDto> obtenerCertificacionesPorPersona(String empresaUuid, String personaUUid);
    PersonalCertificacionDto guardarCertificacion(String empresaUuid, String personaUuid, String username, PersonalCertificacionDto personalCertificacionDto);
    PersonalCertificacionDto modificarCertificacion(String empresaUuid, String personaUuid, String certificacionUuid, String username, PersonalCertificacionDto personalCertificacionDto);
    PersonalCertificacionDto eliminarCertificacion(String empresaUuid, String personaUuid, String certificacionUuid, String username);
}
