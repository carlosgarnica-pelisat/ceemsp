package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import com.pelisat.cesp.ceemsp.database.dto.CanConstanciaSaludDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CanCartillaVacunacion;
import com.pelisat.cesp.ceemsp.database.model.CanConstanciaSalud;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.CanConstanciaSaludRepository;
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
public class CanConstanciaSaludServiceImpl implements CanConstanciaSaludService {

    private final Logger logger = LoggerFactory.getLogger(CanConstanciaSaludService.class);
    private final CanRepository canRepository;
    private final UsuarioService usuarioService;
    private final CanConstanciaSaludRepository canConstanciaSaludRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public CanConstanciaSaludServiceImpl(CanRepository canRepository, UsuarioService usuarioService, CanConstanciaSaludRepository canConstanciaSaludRepository, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper) {
        this.canRepository = canRepository;
        this.usuarioService = usuarioService;
        this.canConstanciaSaludRepository = canConstanciaSaludRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<CanConstanciaSaludDto> obtenerConstanciasSaludPorCanUuid(String empresaUuid, String canUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo constancias de salud del can con uuid [{}]", canUuid);

        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanConstanciaSalud> canConstanciasdeSalud = canConstanciaSaludRepository.findAllByCanAndEliminadoFalse(can.getId());
        return canConstanciasdeSalud.stream().map(daoToDtoConverter::convertDaoToDtoCanConstanciaSalud).collect(Collectors.toList());
    }

    @Override
    public CanConstanciaSaludDto guardarConstanciaSalud(String empresaUuid, String canUuid, String username, CanConstanciaSaludDto canConstanciaSaludDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canConstanciaSaludDto == null) {
            logger.warn("El uuid de la empresa, el can, el usuario o la cartilla de vacunacion vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando constancia de salud al can [{}]", username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        CanConstanciaSalud canConstanciaSalud = dtoToDaoConverter.convertDtoToDaoCanConstanciaSalud(canConstanciaSaludDto);
        canConstanciaSalud.setCan(can.getId());
        daoHelper.fulfillAuditorFields(true, canConstanciaSalud, usuarioDto.getId());

        CanConstanciaSalud canConstanciaSaludCreada = canConstanciaSaludRepository.save(canConstanciaSalud);

        return daoToDtoConverter.convertDaoToDtoCanConstanciaSalud(canConstanciaSaludCreada);
    }
}
