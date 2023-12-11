package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface PersonaService {
    List<PersonaDto> obtenerTodos(String empresaUuid);
    List<PersonaDto> obtenerPersonasEliminadas(String empresaUuid);
    List<PersonaDto> obtenerPersonasNoAsignadas(String empresaUuid);
    PersonaDto obtenerPorUuid(String empresaUuid, String personaUuid);
    PersonaDto obtenerPorId(Integer id);
    File descargarDocumentoFundatorio(String uuid, String personaUuid);
    PersonaDto crearNuevo(PersonaDto personalDto, String username, String empresaUuid);
    PersonaDto modificarInformacionPuesto(PersonaDto personaDto, String username, String empresaUuid, String personaUuid, MultipartFile multipartFile);
    PersonaDto modificarPersona(String empresaUuid, String personaUuid, String username, PersonaDto personaDto);
    PersonaDto eliminarPersona(String empresaUuid, String personaUuid, String username, PersonaDto persona, MultipartFile multipartFile);
    File descargarVolanteCuip(String empresaUuid, String personaUuid);
    void asignarCanAPersona(String empresaUuid, String personaUuid, PersonalCanDto personaDto, String username);
    void desasignarCanAPersona(String empresaUuid, String personaUuid, PersonalCanDto personalCanDto, String username);
    void asignarVehiculoAPersona(String empresaUuid, String personaUuid, PersonalVehiculoDto personaDto, String username);
    void desasignarVehiculoAPersona(String empresaUuid, String personaUuid, PersonalVehiculoDto personalVehiculoDto, String username);
    void asignarArmaCortaAPersona(String empresaUuid, String personaUuid, PersonalArmaDto personaDto, String username);
    void desasignarArmaCortaAPersona(String empresaUuid, String personaUuid, PersonalArmaDto personalArmaDto, String username);
    void asignarArmaLargaAPersona(String empresaUuid, String personaUuid, PersonalArmaDto personaDto, String username);
    void desasignarArmaLargaAPersona(String empresaUuid, String personaUuid, PersonalArmaDto personalArmaDto, String username);
}
