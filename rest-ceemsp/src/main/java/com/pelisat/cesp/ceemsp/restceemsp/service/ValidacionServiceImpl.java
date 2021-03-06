package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidacionServiceImpl implements ValidacionService {

    private final Logger logger = LoggerFactory.getLogger(ValidacionService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaRepository empresaRepository;
    private final PersonaRepository personaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;

    @Autowired
    public ValidacionServiceImpl(DaoToDtoConverter daoToDtoConverter, EmpresaRepository empresaRepository, PersonaRepository personaRepository,
                                 VehiculoRepository vehiculoRepository, EmpresaEscrituraRepository empresaEscrituraRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaRepository = empresaRepository;
        this.personaRepository = personaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
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

        // TODO: Agregar el RFC a la persona y hacer la busqueda
        return existePersonaDto;
    }

    @Override
    public ExisteEmpresaDto buscarExistenciaEmpresa(ExisteEmpresaDto existeEmpresaDto) {
        if(existeEmpresaDto == null) {
            logger.warn("El objeto a realizar la consulta viene como nulo o vacio");
            throw new InvalidDataException();
        }

        if(StringUtils.isBlank(existeEmpresaDto.getRfc()) && StringUtils.isBlank(existeEmpresaDto.getCurp())) {
            logger.warn("El parametro a realizar la busqueda viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Buscando la empresa registrada");
        existeEmpresaDto.setExiste(false);

        if(StringUtils.isNotBlank(existeEmpresaDto.getRfc())) {
            logger.info("Buscando la empresa con el RFC [{}]", existeEmpresaDto.getRfc());
            Empresa empresa = empresaRepository.getByRfcAndEliminadoFalse(existeEmpresaDto.getRfc());
            if(empresa != null) {
                logger.info("La empresa fue encontrada con el RFC");
                existeEmpresaDto.setExiste(true);
                existeEmpresaDto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresa));
            }
        }

        if(StringUtils.isNotBlank(existeEmpresaDto.getCurp())) {
            logger.info("Buscando la empresa con el CURP [{}]", existeEmpresaDto.getCurp());
            Empresa empresa = empresaRepository.getByCurpAndEliminadoFalse(existeEmpresaDto.getCurp());
            if(empresa != null) {
                logger.info("La empresa fue encontrada con el CURP");
                existeEmpresaDto.setExiste(true);
                existeEmpresaDto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresa));
            }
        }

        return existeEmpresaDto;
    }

    @Override
    public ExisteEscrituraDto buscarEscrituraDto(ExisteEscrituraDto existeEscrituraDto) {
        if(existeEscrituraDto == null) {
            logger.warn("El objeto a realizar la consulta viene como nulo o vacio");
            throw new InvalidDataException();
        }

        if(StringUtils.isBlank(existeEscrituraDto.getNumero())) {
            logger.warn("El parametro a realizar la busqueda viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Buscando la escritura registrada");
        existeEscrituraDto.setExiste(false);

        if(StringUtils.isNotBlank(existeEscrituraDto.getNumero())) {
            logger.info("Buscando la escritura con el numero [{}]", existeEscrituraDto.getNumero());
            List<EmpresaEscritura> empresaEscrituras = empresaEscrituraRepository.findAllByNumeroEscrituraLikeAndEliminadoFalse(existeEscrituraDto.getNumero());
            if(empresaEscrituras != null && empresaEscrituras.size() > 0) {
                logger.info("La escritura fue encontrada con el RFC");
                existeEscrituraDto.setExiste(true);
                existeEscrituraDto.setEscritura(daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscrituras.get(0)));
            }
        }

        return existeEscrituraDto;
    }
}
