package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanAdiestramientoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CanAdiestramiento;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.CanAdiestramientoRepository;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanAdiestramientoServiceImpl implements CanAdiestramientoService {

    private final Logger logger = LoggerFactory.getLogger(CanAdiestramientoService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final CanAdiestramientoRepository canAdiestramientoRepository;
    private final CanRepository canRepository;
    private final CanTipoAdiestramientoService canTipoAdiestramientoService;

    @Autowired
    public CanAdiestramientoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                        EmpresaService empresaService, UsuarioService usuarioService, CanAdiestramientoRepository canAdiestramientoRepository,
                                        CanRepository canRepository, CanTipoAdiestramientoService canTipoAdiestramientoService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.canAdiestramientoRepository = canAdiestramientoRepository;
        this.canRepository = canRepository;
        this.canTipoAdiestramientoService = canTipoAdiestramientoService;
    }

    @Override
    public List<CanAdiestramientoDto> obtenerAdiestramientosPorCanUuid(String empresaUuid, String canUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        Can can = canRepository.getByUuid(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanAdiestramiento> canAdiestramientos = canAdiestramientoRepository.findAllByCanAndEliminadoFalse(can.getId());

        return canAdiestramientos.stream().map(ca -> {
            CanAdiestramientoDto canAdiestramientoDto = daoToDtoConverter.convertDaoToDtoCanAdiestramiento(ca);
            canAdiestramientoDto.setCanTipoAdiestramiento(canTipoAdiestramientoService.obtenerPorId(ca.getTipoAdiestramiento()));
            return canAdiestramientoDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CanAdiestramientoDto> obtenerTodosAdiestramientosPorCanUuid(String empresaUuid, String canUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        Can can = canRepository.getByUuid(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanAdiestramiento> canAdiestramientos = canAdiestramientoRepository.findAllByCan(can.getId());

        return canAdiestramientos.stream().map(ca -> {
            CanAdiestramientoDto canAdiestramientoDto = daoToDtoConverter.convertDaoToDtoCanAdiestramiento(ca);
            canAdiestramientoDto.setCanTipoAdiestramiento(canTipoAdiestramientoService.obtenerPorId(ca.getTipoAdiestramiento()));
            return canAdiestramientoDto;
        }).collect(Collectors.toList());
    }

    @Override
    public CanAdiestramientoDto guardarCanAdiestramiento(String empresaUuid, String canUuid, String username, CanAdiestramientoDto canAdiestramientoDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canAdiestramientoDto == null) {
            logger.warn("El uuid de la empresa, el can, el usuario o el adiestramiento a guardar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nuevo adiestramiento al can [{}]", username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        CanAdiestramiento canAdiestramiento = dtoToDaoConverter.convertDtoToDaoAdiestramiento(canAdiestramientoDto);
        canAdiestramiento.setCan(can.getId());
        canAdiestramiento.setTipoAdiestramiento(canAdiestramientoDto.getCanTipoAdiestramiento().getId());
        canAdiestramiento.setFechaConstancia(LocalDate.parse(canAdiestramientoDto.getFechaConstancia()));
        daoHelper.fulfillAuditorFields(true, canAdiestramiento, usuarioDto.getId());

        CanAdiestramiento canAdiestramientoCreado = canAdiestramientoRepository.save(canAdiestramiento);

        return daoToDtoConverter.convertDaoToDtoCanAdiestramiento(canAdiestramientoCreado);
    }

    @Override
    public CanAdiestramientoDto modificarCanAdiestramiento(String empresaUuid, String canUuid, String adiestramientoUuid, String username, CanAdiestramientoDto canAdiestramientoDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(adiestramientoUuid) || StringUtils.isBlank(username) || canAdiestramientoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Modificando adiestramiento con uuid [{}]", adiestramientoUuid);

        CanAdiestramiento canAdiestramiento = canAdiestramientoRepository.findByUuidAndEliminadoFalse(adiestramientoUuid);

        if(canAdiestramiento == null) {
            logger.warn("El adiestramiento viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        canAdiestramiento.setFechaConstancia(LocalDate.parse(canAdiestramientoDto.getFechaConstancia()));
        canAdiestramiento.setTipoAdiestramiento(canAdiestramientoDto.getCanTipoAdiestramiento().getId());
        canAdiestramiento.setNombreInstructor(canAdiestramientoDto.getNombreInstructor());
        daoHelper.fulfillAuditorFields(false, canAdiestramiento, usuarioDto.getId());
        canAdiestramientoRepository.save(canAdiestramiento);
        return canAdiestramientoDto;
    }

    @Override
    public CanAdiestramientoDto eliminarCanAdiestramiento(String empresaUuid, String canUuid, String adiestramientoUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(adiestramientoUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando adiestramiento con uuid [{}]", adiestramientoUuid);

        CanAdiestramiento canAdiestramiento = canAdiestramientoRepository.findByUuidAndEliminadoFalse(adiestramientoUuid);

        if(canAdiestramiento == null) {
            logger.warn("El adiestramiento viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        canAdiestramiento.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, canAdiestramiento, usuarioDto.getId());
        canAdiestramientoRepository.save(canAdiestramiento);
        return daoToDtoConverter.convertDaoToDtoCanAdiestramiento(canAdiestramiento);
    }
}
