package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonaServiceImpl implements PersonaService {

    private final DaoHelper<CommonModel> daoHelper;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
    private final ModalidadService modalidadService;
    private final PersonalPuestoDeTrabajoService personalPuestoDeTrabajoService;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final PersonalNacionalidadService personalNacionalidadService;
    private final PersonalCertificacionService personalCertificacionService;
    private final Logger logger = LoggerFactory.getLogger(PersonaService.class);

    @Autowired
    public PersonaServiceImpl(DaoHelper<CommonModel> daoHelper, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaService empresaService,
                              UsuarioService usuarioService, PersonaRepository personaRepository,
                              ModalidadService modalidadService, PersonalPuestoDeTrabajoServiceImpl personalPuestoDeTrabajoService,
                              EmpresaDomicilioService empresaDomicilioService, PersonalNacionalidadService personalNacionalidadService,
                              PersonalCertificacionService personalCertificacionService) {
        this.daoHelper = daoHelper;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.personaRepository = personaRepository;
        this.personalPuestoDeTrabajoService = personalPuestoDeTrabajoService;
        this.modalidadService = modalidadService;
        this.empresaDomicilioService = empresaDomicilioService;
        this.personalNacionalidadService = personalNacionalidadService;
        this.personalCertificacionService = personalCertificacionService;
    }

    @Override
    public List<PersonaDto> obtenerTodos(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Personal> personal = personaRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return personal.stream().map(daoToDtoConverter::convertDaoToDtoPersona).collect(Collectors.toList());
    }

    @Override
    public PersonaDto obtenerPorUuid(String empresaUuid, String personaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid)) {
            logger.warn("El uuid de la empresa o de la persona vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo a la persona con el uuid [{}]", personaUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        if(personal == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(personal.getEmpresa() != empresaDto.getId()) {
            logger.warn("El personal a consultar no pertenece a este recurso");
            throw new MissingRelationshipException();
        }

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setCertificaciones(personalCertificacionService.obtenerCertificacionesPorPersona(empresaUuid, personaUuid));
        personaDto.setNacionalidad(personalNacionalidadService.obtenerPorId(personal.getNacionalidad()));
        return personaDto;
    }

    @Override
    public PersonaDto obtenerPorId(String empresaUuid, Integer id) {
        if(StringUtils.isBlank(empresaUuid) || id == null || id < 1) {
            logger.warn("La empresa o el id vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la persona con el id [{}]", id);

        Personal personal = personaRepository.getOne(id);

        if(personal == null || personal.getEliminado())  {
            logger.warn("La persona no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setNacionalidad(personalNacionalidadService.obtenerPorId(personal.getNacionalidad()));

        return personaDto;
    }

    @Override
    public PersonaDto crearNuevo(PersonaDto personalDto, String username, String empresaUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || personalDto == null) {
            logger.warn("El usuario, la empresa o el personal a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo personal");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        Personal personal = dtoToDaoConverter.convertDtoToDaoPersonal(personalDto);
        daoHelper.fulfillAuditorFields(true, personal, usuarioDto.getId());
        personal.setEmpresa(empresaDto.getId());

        Personal personalCreado = personaRepository.save(personal);

        PersonaDto response = daoToDtoConverter.convertDaoToDtoPersona(personalCreado);
        return response;
    }
}
