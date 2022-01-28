package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Equipo;
import com.pelisat.cesp.ceemsp.database.model.Modalidad;
import com.pelisat.cesp.ceemsp.database.model.Uniforme;
import com.pelisat.cesp.ceemsp.database.repository.EquipoRepository;
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

    @Autowired
    public EquipoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                             UsuarioService usuarioService, EquipoRepository equipoRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.equipoRepository = equipoRepository;
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
    public EquipoDto modificarEquipo(String equipoUuid, String username, EquipoDto equipoDto) {
        return null;
    }

    @Override
    public EquipoDto eliminarEquipo(String equipoUuid, String username) {
        return null;
    }
}
