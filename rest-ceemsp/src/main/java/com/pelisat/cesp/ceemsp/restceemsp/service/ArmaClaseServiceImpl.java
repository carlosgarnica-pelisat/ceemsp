package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaClaseDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.ArmaClase;
import com.pelisat.cesp.ceemsp.database.model.ArmaMarca;
import com.pelisat.cesp.ceemsp.database.model.CanRaza;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.ArmaClaseRepository;
import com.pelisat.cesp.ceemsp.database.repository.CanRazaRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArmaClaseServiceImpl implements ArmaClaseService {

    private final ArmaClaseRepository armaClaseRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final Logger logger = LoggerFactory.getLogger(ArmaClaseServiceImpl.class);
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public ArmaClaseServiceImpl(ArmaClaseRepository armaClaseRepository, UsuarioService usuarioService,
                                DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                DaoHelper<CommonModel> daoHelper) {
        this.armaClaseRepository = armaClaseRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<ArmaClaseDto> obtenerTodos() {
        logger.info("Consultando todas las clases de armas guardadas en la base de datos");
        List<ArmaClase> armaClases = armaClaseRepository.getAllByEliminadoFalse();
        return armaClases.stream()
                .map(armaClase -> daoToDtoConverter.convertDaoToDtoArmaClase(armaClase))
                .collect(Collectors.toList());
    }

    @Override
    public ArmaClaseDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando arma clase con el uuid [{}]", uuid);

        ArmaClase armaClase = armaClaseRepository.getByUuidAndEliminadoFalse(uuid);

        if(armaClase == null) {
            logger.warn("La arma clase no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoArmaClase(armaClase);
    }

    @Override
    public ArmaClaseDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id esta viniendo como nulo o menor a 1");
            throw new InvalidDataException();
        }

        logger.info("Consultando la clase del arma con el id [{}]", id);

        ArmaClase armaClase = armaClaseRepository.getOne(id);

        if(armaClase == null) {
            logger.warn("El armna clase no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoArmaClase(armaClase);
    }

    @Override
    public ArmaClaseDto crearNuevo(ArmaClaseDto armaClaseDto, String username) {
        if(armaClaseDto == null || StringUtils.isBlank(username)) {
            logger.warn("La clase del arma o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nueva clase de arma con nombre: [{}]", armaClaseDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        ArmaClase armaClase = dtoToDaoConverter.convertDtoToDaoArmaClase(armaClaseDto);
        daoHelper.fulfillAuditorFields(true, armaClase, usuario.getId());
        ArmaClase armaClaseCreada = armaClaseRepository.save(armaClase);

        return daoToDtoConverter.convertDaoToDtoArmaClase(armaClaseCreada);
    }

    @Override
    public ArmaClaseDto modificar(ArmaClaseDto armaClaseDto, String uuid, String username) {
        return null;
    }

    @Override
    public ArmaClaseDto eliminar(String uuid, String username) {
        return null;
    }
}
