package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CalleDto;
import com.pelisat.cesp.ceemsp.database.model.Calle;
import com.pelisat.cesp.ceemsp.database.repository.CalleRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalleServiceImpl implements CalleService {

    private final CalleRepository calleRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(CalleService.class);

    @Autowired
    public CalleServiceImpl(CalleRepository calleRepository, DaoToDtoConverter daoToDtoConverter) {
        this.calleRepository = calleRepository;
        this.daoToDtoConverter = daoToDtoConverter;
    }

    @Override
    public List<CalleDto> obtenerTodasLasCalles(Integer limit) {
        logger.info("Obteniendo todas las calles guardadas en la base de datos.");
        List<Calle> calles = new ArrayList<>();

        if(limit == null) {
            calles = calleRepository.findAllByEliminadoFalse();
        } else {
            calles = calleRepository.findAllByEliminadoFalse(PageRequest.of(0, limit));
        }

        return calles.stream().map(daoToDtoConverter::convertDaoToDtoCalle).collect(Collectors.toList());
    }

    @Override
    public List<CalleDto> obtenerCallesPorQuery(String query) {
        if(StringUtils.isBlank(query)) {
            logger.warn("La query ingresada para la busqueda de calles viene como nula o vacia");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las calles con el query [{}]", query);
        List<Calle> calles = calleRepository.findAllByNombreContainsAndEliminadoFalse(query);

        return calles.stream().map(daoToDtoConverter::convertDaoToDtoCalle).collect(Collectors.toList());
    }

    @Override
    public CalleDto obtenerCallePorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id ingresado es invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la calle con el id [{}]", id);
        Calle calle = calleRepository.getOne(id);

        if(calle == null || calle.getEliminado()) {
            logger.warn("La calle no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCalle(calle);
    }
}
