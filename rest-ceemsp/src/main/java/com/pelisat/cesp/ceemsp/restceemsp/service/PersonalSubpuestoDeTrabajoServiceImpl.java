package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalPuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonalSubpuestoDeTrabajoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.PersonalPuesto;
import com.pelisat.cesp.ceemsp.database.model.PersonalSubpuesto;
import com.pelisat.cesp.ceemsp.database.repository.PersonalPuestoRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonalSubpuestoRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalSubpuestoDeTrabajoServiceImpl implements PersonalSubpuestoDeTrabajoService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final PersonalSubpuestoRepository personalSubpuestoRepository;
    private final PersonalPuestoRepository personalPuestoRepository;
    private final Logger logger = LoggerFactory.getLogger(PersonalSubpuestoDeTrabajoServiceImpl.class);

    @Autowired
    public PersonalSubpuestoDeTrabajoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                                 DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                                 PersonalSubpuestoRepository personalSubpuestoRepository,
                                                 PersonalPuestoRepository personalPuestoRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.personalSubpuestoRepository = personalSubpuestoRepository;
        this.personalPuestoRepository = personalPuestoRepository;
    }

    @Override
    public List<PersonalSubpuestoDeTrabajoDto> obtenerTodos(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        PersonalPuesto personalPuesto = personalPuestoRepository.getByUuidAndEliminadoFalse(uuid);
        if(personalPuesto == null) {
            logger.warn("No se encontro el puesto de trabajo en la base de datos");
            throw new NotFoundResourceException();
        }

        logger.info("Consultando todos los subpuestos de trabajo guardadas en la base de datos");
        List<PersonalSubpuesto> personalSubpuestos = personalSubpuestoRepository.getAllByPuestoAndEliminadoFalse(personalPuesto.getId());
        return personalSubpuestos.stream()
                .map(daoToDtoConverter::convertDaoToDtoPersonalSubpuestoDeTrabajo)
                .collect(Collectors.toList());
    }

    @Override
    public PersonalSubpuestoDeTrabajoDto obtenerPorUuid(String uuid, String subpuestoUuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando el subpuesto de trabajo con el uuid [{}]", uuid);

        PersonalSubpuesto personalSubpuesto = personalSubpuestoRepository.getByUuidAndEliminadoFalse(uuid);

        if(personalSubpuesto == null) {
            logger.warn("El subpuesto de personal no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoPersonalSubpuestoDeTrabajo(personalSubpuesto);
    }

    @Override
    public PersonalSubpuestoDeTrabajoDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando el subpuesto de trabajo con el id [{}]", id);

        PersonalSubpuesto personalSubpuesto = personalSubpuestoRepository.getOne(id);

        if(personalSubpuesto == null) {
            logger.warn("El subpuesto del personal no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoPersonalSubpuestoDeTrabajo(personalSubpuesto);
    }

    @Override
    public PersonalSubpuestoDeTrabajoDto crearNuevo(PersonalSubpuestoDeTrabajoDto personalSubpuestoDeTrabajoDto, String username, String uuid) {
        if(personalSubpuestoDeTrabajoDto == null || StringUtils.isBlank(username)) {
            logger.warn("El usuario o el subpuesto de trabajo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando subpuesto de trabajo con nombre: [{}]", personalSubpuestoDeTrabajoDto.getNombre());

        PersonalPuesto personalPuesto = personalPuestoRepository.getByUuidAndEliminadoFalse(uuid);
        if(personalPuesto == null) {
            logger.warn("No se encontro el puesto de trabajo en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        PersonalSubpuesto personalSubpuesto = dtoToDaoConverter.convertDtoToDaoSubpuestoTrabajo(personalSubpuestoDeTrabajoDto);
        daoHelper.fulfillAuditorFields(true, personalSubpuesto, usuario.getId());
        personalSubpuesto.setPuesto(personalPuesto.getId());
        PersonalSubpuesto personalSubpuestoCreado = personalSubpuestoRepository.save(personalSubpuesto);

        return daoToDtoConverter.convertDaoToDtoPersonalSubpuestoDeTrabajo(personalSubpuestoCreado);
    }
}
