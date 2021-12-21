package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaMarcaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.ArmaClase;
import com.pelisat.cesp.ceemsp.database.model.ArmaMarca;
import com.pelisat.cesp.ceemsp.database.model.ArmaTipo;
import com.pelisat.cesp.ceemsp.database.repository.ArmaClaseRepository;
import com.pelisat.cesp.ceemsp.database.repository.ArmaMarcaRepository;
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
public class ArmaMarcaServiceImpl implements ArmaMarcaService {

    private final ArmaMarcaRepository armaMarcaRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final Logger logger = LoggerFactory.getLogger(ArmaClaseServiceImpl.class);

    @Autowired
    public ArmaMarcaServiceImpl(ArmaMarcaRepository armaMarcaRepository, UsuarioService usuarioService,
                                DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter) {
        this.armaMarcaRepository = armaMarcaRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<ArmaMarcaDto> obtenerTodos() {
        logger.info("Consultando todas las clases de armas guardadas en la base de datos");
        List<ArmaMarca> armaMarcas = armaMarcaRepository.getAllByEliminadoFalse();
        return armaMarcas.stream()
                .map(armaMarca -> daoToDtoConverter.convertDaoToDtoArmaMarca(armaMarca))
                .collect(Collectors.toList());
    }

    @Override
    public ArmaMarcaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid esta viniendo como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando arma clase con el uuid [{}]", uuid);

        ArmaMarca armaMarca = armaMarcaRepository.getByUuidAndEliminadoFalse(uuid);

        if(armaMarca == null) {
            logger.warn("La arma clase no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoArmaMarca(armaMarca);
    }

    @Override
    public ArmaMarcaDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la marca del arma con el id [{}]", id);

        ArmaMarca armaMarca = armaMarcaRepository.getOne(id);
        return daoToDtoConverter.convertDaoToDtoArmaMarca(armaMarca);
    }

    @Override
    public ArmaMarcaDto crearNuevo(ArmaMarcaDto armaMarcaDto, String username) {
        if(armaMarcaDto == null || StringUtils.isBlank(username)) {
            logger.warn("La marca del arma o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nueva marca de arma con nombre: [{}]", armaMarcaDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        ArmaMarca armaMarca = dtoToDaoConverter.convertDtoToDaoArmaMarca(armaMarcaDto);

        armaMarca.setFechaCreacion(LocalDateTime.now());
        armaMarca.setCreadoPor(usuario.getId());
        armaMarca.setActualizadoPor(usuario.getId());
        armaMarca.setFechaActualizacion(LocalDateTime.now());

        ArmaMarca armaMarcaCreada = armaMarcaRepository.save(armaMarca);

        return daoToDtoConverter.convertDaoToDtoArmaMarca(armaMarcaCreada);
    }

    @Override
    public ArmaMarcaDto modificar(ArmaMarcaDto armaMarcaDto, String uuid, String username) {
        return null;
    }

    @Override
    public ArmaMarcaDto eliminar(String uuid, String username) {
        return null;
    }
}
