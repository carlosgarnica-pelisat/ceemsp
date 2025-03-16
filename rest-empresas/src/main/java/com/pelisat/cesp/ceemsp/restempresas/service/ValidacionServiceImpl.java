package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ExisteArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.ExistePersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.Arma;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidacionServiceImpl implements ValidacionService {

    private final Logger logger = LoggerFactory.getLogger(ValidacionService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final PersonaRepository personaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final ArmaRepository armaRepository;

    @Autowired
    public ValidacionServiceImpl(DaoToDtoConverter daoToDtoConverter, PersonaRepository personaRepository,
                                 VehiculoRepository vehiculoRepository, EmpresaEscrituraRepository empresaEscrituraRepository,
                                 ArmaRepository armaRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.personaRepository = personaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.armaRepository = armaRepository;
    }

    @Override
    public ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto) {
        if(existeVehiculoDto == null) {
            logger.warn("El objeto a consultar viene como nulo o vacio ");
            throw new InvalidDataException();
        }

        if(StringUtils.isBlank(existeVehiculoDto.getNumeroSerie()) && StringUtils.isBlank(existeVehiculoDto.getPlacas())) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Consultando la existencia del vehiculo");
        existeVehiculoDto.setExiste(false);

        if(StringUtils.isNotBlank(existeVehiculoDto.getPlacas())) {
            logger.info("Buscando con las placas [{}]", existeVehiculoDto.getPlacas());
            Vehiculo vehiculo = vehiculoRepository.getByPlacasAndEliminadoFalse(existeVehiculoDto.getPlacas());
            if(vehiculo != null) {
                logger.info("Se encontro el vehiculo con las placas");
                existeVehiculoDto.setExiste(true);
                existeVehiculoDto.setVehiculo(daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo));
            }
        } else if (StringUtils.isNotBlank(existeVehiculoDto.getNumeroSerie())) {
            logger.info("Buscando con el numero de serie [{}]", existeVehiculoDto.getNumeroSerie());
            Vehiculo vehiculo = vehiculoRepository.getBySerieAndEliminadoFalse(existeVehiculoDto.getNumeroSerie());
            if(vehiculo != null) {
                logger.info("Se encontro el vehiculo con el numero de serie");
                existeVehiculoDto.setExiste(true);
                existeVehiculoDto.setVehiculo(daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo));
            }
        }

        return existeVehiculoDto;
    }

    @Override
    public ExistePersonaDto buscarExistenciaPersona(ExistePersonaDto existePersonaDto) {
        if(existePersonaDto == null) {
            logger.warn("El objeto a analizar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        if(StringUtils.isBlank(existePersonaDto.getCurp()) && StringUtils.isBlank(existePersonaDto.getRfc())) {
            logger.warn("Los parametros de busqueda vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Consultando la existencia de la persona");
        existePersonaDto.setExiste(false);

        if(StringUtils.isNotBlank(existePersonaDto.getCurp())) {
            logger.info("Buscando la persona con el CURP [{}]", existePersonaDto.getCurp());
            Personal personal = personaRepository.getByCurpAndEliminadoFalse(existePersonaDto.getCurp());
            if(personal != null) {
                logger.info("Se encontro la persona por medio del CURP");
                existePersonaDto.setExiste(true);
                existePersonaDto.setPersona(daoToDtoConverter.convertDaoToDtoPersona(personal));
            }
        }

        if(StringUtils.isNotBlank(existePersonaDto.getRfc())) {
            logger.info("Buscando la persona con el RFC[{}]", existePersonaDto.getRfc());
            Personal personal = personaRepository.getByRfcAndEliminadoFalse(existePersonaDto.getRfc());
            if(personal != null) {
                logger.info("Se encontro la persona por medio del RFC");
                existePersonaDto.setExiste(true);
                existePersonaDto.setPersona(daoToDtoConverter.convertDaoToDtoPersona(personal));
            }
        }

        return existePersonaDto;
    }

    @Override
    public ExisteEscrituraDto buscarEscrituraDto(ExisteEscrituraDto existeEscrituraDto) {
        return null;
    }

    @Override
    public ExisteArmaDto buscarArma(ExisteArmaDto existeArmaDto) {
        if(existeArmaDto == null) {
            logger.warn("El objeto a realizar la consulta viene como nulo o vacio");
            throw new InvalidDataException();
        }

        if(StringUtils.isBlank(existeArmaDto.getMatricula()) && StringUtils.isBlank(existeArmaDto.getSerie())) {
            logger.warn("El parametro a realizar la busqueda viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Buscando la empresa registrada");
        existeArmaDto.setExiste(false);

        if(StringUtils.isNotBlank(existeArmaDto.getSerie())) {
            logger.info("Buscando el arma con la serie [{}]", existeArmaDto.getSerie());
            Arma arma = armaRepository.getFirstBySerieAndEliminadoFalse(existeArmaDto.getSerie());
            if(arma != null) {
                logger.info("El arma fue encontrada con el numero de serie");
                existeArmaDto.setExiste(true);
                existeArmaDto.setArma(daoToDtoConverter.convertDaoToDtoArma(arma));
            }
        }

        if(StringUtils.isNotBlank(existeArmaDto.getMatricula())) {
            logger.info("Buscando el arma con la Matricula [{}]", existeArmaDto.getMatricula());
            Arma arma = armaRepository.getFirstByMatricula(existeArmaDto.getMatricula());
            if(arma != null && !arma.getEliminado()) {
                logger.info("El arma se encuentra activa");
                existeArmaDto.setExiste(true);
                existeArmaDto.setArma(daoToDtoConverter.convertDaoToDtoArma(arma));
            }
            else if(arma != null && arma.getEliminado() && arma.getStatus() == ArmaStatusEnum.CUSTODIA) {
                logger.info("El arma se encuentra marcada como custodia.");
                existeArmaDto.setExiste(true);
                existeArmaDto.setArma(daoToDtoConverter.convertDaoToDtoArma(arma));
            }
            else if(arma != null && arma.getEliminado() && (StringUtils.equals(arma.getMotivoBaja(), "ROBO") || StringUtils.equals(arma.getMotivoBaja(), "INSERVIBLE") || StringUtils.equals(arma.getMotivoBaja(), "CUSTODIA") || StringUtils.containsIgnoreCase(arma.getObservacionesBaja(), "incidencia"))) {
                logger.info("El arma no se encuentra activa pero fue marcada como ROBO, INSERVIBLE O ASEGURAMIENTO");
                existeArmaDto.setExiste(true);
                existeArmaDto.setArma(daoToDtoConverter.convertDaoToDtoArma(arma));
            }
        }

        return existeArmaDto;
    }
}
