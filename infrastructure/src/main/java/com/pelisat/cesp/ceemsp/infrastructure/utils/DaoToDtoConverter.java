package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.dto.CanRazaDto;
import com.pelisat.cesp.ceemsp.database.dto.CanTipoAdiestramientoDto;
import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CanRaza;
import com.pelisat.cesp.ceemsp.database.model.CanTipoAdiestramiento;
import com.pelisat.cesp.ceemsp.database.model.Modalidad;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DaoToDtoConverter {
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(DaoToDtoConverter.class);

    @Autowired
    public DaoToDtoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void setDaoToDtoConverterSetup() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper.getConfiguration().setAmbiguityIgnored(true);
    }

    public UsuarioDto convertDaoToDtoUser(Usuario usuario) {
        if(usuario == null) {
            logger.warn("The user dao is coming as null");
            throw new InvalidDataException();
        }

        return modelMapper.map(usuario, UsuarioDto.class);
    }

    public CanRazaDto convertDaoToDtoCanRaza(CanRaza canRaza) {
        if(canRaza == null) {
            logger.warn("La raza a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(canRaza, CanRazaDto.class);
    }

    public CanTipoAdiestramientoDto convertDaoToDtoCanTipoAdiestramiento(CanTipoAdiestramiento canTipoAdiestramiento) {
        if(canTipoAdiestramiento == null) {
            logger.warn("El tipo de adiestramiento a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(canTipoAdiestramiento, CanTipoAdiestramientoDto.class);
    }

    public ModalidadDto convertDaoToDtoModalidad(Modalidad modalidad) {
        if(modalidad == null) {
            logger.warn("La modalidad a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(modalidad, ModalidadDto.class);
    }


}
