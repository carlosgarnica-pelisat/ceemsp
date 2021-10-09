package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.dto.*;
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

    public ArmaClase convertDtoToDaoArmaClase(ArmaClaseDto armaClaseDto) {
        if(armaClaseDto == null) {
            logger.warn("La clase del arma viene como vacio o nulo");
            throw new InvalidDataException();
        }

        ArmaClase armaClase = modelMapper.map(armaClaseDto, ArmaClase.class);
        if(StringUtils.isBlank(armaClase.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            armaClase.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return armaClase;
    }

    public ArmaMarca convertDtoToDaoArmaMarca(ArmaMarcaDto armaMarcaDto) {
        if(armaMarcaDto == null) {
            logger.warn("La marca del arma viene como vacio o nulo");
            throw new InvalidDataException();
        }

        ArmaMarca armaMarca = modelMapper.map(armaMarcaDto, ArmaMarca.class);
        if(StringUtils.isBlank(armaMarca.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            armaMarca.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return armaMarca;
    }

    public ArmaTipo convertDtoToDaoArmaTipo(ArmaTipoDto armaTipoDto) {
        if(armaTipoDto == null) {
            logger.warn("El tipo del arma viene como vacio o nulo");
            throw new InvalidDataException();
        }

        ArmaTipo armaTipo = modelMapper.map(armaTipoDto, ArmaTipo.class);
        if(StringUtils.isBlank(armaTipo.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            armaTipo.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return armaTipo;
    }

    public VehiculoMarca convertDtoToDaoVehiculoMarca(VehiculoMarcaDto vehiculoMarcaDto) {
        if(vehiculoMarcaDto == null) {
            logger.warn("La marca del vehiculo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        VehiculoMarca vehiculoMarca = modelMapper.map(vehiculoMarcaDto, VehiculoMarca.class);
        if(StringUtils.isBlank(vehiculoMarca.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoMarca.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoMarca;
    }

    public VehiculoSubmarca convertDtoToDaoVehiculoSubmarca(VehiculoSubmarcaDto vehiculoSubmarcaDto) {
        if(vehiculoSubmarcaDto == null) {
            logger.warn("La submarca del vehiculo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        VehiculoSubmarca vehiculoSubmarca = modelMapper.map(vehiculoSubmarcaDto, VehiculoSubmarca.class);
        if(StringUtils.isBlank(vehiculoSubmarca.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoSubmarca.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoSubmarca;
    }

    public VehiculoTipo convertDtoToDaoVehiculoTipo(VehiculoTipoDto vehiculoTipoDto) {
        if(vehiculoTipoDto == null) {
            logger.warn("El tipo de vehiculo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        VehiculoTipo vehiculoTipo = modelMapper.map(vehiculoTipoDto, VehiculoTipo.class);
        if(StringUtils.isBlank(vehiculoTipo.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoTipo.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoTipo;
    }

    public Empresa convertDtoToDaoEmpresa(EmpresaDto empresaDto) {
        if(empresaDto == null) {
            logger.warn("La empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        Empresa empresa = modelMapper.map(empresaDto, Empresa.class);
        if(StringUtils.isBlank(empresa.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresa.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresa;
    }



}