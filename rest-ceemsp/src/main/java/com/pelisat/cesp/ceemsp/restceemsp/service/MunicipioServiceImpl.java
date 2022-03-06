package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.MunicipioDto;
import com.pelisat.cesp.ceemsp.database.model.Municipio;
import com.pelisat.cesp.ceemsp.database.repository.MunicipioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MunicipioServiceImpl implements MunicipioService {

    private final MunicipioRepository municipioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(MunicipioService.class);

    @Autowired
    public MunicipioServiceImpl(MunicipioRepository municipioRepository, DaoToDtoConverter daoToDtoConverter) {
        this.municipioRepository = municipioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
    }

    @Override
    public MunicipioDto obtenerMunicipioPorId(int id) {
        if(id < 1) {
            logger.warn("El id del municipio a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el municipio por id [{}]", id);
        Municipio municipio = municipioRepository.getOne(id);

        if(municipio == null || municipio.getEliminado()) {
            logger.warn("El municipio no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoMunicipio(municipio);
    }

    @Override
    public MunicipioDto obtenerMunicipioPorUuid(String uuid) {
        return null;
    }
}
