package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonalNacionalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.PersonalNacionalidad;
import com.pelisat.cesp.ceemsp.database.model.PersonalPuesto;
import com.pelisat.cesp.ceemsp.database.model.Uniforme;
import com.pelisat.cesp.ceemsp.database.repository.PersonalNacionalidadRepository;
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
public class PersonalNacionalidadServiceImpl implements PersonalNacionalidadService {
    private final Logger logger = LoggerFactory.getLogger(PersonalPuestoDeTrabajoServiceImpl.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final PersonalNacionalidadRepository personalNacionalidadRepository;

    @Autowired
    public PersonalNacionalidadServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                           DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                           PersonalNacionalidadRepository personalNacionalidadRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.personalNacionalidadRepository = personalNacionalidadRepository;
    }

    @Override
    public List<PersonalNacionalidadDto> obtenerTodos() {
        logger.info("Consultando todas las nacionalidades guardadas en la base de datos");
        List<PersonalNacionalidad> personalNacionalidades = personalNacionalidadRepository.getAllByEliminadoFalseOrderByNombre();
        return personalNacionalidades.stream()
                .map(daoToDtoConverter::convertDaoToDtoPersonalNacionalidad)
                .collect(Collectors.toList());
    }

    @Override
    public PersonalNacionalidadDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando la nacionalidad con el uuid [{}]", uuid);

        PersonalNacionalidad personalNacionalidad = personalNacionalidadRepository.getByUuidAndEliminadoFalse(uuid);

        if(personalNacionalidad == null) {
            logger.warn("El puesto de personal no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoPersonalNacionalidad(personalNacionalidad);
    }

    @Override
    public PersonalNacionalidadDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando la nacionalidad con el id [{}]", id);

        PersonalNacionalidad personalNacionalidad = personalNacionalidadRepository.getOne(id);

        if(personalNacionalidad == null) {
            logger.warn("La nacionalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoPersonalNacionalidad(personalNacionalidad);
    }

    @Override
    @Transactional
    public PersonalNacionalidadDto crearNuevo(PersonalNacionalidadDto personalNacionalidadDto, String username) {
        if(personalNacionalidadDto == null || StringUtils.isBlank(username)) {
            logger.warn("El usuario o la nacionalidad vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nacionalidad con nombre: [{}]", personalNacionalidadDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        PersonalNacionalidad personalNacionalidad = dtoToDaoConverter.convertDtoToDaoNacionalidad(personalNacionalidadDto);
        daoHelper.fulfillAuditorFields(true, personalNacionalidad, usuario.getId());
        PersonalNacionalidad personalNacionalidadCreada = personalNacionalidadRepository.save(personalNacionalidad);

        return daoToDtoConverter.convertDaoToDtoPersonalNacionalidad(personalNacionalidadCreada);
    }

    @Override
    @Transactional
    public PersonalNacionalidadDto modificarNacionalidad(String uuid, String username, PersonalNacionalidadDto personalNacionalidadDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || personalNacionalidadDto == null) {
            logger.warn("Alguno de los campos vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando la nacionalidad con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        PersonalNacionalidad nacionalidad = personalNacionalidadRepository.getByUuidAndEliminadoFalse(uuid);

        if(nacionalidad == null) {
            logger.warn("La nacionalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        nacionalidad.setNombre(personalNacionalidadDto.getNombre());
        nacionalidad.setDescripcion(personalNacionalidadDto.getDescripcion());
        nacionalidad.setFechaActualizacion(LocalDateTime.now());
        nacionalidad.setActualizadoPor(usuario.getId());

        personalNacionalidadRepository.save(nacionalidad);

        return daoToDtoConverter.convertDaoToDtoPersonalNacionalidad(nacionalidad);
    }

    @Override
    @Transactional
    public PersonalNacionalidadDto eliminarNacionalidad(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la nacionalidad con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        PersonalNacionalidad nacionalidad = personalNacionalidadRepository.getByUuidAndEliminadoFalse(uuid);

        if(nacionalidad == null) {
            logger.warn("La nacionalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        nacionalidad.setEliminado(true);
        nacionalidad.setFechaActualizacion(LocalDateTime.now());
        nacionalidad.setActualizadoPor(usuario.getId());

        personalNacionalidadRepository.save(nacionalidad);

        return daoToDtoConverter.convertDaoToDtoPersonalNacionalidad(nacionalidad);
    }
}
