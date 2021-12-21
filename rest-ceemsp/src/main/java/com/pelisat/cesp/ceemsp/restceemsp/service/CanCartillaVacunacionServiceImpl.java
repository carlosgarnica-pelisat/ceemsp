package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CanAdiestramiento;
import com.pelisat.cesp.ceemsp.database.model.CanCartillaVacunacion;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.CanCartillaVacunacionRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanCartillaVacunacionServiceImpl implements CanCartillaVacunacionService{

    private final Logger logger = LoggerFactory.getLogger(CanCartillaVacunacionService.class);
    private final UsuarioService usuarioService;
    private final CanRepository canRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final CanCartillaVacunacionRepository canCartillaVacunacionRepository;

    @Autowired
    public CanCartillaVacunacionServiceImpl(UsuarioService usuarioService, CanRepository canRepository, DaoToDtoConverter daoToDtoConverter,
                                            DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, CanCartillaVacunacionRepository canCartillaVacunacionRepository) {
        this.usuarioService = usuarioService;
        this.canRepository = canRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.canCartillaVacunacionRepository = canCartillaVacunacionRepository;
    }

    @Override
    public List<CanCartillaVacunacionDto> obtenerCartillasVacunacionPorCanUuid(String empresaUuid, String canUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las cartillas de vacunacion para el can con uuid [{}]", canUuid);

        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanCartillaVacunacion> canCartillaVacunaciones = canCartillaVacunacionRepository.findAllByCanAndEliminadoFalse(can.getId());
        return canCartillaVacunaciones.stream().map(daoToDtoConverter::convertDaoToDtoCanCartillaVacunacion).collect(Collectors.toList());
    }

    @Override
    public CanCartillaVacunacionDto guardarCartillaVacunacion(String empresaUuid, String canUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canCartillaVacunacionDto == null) {
            logger.warn("El uuid de la empresa, el can, el usuario o la cartilla de vacunacion a guardar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nueva cartilla de vacunacion al can [{}]", username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        CanCartillaVacunacion canCartillaVacunacion = dtoToDaoConverter.convertDtoToDaoCanCartillaVacunacion(canCartillaVacunacionDto);
        canCartillaVacunacion.setCan(can.getId());
        daoHelper.fulfillAuditorFields(true, canCartillaVacunacion, usuarioDto.getId());

        CanCartillaVacunacion canCartillaVacunacionCreada = canCartillaVacunacionRepository.save(canCartillaVacunacion);

        return daoToDtoConverter.convertDaoToDtoCanCartillaVacunacion(canCartillaVacunacionCreada);
    }
}
