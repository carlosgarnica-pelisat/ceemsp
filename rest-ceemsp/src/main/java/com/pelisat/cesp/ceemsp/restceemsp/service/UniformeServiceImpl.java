package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Uniforme;
import com.pelisat.cesp.ceemsp.database.repository.UniformeRepository;
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
public class UniformeServiceImpl implements UniformeService {

    private final Logger logger = LoggerFactory.getLogger(EquipoService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final UniformeRepository uniformeRepository;

    @Autowired
    public UniformeServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService, UniformeRepository uniformeRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.uniformeRepository = uniformeRepository;
    }

    @Override
    public List<UniformeDto> obtenerUniformes() {
        logger.info("Consultando todos los uniformes guardados en la base de datos");
        List<Uniforme> uniformes = uniformeRepository.findAllByEliminadoFalse();
        return uniformes.stream()
                .map(u -> daoToDtoConverter.convertDaoToDtoUniforme(u))
                .collect(Collectors.toList());
    }

    @Override
    public UniformeDto obtenerUniformePorUuid(String uniformeUuid) {
        if(StringUtils.isBlank(uniformeUuid)) {
            logger.warn("El uuid del uniforme a consultar esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Consultando el uniforme con el uuid [{}]", uniformeUuid);

        Uniforme uniforme = uniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);

        if(uniforme == null) {
            logger.warn("El uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoUniforme(uniforme);
    }

    @Override
    public UniformeDto obtenerUniformePorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultanmdo el uniforme con el id [{}]", id);

        Uniforme uniforme = uniformeRepository.getOne(id);

        return daoToDtoConverter.convertDaoToDtoUniforme(uniforme);
    }

    @Override
    @Transactional
    public UniformeDto guardarUniforme(UniformeDto uniformeDto, String username) {
        if(uniformeDto == null || StringUtils.isBlank(username)) {
            logger.warn("El uniforme a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo uniforme");

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        Uniforme uniforme = dtoToDaoConverter.convertDtoToDaoUniforme(uniformeDto);
        daoHelper.fulfillAuditorFields(true, uniforme, usuario.getId());

        Uniforme uniformeCreado = uniformeRepository.save(uniforme);

        return daoToDtoConverter.convertDaoToDtoUniforme(uniformeCreado);
    }

    @Override
    @Transactional
    public UniformeDto modificarUniforme(String uniformeUuid, String username, UniformeDto uniformeDto) {
        if(StringUtils.isBlank(uniformeUuid) || StringUtils.isBlank(username) || uniformeDto == null) {
            logger.warn("Alguno de los campos vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el uniforme con el uuid [{}]", uniformeUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        Uniforme uniforme = uniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);

        if(uniforme == null) {
            logger.warn("El uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        uniforme.setNombre(uniformeDto.getNombre());
        uniforme.setDescripcion(uniformeDto.getDescripcion());
        uniforme.setFechaActualizacion(LocalDateTime.now());
        uniforme.setActualizadoPor(usuario.getId());

        uniformeRepository.save(uniforme);

        return daoToDtoConverter.convertDaoToDtoUniforme(uniforme);
    }

    @Override
    @Transactional
    public UniformeDto eliminarUniforme(String uniformeUuid, String username) {
        if(StringUtils.isBlank(uniformeUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el uniforme con el uuid [{}]", uniformeUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        Uniforme uniforme = uniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);

        if(uniforme == null) {
            logger.warn("El uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        uniforme.setEliminado(true);
        uniforme.setFechaActualizacion(LocalDateTime.now());
        uniforme.setActualizadoPor(usuario.getId());

        uniformeRepository.save(uniforme);

        return daoToDtoConverter.convertDaoToDtoUniforme(uniforme);
    }
}
