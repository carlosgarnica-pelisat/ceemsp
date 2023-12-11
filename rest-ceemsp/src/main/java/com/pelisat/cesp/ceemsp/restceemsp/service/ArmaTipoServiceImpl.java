package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaTipoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.ArmaTipo;
import com.pelisat.cesp.ceemsp.database.repository.ArmaTipoRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArmaTipoServiceImpl implements ArmaTipoService {

    private final ArmaTipoRepository armaTipoRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final Logger logger = LoggerFactory.getLogger(ArmaClaseServiceImpl.class);

    @Autowired
    public ArmaTipoServiceImpl(ArmaTipoRepository armaTipoRepository, UsuarioService usuarioService,
                               DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter) {
        this.armaTipoRepository = armaTipoRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<ArmaTipoDto> obtenerTodos() {
        logger.info("Consultando todas las clases de armas guardadas en la base de datos");
        List<ArmaTipo> armaTipos = armaTipoRepository.getAllByEliminadoFalse();
        return armaTipos.stream()
                .map(armaTipo -> daoToDtoConverter.convertDaoToDtoArmaTipo(armaTipo))
                .collect(Collectors.toList());
    }

    @Override
    public ArmaTipoDto obtenerPorUuid(String uuid) {
        return null;
    }

    @Override
    public ArmaTipoDto obtenerPorId(Integer id) {
        return null;
    }

    @Override
    @Transactional
    public ArmaTipoDto crearNuevo(ArmaTipoDto armaTipoDto, String username) {
        if(armaTipoDto == null || StringUtils.isBlank(username)) {
            logger.warn("El tipo de arma a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando nuevo tipo de arma con nombre: [{}]", armaTipoDto.getNombre());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        ArmaTipo armaTipo = dtoToDaoConverter.convertDtoToDaoArmaTipo(armaTipoDto);

        armaTipo.setFechaCreacion(LocalDateTime.now());
        armaTipo.setCreadoPor(usuario.getId());
        armaTipo.setActualizadoPor(usuario.getId());
        armaTipo.setFechaActualizacion(LocalDateTime.now());

        ArmaTipo armaTipoCreada = armaTipoRepository.save(armaTipo);

        return daoToDtoConverter.convertDaoToDtoArmaTipo(armaTipoCreada);
    }

    @Override
    public ArmaTipoDto modificar(ArmaTipoDto armaTipoDto, String uuid, String username) {
        return null;
    }

    @Override
    public ArmaTipoDto eliminar(String uuid, String username) {
        return null;
    }
}
