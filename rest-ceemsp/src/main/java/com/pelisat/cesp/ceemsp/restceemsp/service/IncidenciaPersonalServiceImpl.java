package com.pelisat.cesp.ceemsp.restceemsp.service;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenciaPersonalServiceImpl implements IncidenciaPersonalService {

    private final Logger logger = LoggerFactory.getLogger(IncidenciaPersonalService.class);
    private final IncidenciaPersonaRepository incidenciaPersonaRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final PersonaRepository personaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<IncidenciaPersona> daoHelper;

    @Autowired
    public IncidenciaPersonalServiceImpl(IncidenciaPersonaRepository incidenciaPersonaRepository, IncidenciaRepository incidenciaRepository,
                                         DaoToDtoConverter daoToDtoConverter, UsuarioService usuarioService, DaoHelper<IncidenciaPersona> daoHelper,
                                         PersonaRepository personaRepository) {
        this.incidenciaPersonaRepository = incidenciaPersonaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.personaRepository = personaRepository;
    }

    @Override
    public List<PersonaDto> obtenerPersonasIncidencia(String empresaUuid, String incidenciaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando persona a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<IncidenciaPersona> incidenciaPersonas = incidenciaPersonaRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());
        return incidenciaPersonas.stream().map(p -> {
            Personal personal = personaRepository.getOne(p.getPersona());
            return daoToDtoConverter.convertDaoToDtoPersona(personal);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PersonaDto agregarPersonaIncidencia(String empresaUuid, String incidenciaUuid, String username, PersonaDto personaDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || personaDto == null) {
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
    @Transactional
    public PersonaDto eliminarPersonaIncidencia(String empresaUuid, String incidenciaUuid, String personaIncidenciaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(personaIncidenciaUuid)) {
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
