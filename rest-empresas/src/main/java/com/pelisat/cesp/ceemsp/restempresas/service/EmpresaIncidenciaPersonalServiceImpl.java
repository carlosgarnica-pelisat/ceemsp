package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Incidencia;
import com.pelisat.cesp.ceemsp.database.model.IncidenciaPersona;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaPersonaRepository;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaIncidenciaPersonalServiceImpl implements EmpresaIncidenciaPersonalService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaIncidenciaPersonalService.class);
    private final IncidenciaPersonaRepository incidenciaPersonaRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final PersonaRepository personaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<IncidenciaPersona> daoHelper;

    @Autowired
    public EmpresaIncidenciaPersonalServiceImpl(
            IncidenciaPersonaRepository incidenciaPersonaRepository, IncidenciaRepository incidenciaRepository,
            PersonaRepository personaRepository, DaoToDtoConverter daoToDtoConverter,
            UsuarioService usuarioService, DaoHelper<IncidenciaPersona> daoHelper
    ) {
        this.incidenciaPersonaRepository = incidenciaPersonaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.personaRepository = personaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
    }

    @Override
    public PersonaDto agregarPersonaIncidencia(String incidenciaUuid, String username, PersonaDto personaDto) {
        if(StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || personaDto == null) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando persona a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        IncidenciaPersona incidenciaPersona = new IncidenciaPersona();
        incidenciaPersona.setIncidencia(incidencia.getId());
        incidenciaPersona.setPersona(personaDto.getId());
        daoHelper.fulfillAuditorFields(true, incidenciaPersona, usuario.getId());

        incidenciaPersonaRepository.save(incidenciaPersona);

        return personaDto;
    }

    @Override
    public PersonaDto eliminarPersonaIncidencia(String incidenciaUuid, String personaIncidenciaUuid, String username) {
        if(StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(personaIncidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando persona de la incidencia con el uuid [{}]", personaIncidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaIncidenciaUuid);

        if(personal == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        IncidenciaPersona incidenciaPersona = incidenciaPersonaRepository.getByPersonaAndIncidenciaAndEliminadoFalse(personal.getId(), incidencia.getId());

        if(incidenciaPersona == null) {
            logger.warn("La persona en la incidencia no se encuentra registrada");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        incidenciaPersona.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, incidenciaPersona, usuario.getId());
        incidenciaPersonaRepository.save(incidenciaPersona);

        return null;
    }
}
