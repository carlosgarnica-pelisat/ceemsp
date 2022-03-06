package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ColoniaDto;
import com.pelisat.cesp.ceemsp.database.model.Colonia;
import com.pelisat.cesp.ceemsp.database.model.Municipio;
import com.pelisat.cesp.ceemsp.database.repository.ColoniaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColoniaServiceImpl implements ColoniaService {

    private final ColoniaRepository coloniaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(ColoniaService.class);

    @Autowired
    public ColoniaServiceImpl(ColoniaRepository coloniaRepository, DaoToDtoConverter daoToDtoConverter) {
        this.coloniaRepository = coloniaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
    }

    @Override
    public ColoniaDto obtenerColoniaPorId(int id) {
        if(id < 1) {
            logger.warn("El id de la colonia a consultar es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la colonia por id [{}]", id);
        Colonia colonia = coloniaRepository.getOne(id);

        if(colonia == null || colonia.getEliminado()) {
            logger.warn("La colonia no existe en la base de datos");
            throw new InvalidDataException();
        }

        return daoToDtoConverter.convertDaoToDtoColonia(colonia);
    }

    @Override
    public ColoniaDto obtenerColoniaPorUuid(String uuid) {
        return null;
    }
}
