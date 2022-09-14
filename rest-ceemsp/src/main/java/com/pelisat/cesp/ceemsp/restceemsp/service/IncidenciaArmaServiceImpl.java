package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenciaArmaServiceImpl implements IncidenciaArmaService {

    private final Logger logger = LoggerFactory.getLogger(IncidenciaPersonalService.class);
    private final IncidenciaArmaRepository incidenciaArmaRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final ArmaRepository armaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<IncidenciaArma> daoHelper;

    @Autowired
    public IncidenciaArmaServiceImpl(IncidenciaArmaRepository incidenciaArmaRepository, IncidenciaRepository incidenciaRepository,
                                         DaoToDtoConverter daoToDtoConverter, UsuarioService usuarioService, DaoHelper<IncidenciaArma> daoHelper,
                                         ArmaRepository armaRepository) {
        this.incidenciaArmaRepository = incidenciaArmaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.armaRepository = armaRepository;
    }

    @Override
    public List<ArmaDto> obtenerArmasIncidencia(String empresaUuid, String incidenciaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando arma a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<IncidenciaArma> incidenciaArmas = incidenciaArmaRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());

        return incidenciaArmas.stream().map(a -> {
            Arma arma = armaRepository.getOne(a.getArma());
            return daoToDtoConverter.convertDaoToDtoArma(arma);
        }).collect(Collectors.toList());
    }

    @Override
    public ArmaDto agregarArmaIncidencia(String empresaUuid, String incidenciaUuid, String username, ArmaDto armaDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || armaDto == null) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando arma a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        IncidenciaArma incidenciaArma = new IncidenciaArma();
        incidenciaArma.setIncidencia(incidencia.getId());
        incidenciaArma.setArma(armaDto.getId());
        daoHelper.fulfillAuditorFields(true, incidenciaArma, usuario.getId());

        incidenciaArmaRepository.save(incidenciaArma);

        return armaDto;
    }

    @Override
    public ArmaDto eliminarArmaIncidencia(String empresaUuid, String incidenciaUuid, String armaIncidenciaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(armaIncidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando arma de la incidencia con el uuid [{}]", armaIncidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Arma arma = armaRepository.getByUuidAndEliminadoFalse(armaIncidenciaUuid);

        if(arma == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        IncidenciaArma incidenciaArma = incidenciaArmaRepository.getByIncidenciaAndArmaAndEliminadoFalse(incidencia.getId(), arma.getId());

        if(incidenciaArma == null) {
            logger.warn("El arma en la incidencia no se encuentra registrada");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        incidenciaArma.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, incidenciaArma, usuario.getId());
        incidenciaArmaRepository.save(incidenciaArma);

        return null;
    }
}
