package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoMarcaDto;
import com.pelisat.cesp.ceemsp.database.model.ArmaTipo;
import com.pelisat.cesp.ceemsp.database.model.VehiculoMarca;
import com.pelisat.cesp.ceemsp.database.model.VehiculoSubmarca;
import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import com.pelisat.cesp.ceemsp.database.repository.ArmaTipoRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoMarcaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoSubmarcaRepository;
import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
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
    private final VehiculoSubmarcaRepository vehiculoSubmarcaRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final Logger logger = LoggerFactory.getLogger(ArmaClaseServiceImpl.class);

    @Autowired
    public VehiculoMarcaServiceImpl(VehiculoMarcaRepository vehiculoMarcaRepository, UsuarioService usuarioService,
                                    DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                    VehiculoSubmarcaRepository vehiculoSubmarcaRepository) {
        this.vehiculoMarcaRepository = vehiculoMarcaRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.vehiculoSubmarcaRepository = vehiculoSubmarcaRepository;
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
    public List<VehiculoMarcaDto> obtenerMarcaTipo(VehiculoTipoEnum vehiculoTipoEnum) {
        logger.info("Consultando las marcas por el tipo [{}]", vehiculoTipoEnum);
        List<VehiculoMarca> vehiculoMarcas = vehiculoMarcaRepository.getAllByTipoAndEliminadoFalse(vehiculoTipoEnum);
        return vehiculoMarcas.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculoMarca)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculoMarcaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la marca del vehiculo a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        VehiculoMarca vehiculoMarca = vehiculoMarcaRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoMarca == null) {
            logger.warn("La marca del vehiculo con uuid [{}] viene como nula o vacia", uuid);
            throw new NotFoundResourceException();
        }

        List<VehiculoSubmarca> vehiculoSubmarcas = vehiculoSubmarcaRepository.getAllByMarcaAndEliminadoFalse(vehiculoMarca.getId());

        VehiculoMarcaDto vehiculoMarcaDto = daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarca);

        vehiculoMarcaDto.setSubmarcas(
                vehiculoSubmarcas.stream().map(daoToDtoConverter::convertDaoToDtoVehiculoSubmarca)
                        .collect(Collectors.toList())
        );

        return vehiculoMarcaDto;
    }

    @Override
    public VehiculoMarcaDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id viene como nulo o vacio");
            throw new InvalidDataException();
        }
        logger.info("Descargando la marca del vehiculo con el id [{}]", id);
        VehiculoMarca vehiculoMarca = vehiculoMarcaRepository.getOne(id);

        if(vehiculoMarca == null) {
            logger.warn("La marca del vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarca);
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
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || vehiculoMarcaDto == null) {
            logger.warn("Alguno de los campos vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando la marca del vehiculo con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        VehiculoMarca vehiculoMarca = vehiculoMarcaRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoMarca == null) {
            logger.warn("La marca no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        vehiculoMarca.setNombre(vehiculoMarcaDto.getNombre());
        vehiculoMarca.setDescripcion(vehiculoMarcaDto.getDescripcion());
        vehiculoMarca.setTipo(vehiculoMarcaDto.getTipo());
        vehiculoMarca.setFechaActualizacion(LocalDateTime.now());
        vehiculoMarca.setActualizadoPor(usuario.getId());

        vehiculoMarcaRepository.save(vehiculoMarca);

        return daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarca);
    }

    @Override
    public VehiculoMarcaDto eliminar(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la marca del vehiculo con el uuid [{}]", uuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        VehiculoMarca vehiculoMarca = vehiculoMarcaRepository.getByUuidAndEliminadoFalse(uuid);

        if(vehiculoMarca == null) {
            logger.warn("El tipo del vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        vehiculoMarca.setEliminado(true);
        vehiculoMarca.setFechaActualizacion(LocalDateTime.now());
        vehiculoMarca.setActualizadoPor(usuario.getId());

        vehiculoMarcaRepository.save(vehiculoMarca);

        return daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarca);
    }
}
