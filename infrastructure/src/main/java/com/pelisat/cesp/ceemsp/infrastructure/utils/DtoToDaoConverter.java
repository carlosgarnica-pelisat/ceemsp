package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.dto.CanRazaDto;
import com.pelisat.cesp.ceemsp.database.dto.CanTipoAdiestramientoDto;
import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DtoToDaoConverter {
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(DtoToDaoConverter.class);

    private static final int MAXIMUM_UUID_CHARS = 12;

    @Autowired
    public DtoToDaoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Usuario convertDtoToDaoUser(Usuario userDto) {
        if(userDto == null) {
            logger.warn("The userDto to be converted is coming as null");
            throw new InvalidDataException();
        }

        Usuario usuario = modelMapper.map(userDto, Usuario.class);
        if(StringUtils.isBlank(usuario.getUuid())) {
            logger.info("Uuid is coming as null. Generating a new one");
            usuario.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return usuario;
    }

    public CanRaza convertDtoToDaoCanRaza(CanRazaDto canRazaDto) {
        if(canRazaDto == null) {
            logger.warn("La raza viene como vacia o invalida");
            throw new InvalidDataException();
        }

        CanRaza canRaza = modelMapper.map(canRazaDto, CanRaza.class);
        if(StringUtils.isBlank(canRaza.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            canRaza.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return canRaza;
    }

    public CanTipoAdiestramiento convertDtoToDaoCanTipoAdiestramiento(CanTipoAdiestramientoDto canTipoAdiestramientoDto) {
        if(canTipoAdiestramientoDto == null) {
            logger.warn("El tipo de adiestramiento viene como vacio o nulo");
            throw new InvalidDataException();
        }

        CanTipoAdiestramiento canTipoAdiestramiento = modelMapper.map(canTipoAdiestramientoDto, CanTipoAdiestramiento.class);
        if(StringUtils.isBlank(canTipoAdiestramiento.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            canTipoAdiestramiento.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return canTipoAdiestramiento;
    }

    public Modalidad convertDtoToDaoModalidad(ModalidadDto modalidadDto) {
        if(modalidadDto == null) {
            logger.warn("La modalidad viene como vacia o nula");
            throw new InvalidDataException();
        }

        Modalidad modalidad = modelMapper.map(modalidadDto, Modalidad.class);
        if(StringUtils.isBlank(modalidad.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            modalidad.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return modalidad;
    }


}
