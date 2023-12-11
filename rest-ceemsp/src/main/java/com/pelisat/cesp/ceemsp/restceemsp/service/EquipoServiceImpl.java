package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaFormaEjecucionRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.EquipoRepository;
import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
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
public class EquipoServiceImpl implements EquipoService {

    private final Logger logger = LoggerFactory.getLogger(EquipoService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final EquipoRepository equipoRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;

    @Autowired
    public EquipoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                             UsuarioService usuarioService, EquipoRepository equipoRepository, EmpresaRepository empresaRepository,
                             EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.equipoRepository = equipoRepository;
        this.empresaRepository = empresaRepository;
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
    }

    @Override
    public List<EquipoDto> obtenerEquipos() {
        logger.info("Consultando todos los equipos guardadas en la base de datos");
        List<Equipo> equipos = equipoRepository.findAllByEliminadoFalse();
        return equipos.stream()
                .map(equipo -> daoToDtoConverter.convertDaoToDtoEquipo(equipo))
                .collect(Collectors.toList());
    }

    @Override
    public List<EquipoDto> obtenerEquipos(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("Alguno de los parametros viene como invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaFormaEjecucion> empresaFormasEjecucion = empresaFormaEjecucionRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());
        List<Equipo> equipos = equipoRepository.findAllByFormaEjecucionAndEliminadoFalse(FormaEjecucionEnum.NA);

        empresaFormasEjecucion.forEach(efe -> {
            equipos.addAll(equipoRepository.findAllByFormaEjecucionAndEliminadoFalse(efe.getFormaEjecucion()));
        });

        return equipos.stream().map(daoToDtoConverter::convertDaoToDtoEquipo).collect(Collectors.toList());
    }

    @Override
    public EquipoDto obtenerEquipoPorUuid(String equipoUuid) {
        if(StringUtils.isBlank(equipoUuid)) {
            logger.warn("El uuid del equipo a consultar esta viniendo como vacio o nulo");
            throw new InvalidDataException();
        }

        logger.info("Consultando el equipo con el uuid [{}]", equipoUuid);

        Equipo equipo = equipoRepository.findByUuidAndEliminadoFalse(equipoUuid);

        if(equipo == null) {
            logger.warn("El equipo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEquipo(equipo);
    }

    @Override
    public EquipoDto obtenerEquipoPorId(int id) {
        if(id < 1) {
            logger.warn("El id a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Consultando el equipo con el id [{}]", id);
        Equipo equipo = equipoRepository.getOne(id);
        return daoToDtoConverter.convertDaoToDtoEquipo(equipo);
    }

    @Override
    @Transactional
    public EquipoDto guardarEquipo(EquipoDto equipoDto, String username) {
        if(equipoDto == null || StringUtils.isBlank(username)) {
            logger.warn("El equipo a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo equipo");

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        Equipo equipo = dtoToDaoConverter.convertDtoToDaoEquipo(equipoDto);
        daoHelper.fulfillAuditorFields(true, equipo, usuario.getId());

        Equipo equipoCreado = equipoRepository.save(equipo);

        return daoToDtoConverter.convertDaoToDtoEquipo(equipoCreado);
    }

    @Override
    @Transactional
    public EquipoDto modificarEquipo(String equipoUuid, String username, EquipoDto equipoDto) {
        if(StringUtils.isBlank(equipoUuid) || StringUtils.isBlank(username) || equipoDto == null) {
            logger.warn("Alguno de los campos vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el equipo con el uuid [{}]", equipoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        Equipo equipo = equipoRepository.findByUuidAndEliminadoFalse(equipoUuid);

        if(equipo == null) {
            logger.warn("El equipo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        equipo.setNombre(equipoDto.getNombre());
        equipo.setDescripcion(equipoDto.getDescripcion());
        equipo.setFormaEjecucion(equipoDto.getFormaEjecucion());
        equipo.setFechaActualizacion(LocalDateTime.now());
        equipo.setActualizadoPor(usuario.getId());

        equipoRepository.save(equipo);

        return daoToDtoConverter.convertDaoToDtoEquipo(equipo);
    }

    @Override
    @Transactional
    public EquipoDto eliminarEquipo(String equipoUuid, String username) {
        if(StringUtils.isBlank(equipoUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el equipo con el uuid [{}]", equipoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        Equipo equipo = equipoRepository.findByUuidAndEliminadoFalse(equipoUuid);

        if(equipo == null) {
            logger.warn("La modalidad no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        equipo.setEliminado(true);
        equipo.setFechaActualizacion(LocalDateTime.now());
        equipo.setActualizadoPor(usuario.getId());

        equipoRepository.save(equipo);

        return daoToDtoConverter.convertDaoToDtoEquipo(equipo);
    }
}
