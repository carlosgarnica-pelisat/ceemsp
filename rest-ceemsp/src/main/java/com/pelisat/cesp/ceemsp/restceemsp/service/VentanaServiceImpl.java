package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VentanaDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Ventana;
import com.pelisat.cesp.ceemsp.database.repository.VentanaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.AlreadyActiveException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.WindowAlreadyOpenException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentanaServiceImpl implements VentanaService {

    private final Logger logger = LoggerFactory.getLogger(VentanaService.class);
    private final VentanaRepository ventanaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;

    @Autowired
    public VentanaServiceImpl(VentanaRepository ventanaRepository, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                              UsuarioService usuarioService) {
        this.ventanaRepository = ventanaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<VentanaDto> obtenerVentanas() {
        return ventanaRepository.getAllByEliminadoFalse()
                .stream()
                .map(daoToDtoConverter::convertDaoToDtoVentana)
                .collect(Collectors.toList());
    }

    @Override
    public VentanaDto obtenerVentanaPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Ventana ventana = ventanaRepository.getByUuidAndEliminadoFalse(uuid);

        if(ventana == null) {
            logger.warn("La ventana no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoVentana(ventana);
    }

    @Override
    public VentanaDto guardarVentana(VentanaDto ventanaDto, String username) {
        if(ventanaDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Guardando una nueva ventana");
        logger.info("Verificando si actualmente hay ventanas abiertas");

        Ventana hayVentanaRegistrada = ventanaRepository.getByFechaFinGreaterThanEqualAndEliminadoFalse(LocalDate.now());

        if(hayVentanaRegistrada != null) {
            logger.warn("Ya hay una ventana abierta");
            throw new WindowAlreadyOpenException();
        }

        Ventana ventana = dtoToDaoConverter.convertDtoToDaoVentana(ventanaDto);
        ventana.setFechaInicio(LocalDate.parse(ventanaDto.getFechaInicio()));
        ventana.setFechaFin(LocalDate.parse(ventanaDto.getFechaFin()));
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        daoHelper.fulfillAuditorFields(true, ventana, usuarioDto.getId());

        Ventana ventanaCreada = ventanaRepository.save(ventana);

        return daoToDtoConverter.convertDaoToDtoVentana(ventanaCreada);
    }

    @Override
    public VentanaDto modificarVentana(String uuid, VentanaDto ventanaDto, String username) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Ventana ventana = ventanaRepository.getByUuidAndEliminadoFalse(uuid);

        if(ventana == null) {
            logger.warn("La ventana no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        ventana.setNombre(ventanaDto.getNombre());
        ventana.setFechaInicio(LocalDate.parse(ventanaDto.getFechaInicio()));
        ventana.setFechaFin(LocalDate.parse(ventanaDto.getFechaFin()));
        daoHelper.fulfillAuditorFields(false, ventana, usuarioDto.getId());
        ventanaRepository.save(ventana);
        return daoToDtoConverter.convertDaoToDtoVentana(ventana);
    }

    @Override
    public VentanaDto eliminarVentana(String uuid, String username) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Ventana ventana = ventanaRepository.getByUuidAndEliminadoFalse(uuid);

        if(ventana == null) {
            logger.warn("La ventana no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        ventana.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, ventana, usuarioDto.getId());
        ventanaRepository.save(ventana);
        return daoToDtoConverter.convertDaoToDtoVentana(ventana);
    }
}
