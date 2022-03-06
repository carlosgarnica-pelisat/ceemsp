package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.LocalidadDto;
import com.pelisat.cesp.ceemsp.database.model.Localidad;
import com.pelisat.cesp.ceemsp.database.model.Municipio;
import com.pelisat.cesp.ceemsp.database.repository.LocalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.MunicipioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalidadServiceImpl implements LocalidadService {

    private final LocalidadRepository localidadRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(MunicipioService.class);

    @Autowired
    public LocalidadServiceImpl(LocalidadRepository localidadRepository, DaoToDtoConverter daoToDtoConverter) {
        this.localidadRepository = localidadRepository;
        this.daoToDtoConverter = daoToDtoConverter;
    }

    @Override
    public LocalidadDto obtenerLocalidadPorId(int id) {
        if(id < 1) {
            logger.warn("El id de la localidad a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la localidad por id [{}]", id);
        Localidad localidad = localidadRepository.getOne(id);

        if(localidad == null || localidad.getEliminado()) {
            logger.warn("La localidad no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoLocalidad(localidad);
    }

    @Override
    public LocalidadDto obtenerLocalidadPorUuid(String uuid) {
        return null;
    }
}
