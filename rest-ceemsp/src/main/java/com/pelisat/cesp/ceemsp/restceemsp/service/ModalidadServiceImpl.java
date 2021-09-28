package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CanTipoAdiestramiento;
import com.pelisat.cesp.ceemsp.database.model.Modalidad;
import com.pelisat.cesp.ceemsp.database.repository.ModalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.SubmodalidadRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
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
public class ModalidadServiceImpl implements ModalidadService {
    private final Logger logger = LoggerFactory.getLogger(ModalidadService.class);
    private final ModalidadRepository modalidadRepository;
    private final SubmodalidadRepository submodalidadRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final UsuarioService usuarioService;

    @Autowired
    public ModalidadServiceImpl(ModalidadRepository modalidadRepository, SubmodalidadRepository submodalidadRepository,
                                DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                UsuarioService usuarioService) {
        this.modalidadRepository = modalidadRepository;
        this.submodalidadRepository = submodalidadRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<ModalidadDto> obtenerModalidades() {
        logger.info("Consultando todas las razas guardadas en la base de datos");
        List<Modalidad> modalidades = modalidadRepository.findAllByEliminadoFalse();
        return modalidades.stream()
                .map(modalidad -> daoToDtoConverter.convertDaoToDtoModalidad(modalidad))
                .collect(Collectors.toList());
    }

    @Override
    public ModalidadDto obtenerModalidadPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid a consultar esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Consultando la modalidad con el uuid [{}]", uuid);

        Modalidad modalidad = modalidadRepository.findByUuidAndEliminadoFalse(uuid);

        if(modalidad == null) {
            logger.warn("El tipo de adiestramiento no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoModalidad(modalidad);
    }

    @Override
    public ModalidadDto obtenerModalidadPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id a consultar esta viniendo como invalido o nulo");
            throw new InvalidDataException();
        }

        logger.info("Consultando la modalidad con el id [{}]", id);

        Modalidad modalidad = modalidadRepository.getOne(id);

        if(modalidad == null) {
            logger.warn("El tipo de adiestramiento no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoModalidad(modalidad);
    }

    @Override
    public ModalidadDto guardarModalidad(ModalidadDto modalidadDto, String username) {
        if(modalidadDto == null || StringUtils.isBlank(username)) {
            logger.warn("La modalidad a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nueva modalidad");

        UsuarioDto usuario = usuarioService.getUserByUsername(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        Modalidad modalidad = dtoToDaoConverter.convertDtoToDaoModalidad(modalidadDto);

        modalidad.setFechaCreacion(LocalDateTime.now());
        modalidad.setCreadoPor(usuario.getId());
        modalidad.setActualizadoPor(usuario.getId());
        modalidad.setFechaActualizacion(LocalDateTime.now());

        Modalidad modalidadCreada = modalidadRepository.save(modalidad);

        return daoToDtoConverter.convertDaoToDtoModalidad(modalidadCreada);
    }

    @Override
    public ModalidadDto modificarModalidad(ModalidadDto modalidadDto, String uuid, String username) {
        return null;
    }

    @Override
    public ModalidadDto eliminarModalidad(String uuid, String username) {
        return null;
    }
}
