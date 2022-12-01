package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.Incidencia;
import com.pelisat.cesp.ceemsp.database.model.IncidenciaVehiculo;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaRepository;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaVehiculoRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaIncidenciaVehiculoServiceImpl implements EmpresaIncidenciaVehiculoService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaIncidenciaVehiculoService.class);
    private final IncidenciaVehiculoRepository incidenciaVehiculoRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<IncidenciaVehiculo> daoHelper;

    @Autowired
    public EmpresaIncidenciaVehiculoServiceImpl(
            IncidenciaVehiculoRepository incidenciaVehiculoRepository, IncidenciaRepository incidenciaRepository,
            VehiculoRepository vehiculoRepository, DaoToDtoConverter daoToDtoConverter,
            UsuarioService usuarioService, DaoHelper<IncidenciaVehiculo> daoHelper) {
        this.incidenciaVehiculoRepository = incidenciaVehiculoRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
    }

    @Override
    public VehiculoDto agregarVehiculoIncidencia(String incidenciaUuid, String username, VehiculoDto vehiculoDto) {
        if(StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || vehiculoDto == null) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Agregando vehiculo a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        IncidenciaVehiculo incidenciaVehiculo = new IncidenciaVehiculo();
        incidenciaVehiculo.setIncidencia(incidencia.getId());
        incidenciaVehiculo.setVehiculo(vehiculoDto.getId());
        daoHelper.fulfillAuditorFields(true, incidenciaVehiculo, usuario.getId());

        incidenciaVehiculoRepository.save(incidenciaVehiculo);

        return vehiculoDto;
    }

    @Override
    public VehiculoDto eliminarVehiculoIncidencia(String incidenciaUuid, String vehiculoIncidenciaUuid, String username) {
        if(StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(vehiculoIncidenciaUuid)) {
            logger.warn("Alguno de los parametros es nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando vehiculo de la incidencia con el uuid [{}]", vehiculoIncidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(vehiculoIncidenciaUuid);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        IncidenciaVehiculo incidenciaVehiculo = incidenciaVehiculoRepository.getByIncidenciaAndVehiculoAndEliminadoFalse(vehiculo.getId(), incidencia.getId());

        if(incidenciaVehiculo == null) {
            logger.warn("El vehiculo en la incidencia no se encuentra registrada");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        incidenciaVehiculo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, incidenciaVehiculo, usuario.getId());
        incidenciaVehiculoRepository.save(incidenciaVehiculo);

        return null;
    }
}