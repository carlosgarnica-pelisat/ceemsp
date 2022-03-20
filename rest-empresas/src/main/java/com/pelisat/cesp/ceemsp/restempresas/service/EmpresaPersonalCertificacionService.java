package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;

import java.util.List;

public interface EmpresaPersonalCertificacionService {
    List<PersonalCertificacionDto> obtenerCertificacionesPorPersona(String personaUUid);
    PersonalCertificacionDto guardarCertificacion(String personaUuid, String username, PersonalCertificacionDto personalCertificacionDto);
    PersonalCertificacionDto modificarCertificacion(String personaUuid, String certificacionUuid, String username, PersonalCertificacionDto personalCertificacionDto);
    PersonalCertificacionDto eliminarCertificacion(String personaUuid, String certificacionUuid, String username);
}
