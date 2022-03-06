package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ColoniaDto;
import com.pelisat.cesp.ceemsp.database.dto.EstadoDto;
import com.pelisat.cesp.ceemsp.database.dto.LocalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.MunicipioDto;
import com.pelisat.cesp.ceemsp.database.model.Colonia;
import com.pelisat.cesp.ceemsp.database.model.Estado;
import com.pelisat.cesp.ceemsp.database.model.Localidad;
import com.pelisat.cesp.ceemsp.database.model.Municipio;
import com.pelisat.cesp.ceemsp.database.repository.ColoniaRepository;
import com.pelisat.cesp.ceemsp.database.repository.EstadoRepository;
import com.pelisat.cesp.ceemsp.database.repository.LocalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.MunicipioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoServiceImpl implements EstadoService {

    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final LocalidadRepository localidadRepository;
    private final ColoniaRepository coloniaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(EstadoService.class);

    @Autowired
    public EstadoServiceImpl(EstadoRepository estadoRepository, MunicipioRepository municipioRepository, DaoToDtoConverter daoToDtoConverter,
                             LocalidadRepository localidadRepository, ColoniaRepository coloniaRepository) {
        this.estadoRepository = estadoRepository;
        this.municipioRepository = municipioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.localidadRepository = localidadRepository;
        this.coloniaRepository = coloniaRepository;
    }

    @Override
    public List<EstadoDto> obtenerTodosLosEstados() {
        List<Estado> estados = estadoRepository.findAll();
        return estados.stream().map(daoToDtoConverter::convertDaoToDtoEstado).collect(Collectors.toList());
    }

    @Override
    public EstadoDto obtenerPorId(int estadoId) {
        if(estadoId < 1) {
            logger.warn("El id del estado a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el estado por id [{}]", estadoId);
        Estado estado = estadoRepository.getOne(estadoId);

        if(estado == null || estado.getEliminado()) {
            logger.warn("El estado no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoEstado(estado);
    }

    @Override
    public List<MunicipioDto> obtenerMunicipiosPorEstadoUuid(String uuidEstado) {
        if(StringUtils.isBlank(uuidEstado)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los municipios con el estado [{}]", uuidEstado);

        Estado estado = estadoRepository.getByUuidAndEliminadoFalse(uuidEstado);

        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Municipio> municipios = municipioRepository.getAllByEstadoAndEliminadoFalse(estado.getId());
        return municipios.stream().map(daoToDtoConverter::convertDaoToDtoMunicipio).collect(Collectors.toList());
    }

    @Override
    public List<LocalidadDto> obtenerLocalidadesPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid) {
        if(StringUtils.isBlank(uuidEstado) || StringUtils.isBlank(municipioUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las localidades con el estado [{}] y municipio [{}]", uuidEstado, municipioUuid);

        Estado estado = estadoRepository.getByUuidAndEliminadoFalse(uuidEstado);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Municipio municipio = municipioRepository.getByUuid(municipioUuid);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Localidad> localidades = localidadRepository.getAllByEstadoAndMunicipioAndEliminadoFalse(estado.getId(), municipio.getClave());
        return localidades.stream().map(daoToDtoConverter::convertDaoToDtoLocalidad).collect(Collectors.toList());
    }

    @Override
    public List<ColoniaDto> obtenerColoniasPorEstadoUuidYMunicipioUuid(String uuidEstado, String municipioUuid) {
        if(StringUtils.isBlank(uuidEstado) || StringUtils.isBlank(municipioUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las localidades con el estado [{}] y municipio [{}]", uuidEstado, municipioUuid);

        Estado estado = estadoRepository.getByUuidAndEliminadoFalse(uuidEstado);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Municipio municipio = municipioRepository.getByUuid(municipioUuid);
        if(estado == null) {
            logger.warn("El estado a consultar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Colonia> colonias = coloniaRepository.getAllByEstadoAndMunicipioAndEliminadoFalse(estado.getId(), municipio.getClave());
        return colonias.stream().map(daoToDtoConverter::convertDaoToDtoColonia).collect(Collectors.toList());
    }
}
