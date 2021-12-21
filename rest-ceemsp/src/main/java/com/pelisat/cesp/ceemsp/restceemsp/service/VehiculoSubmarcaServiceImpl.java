package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoSubmarcaDto;
import com.pelisat.cesp.ceemsp.database.model.VehiculoMarca;
import com.pelisat.cesp.ceemsp.database.model.VehiculoSubmarca;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoMarcaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoSubmarcaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.NotBoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculoSubmarcaServiceImpl implements VehiculoSubmarcaService {

    private final Logger logger = LoggerFactory.getLogger(VehiculoSubmarcaServiceImpl.class);
    private final VehiculoSubmarcaRepository vehiculoSubmarcaRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;

    @Autowired
    public VehiculoSubmarcaServiceImpl(VehiculoSubmarcaRepository vehiculoSubmarcaRepository, UsuarioService usuarioService,
                                       DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter) {
        this.vehiculoSubmarcaRepository = vehiculoSubmarcaRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<VehiculoSubmarcaDto> obtenerTodos() {
        logger.info("Consultando todas las submarcas de vehiculos en la base de datos");
        List<VehiculoSubmarca> vehiculoSubmarcas = vehiculoSubmarcaRepository.getAllByEliminadoFalse();
        return vehiculoSubmarcas.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculoSubmarca)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoSubmarcaDto obtenerPorUuid(String uuid) {
        return null;
    }

    @Override
    public VehiculoSubmarcaDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id de la submarca de vehiculo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la submarca con el id [{}]", id);
        VehiculoSubmarca vehiculoSubmarca = vehiculoSubmarcaRepository.getOne(id);

        if(vehiculoSubmarca == null) {
            logger.warn("La submarca del vehiculo viene como nula o vascia");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoSubmarca(vehiculoSubmarca);
    }

    @Override
    public VehiculoSubmarcaDto crearNuevo(VehiculoSubmarcaDto vehiculoSubmarcaDto, String username) {
        return null;
    }

    @Override
    public VehiculoSubmarcaDto modificar(VehiculoSubmarcaDto vehiculoSubmarcaDto, String uuid, String username) {
        return null;
    }

    @Override
    public VehiculoSubmarcaDto eliminar(String uuid, String username) {
        return null;
    }
}
