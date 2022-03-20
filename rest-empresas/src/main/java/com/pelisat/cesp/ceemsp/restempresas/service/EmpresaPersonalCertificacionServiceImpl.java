package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalCertificacionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.PersonalCertificacion;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonalCertificacionRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaPersonalCertificacionServiceImpl implements EmpresaPersonalCertificacionService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaPersonalCertificacionService.class);
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final PersonalCertificacionRepository personalCertificacionRepository;
    private final PersonaRepository personaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public EmpresaPersonalCertificacionServiceImpl(EmpresaService empresaService, UsuarioService usuarioService,
                                            PersonalCertificacionRepository personalCertificacionRepository, DaoToDtoConverter daoToDtoConverter,
                                            DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                            PersonaRepository personaRepository) {
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.personalCertificacionRepository = personalCertificacionRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.personaRepository = personaRepository;
    }

    @Override
    public List<PersonalCertificacionDto> obtenerCertificacionesPorPersona(String personaUuid) {
        if(StringUtils.isBlank(personaUuid)) {
            logger.warn("El uuid de la persona o de la empresa vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las certificaciones para la persona [{}]", personaUuid);
        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        if(personal == null) {
            logger.warn("La persona o el personal en la base de datos no existen");
            throw new NotFoundResourceException();
        }

        List<PersonalCertificacion> personalCertificaciones = personalCertificacionRepository.getAllByPersonalAndEliminadoFalse(personal.getId());

        return personalCertificaciones.stream().map(daoToDtoConverter::convertDaoToDtoPersonalCertificacion).collect(Collectors.toList());
    }

    @Override
    public PersonalCertificacionDto guardarCertificacion(String personaUuid, String username, PersonalCertificacionDto personalCertificacionDto) {
        if(StringUtils.isBlank(personaUuid) || StringUtils.isBlank(username) || personalCertificacionDto == null) {
            logger.warn("El uuid de la persona o la empresa, el usuario o la certificacion vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("La certificacion se esta guardando");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        PersonalCertificacion personalCertificacion = dtoToDaoConverter.convertDtoToDaoPersonalCertificacion(personalCertificacionDto);
        daoHelper.fulfillAuditorFields(true, personalCertificacion, usuarioDto.getId());
        personalCertificacion.setFechaFin(LocalDate.parse(personalCertificacionDto.getFechaFin()));
        personalCertificacion.setFechaInicio(LocalDate.parse(personalCertificacionDto.getFechaInicio()));
        personalCertificacion.setPersonal(personal.getId());

        PersonalCertificacion certificacionCreada = personalCertificacionRepository.save(personalCertificacion);
        return daoToDtoConverter.convertDaoToDtoPersonalCertificacion(certificacionCreada);
    }

    @Override
    public PersonalCertificacionDto modificarCertificacion(String personaUuid, String certificacionUuid, String username, PersonalCertificacionDto personalCertificacionDto) {
        if(StringUtils.isBlank(personaUuid) || StringUtils.isBlank(certificacionUuid) || StringUtils.isBlank(username) || personalCertificacionDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta modificando la certificacion de la persona con el uuid [{}]", certificacionUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        PersonalCertificacion personalCertificacion = personalCertificacionRepository.findByUuidAndEliminadoFalse(certificacionUuid);

        if(personalCertificacion == null) {
            logger.warn("El socio a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personalCertificacion.setFechaInicio(LocalDate.parse(personalCertificacionDto.getFechaInicio()));
        personalCertificacion.setFechaFin(LocalDate.parse(personalCertificacionDto.getFechaFin()));
        personalCertificacion.setDuracion(personalCertificacionDto.getDuracion());
        personalCertificacion.setNombre(personalCertificacionDto.getNombre());
        personalCertificacion.setNombreInstructor(personalCertificacionDto.getNombreInstructor());
        daoHelper.fulfillAuditorFields(false, personalCertificacion, usuario.getId());

        personalCertificacionRepository.save(personalCertificacion);

        return daoToDtoConverter.convertDaoToDtoPersonalCertificacion(personalCertificacion);
    }

    @Override
    public PersonalCertificacionDto eliminarCertificacion(String personaUuid, String certificacionUuid, String username) {
        if(StringUtils.isBlank(personaUuid) || StringUtils.isBlank(certificacionUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta eliminando la certificacion de la persona con el uuid [{}]", certificacionUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        PersonalCertificacion personalCertificacion = personalCertificacionRepository.findByUuidAndEliminadoFalse(certificacionUuid);

        if(personalCertificacion == null) {
            logger.warn("El socio a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personalCertificacion.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, personalCertificacion, usuario.getId());
        personalCertificacionRepository.save(personalCertificacion);
        return daoToDtoConverter.convertDaoToDtoPersonalCertificacion(personalCertificacion);
    }
}
