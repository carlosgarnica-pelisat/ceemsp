package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VehiculoSubmarcaDto;
import com.pelisat.cesp.ceemsp.database.model.VehiculoMarca;
import com.pelisat.cesp.ceemsp.database.model.VehiculoSubmarca;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoMarcaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoSubmarcaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
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
