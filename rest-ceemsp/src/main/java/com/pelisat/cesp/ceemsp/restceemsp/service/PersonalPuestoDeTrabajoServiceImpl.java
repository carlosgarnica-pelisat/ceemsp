package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalPuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.PersonalPuestoRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalPuestoDeTrabajoServiceImpl implements PersonalPuestoDeTrabajoService {

    private final Logger logger = LoggerFactory.getLogger(PersonalPuestoDeTrabajoServiceImpl.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final PersonalPuestoRepository personalPuestoRepository;
    private final PersonalSubpuestoDeTrabajoService personalSubpuestoDeTrabajoService;

    @Autowired
    public PersonalPuestoDeTrabajoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                              DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                              PersonalPuestoRepository personalPuestoRepository,
                                              PersonalSubpuestoDeTrabajoService personalSubpuestoDeTrabajoService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.personalPuestoRepository = personalPuestoRepository;
        this.personalSubpuestoDeTrabajoService = personalSubpuestoDeTrabajoService;
    }

    @Override
    public List<PersonalPuestoDeTrabajoDto> obtenerTodos() {
        logger.info("Consultando todos los oyestis de trabajo guardadas en la base de datos");
        List<PersonalPuesto> personalPuestos = personalPuestoRepository.getAllByEliminadoFalse();
        return personalPuestos.stream()
                .map(p -> {
                    PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto = daoToDtoConverter.convertDaoToDtoPersonalPuestoDeTrabajo(p);
                    personalPuestoDeTrabajoDto.setSubpuestos(personalSubpuestoDeTrabajoService.obtenerTodos(p.getUuid()));
                    return personalPuestoDeTrabajoDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PersonalPuestoDeTrabajoDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando el puesto de trabajo con el uuid [{}]", uuid);

        PersonalPuesto personalPuesto = personalPuestoRepository.getByUuidAndEliminadoFalse(uuid);

        if(personalPuesto == null) {
            logger.warn("El puesto de personal no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto = daoToDtoConverter.convertDaoToDtoPersonalPuestoDeTrabajo(personalPuesto);
        personalPuestoDeTrabajoDto.setSubpuestos(personalSubpuestoDeTrabajoService.obtenerTodos(personalPuesto.getUuid()));

        return personalPuestoDeTrabajoDto;
    }

    @Override
    public PersonalPuestoDeTrabajoDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando el puesto de trabajo  con el id [{}]", id);

        PersonalPuesto personalPuesto = personalPuestoRepository.getOne(id);

        if(personalPuesto == null) {
            logger.warn("El puesto del personal no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoPersonalPuestoDeTrabajo(personalPuesto);
    }

    @Override
    @Transactional
    public PersonalPuestoDeTrabajoDto crearNuevo(PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto, String username) {
        if(personalPuestoDeTrabajoDto == null || StringUtils.isBlank(username)) {
            logger.warn("El usuario o el puesto de trabajo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando puesto de trabajo con nombre: [{}]", personalPuestoDeTrabajoDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        PersonalPuesto personalPuesto = dtoToDaoConverter.convertDtoToDaoPuestoTrabajo(personalPuestoDeTrabajoDto);
        daoHelper.fulfillAuditorFields(true, personalPuesto, usuario.getId());
        PersonalPuesto personalPuestoCreado = personalPuestoRepository.save(personalPuesto);

        return daoToDtoConverter.convertDaoToDtoPersonalPuestoDeTrabajo(personalPuestoCreado);
    }

    @Override
    @Transactional
    public PersonalPuestoDeTrabajoDto modificar(String uuid, String username, PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || personalPuestoDeTrabajoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Modificando el puesto de trabajo con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        PersonalPuesto personalPuesto = personalPuestoRepository.getByUuidAndEliminadoFalse(uuid);

        if(personalPuesto == null) {
            logger.warn("El puesto de trabajo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personalPuesto.setNombre(personalPuestoDeTrabajoDto.getNombre());
        personalPuesto.setDescripcion(personalPuestoDeTrabajoDto.getDescripcion());
        personalPuesto.setFechaActualizacion(LocalDateTime.now());
        personalPuesto.setActualizadoPor(usuario.getId());

        personalPuestoRepository.save(personalPuesto);

        return daoToDtoConverter.convertDaoToDtoPersonalPuestoDeTrabajo(personalPuesto);
    }

    @Override
    @Transactional
    public PersonalPuestoDeTrabajoDto eliminar(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el puesto de trabajo con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        PersonalPuesto personalPuesto = personalPuestoRepository.getByUuidAndEliminadoFalse(uuid);

        if(personalPuesto == null) {
            logger.warn("El puesto de trabajo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personalPuesto.setEliminado(true);
        personalPuesto.setFechaActualizacion(LocalDateTime.now());
        personalPuesto.setActualizadoPor(usuario.getId());

        personalPuestoRepository.save(personalPuesto);

        return daoToDtoConverter.convertDaoToDtoPersonalPuestoDeTrabajo(personalPuesto);
    }
}
