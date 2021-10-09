package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;
import com.pelisat.cesp.ceemsp.database.model.ArmaTipo;
import com.pelisat.cesp.ceemsp.database.model.VehiculoMarca;
import com.pelisat.cesp.ceemsp.database.repository.ArmaTipoRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoMarcaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculoMarcaServiceImpl implements VehiculoMarcaService {

    private final VehiculoMarcaRepository vehiculoMarcaRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final Logger logger = LoggerFactory.getLogger(ArmaClaseServiceImpl.class);

    @Autowired
    public VehiculoMarcaServiceImpl(VehiculoMarcaRepository vehiculoMarcaRepository, UsuarioService usuarioService,
                                    DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter) {
        this.vehiculoMarcaRepository = vehiculoMarcaRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<VehiculoMarcaDto> obtenerTodos() {
        logger.info("Consultando todas las marcas de vehiculos en la base de datos");
        List<VehiculoMarca> armaTipos = vehiculoMarcaRepository.getAllByEliminadoFalse();
        return armaTipos.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculoMarca)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoMarcaDto obtenerPorUuid(String uuid) {
        return null;
    }

    @Override
    public VehiculoMarcaDto obtenerPorId(Integer id) {
        return null;
    }

    @Override
    public VehiculoMarcaDto crearNuevo(VehiculoMarcaDto vehiculoMarcaDto, String username) {
        if(vehiculoMarcaDto == null || StringUtils.isBlank(username)) {
            logger.warn("La marca de vehiculo  o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nueva marca de vehiculo con nombre: [{}]", vehiculoMarcaDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        VehiculoMarca vehiculoMarca = dtoToDaoConverter.convertDtoToDaoVehiculoMarca(vehiculoMarcaDto);

        vehiculoMarca.setFechaCreacion(LocalDateTime.now());
        vehiculoMarca.setCreadoPor(usuario.getId());
        vehiculoMarca.setActualizadoPor(usuario.getId());
        vehiculoMarca.setFechaActualizacion(LocalDateTime.now());

        VehiculoMarca vehiculoMarcaCreado = vehiculoMarcaRepository.save(vehiculoMarca);

        return daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarcaCreado);
    }

    @Override
    public VehiculoMarcaDto modificar(VehiculoMarcaDto vehiculoMarcaDto, String uuid, String username) {
        return null;
    }

    @Override
    public VehiculoMarcaDto eliminar(String uuid, String username) {
        return null;
    }
}
