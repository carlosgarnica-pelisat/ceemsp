package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalCanDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalVehiculoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface EmpresaPersonalService {
    List<PersonaDto> obtenerTodos(String username);
    List<PersonaDto> obtenerSinAsignar(String username);
    PersonaDto obtenerPorUuid(String personaUuid);
    PersonaDto obtenerPorId(Integer id);
    PersonaDto crearNuevo(PersonaDto personalDto, String username);
    File descargarVolanteCuip(String personaUuid);
    PersonaDto modificarInformacionPuesto(PersonaDto personaDto, String username, String personaUuid, MultipartFile multipartFile);
    PersonaDto modificarPersona(String personaUuid, String username, PersonaDto personaDto);
    PersonaDto eliminarPersona(String personaUuid, String username, PersonaDto persona, MultipartFile multipartFile);
    void asignarCanAPersona(String personaUuid, PersonalCanDto personaDto, String username);
    void desasignarCanAPersona(String personaUuid, PersonalCanDto personalCanDto, String username);
    void asignarVehiculoAPersona(String personaUuid, PersonalVehiculoDto personaDto, String username);
    void desasignarVehiculoAPersona(String personaUuid, PersonalVehiculoDto personalVehiculoDto, String username);
    void asignarArmaCortaAPersona(String personaUuid, PersonalArmaDto personaDto, String username);
    void desasignarArmaCortaAPersona(String personaUuid, PersonalArmaDto personalArmaDto, String username);
    void asignarArmaLargaAPersona(String personaUuid, PersonalArmaDto personaDto, String username);
    void desasignarArmaLargaAPersona(String personaUuid, PersonalArmaDto personalArmaDto, String username);

}
