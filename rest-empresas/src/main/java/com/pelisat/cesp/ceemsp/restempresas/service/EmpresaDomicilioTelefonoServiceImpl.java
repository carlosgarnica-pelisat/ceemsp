package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioTelefonoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilioTelefono;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaDomicilioRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaDomicilioTelefonoRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaDomicilioTelefonoServiceImpl implements EmpresaDomicilioTelefonoService {

    private final EmpresaDomicilioTelefonoRepository empresaDomicilioTelefonoRepository;
    private final Logger logger = LoggerFactory.getLogger(EmpresaDomicilioTelefonoService.class);
    private final UsuarioService usuarioService;
    private final EmpresaDomicilioRepository empresaDomicilioRepository;
    private final DaoHelper<CommonModel> daoHelper;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;

    @Autowired
    public EmpresaDomicilioTelefonoServiceImpl(EmpresaDomicilioTelefonoRepository empresaDomicilioTelefonoRepository, UsuarioService usuarioService,
                                               EmpresaDomicilioRepository empresaDomicilioRepository, DaoHelper<CommonModel> daoHelper,
                                               DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter
                                               ) {
        this.empresaDomicilioTelefonoRepository = empresaDomicilioTelefonoRepository;
        this.usuarioService = usuarioService;
        this.empresaDomicilioRepository = empresaDomicilioRepository;
        this.daoHelper = daoHelper;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<EmpresaDomicilioTelefonoDto> obtenerTelefonosPorDomicilio(String domicilioUuid) {
        if(StringUtils.isBlank(domicilioUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los telefonos con el uuid del domicilio [{}]", domicilioUuid);

        EmpresaDomicilio empresaDomicilio = empresaDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(empresaDomicilio == null) {
            logger.warn("El domicilio no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaDomicilioTelefono> telefonos = empresaDomicilioTelefonoRepository.findAllByDomicilioAndEliminadoFalse(empresaDomicilio.getId());
        return telefonos.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaDomicilioTelefono).collect(Collectors.toList());
    }

    @Override
    public EmpresaDomicilioTelefonoDto obtenerTelefonoPorUuid(String domicilioUuid, String telefonoUuid) {
        if(StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(telefonoUuid)) {
            logger.warn("Alguno de los parametros viene invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el telefono con el uuid [{}]", telefonoUuid);

        EmpresaDomicilioTelefono empresaDomicilioTelefono = empresaDomicilioTelefonoRepository.findByUuidAndEliminadoFalse(telefonoUuid);
        if(empresaDomicilioTelefono == null) {
            logger.warn("El domicilio no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilioTelefono(empresaDomicilioTelefono);
    }

    @Override
    @Transactional
    public EmpresaDomicilioTelefonoDto guardarTelefono(String domicilioUuid, String username, EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto) {
        if(StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(username) || empresaDomicilioTelefonoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Guardando el telefono en el domicilio con uuid [{}]", domicilioUuid);

        EmpresaDomicilio empresaDomicilio = empresaDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(empresaDomicilio == null) {
            logger.warn("El domicilio no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDomicilioTelefono empresaDomicilioTelefono = dtoToDaoConverter.convertDtoToDaoEmpresaDomicilioTelefono(empresaDomicilioTelefonoDto);
        daoHelper.fulfillAuditorFields(true, empresaDomicilioTelefono, usuarioDto.getId());
        empresaDomicilioTelefono.setDomicilio(empresaDomicilio.getId());
        EmpresaDomicilioTelefono creado = empresaDomicilioTelefonoRepository.save(empresaDomicilioTelefono);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilioTelefono(creado);
    }

    @Override
    @Transactional
    public EmpresaDomicilioTelefonoDto modificarTelefono(String domicilioUuid, String telefonoUuid, String username, EmpresaDomicilioTelefonoDto empresaDomicilioTelefonoDto) {
        if(StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(telefonoUuid) || StringUtils.isBlank(username) || empresaDomicilioTelefonoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Modificando el telefono con uuid [{}]", telefonoUuid);

        EmpresaDomicilioTelefono empresaDomicilioTelefono = empresaDomicilioTelefonoRepository.findByUuidAndEliminadoFalse(telefonoUuid);
        if(empresaDomicilioTelefono == null) {
            logger.warn("El domicilio no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        empresaDomicilioTelefono.setTelefono(empresaDomicilioTelefonoDto.getTelefono());
        empresaDomicilioTelefono.setTipoTelefono(empresaDomicilioTelefonoDto.getTipoTelefono());
        daoHelper.fulfillAuditorFields(false, empresaDomicilioTelefono, usuarioDto.getId());

        EmpresaDomicilioTelefono creado = empresaDomicilioTelefonoRepository.save(empresaDomicilioTelefono);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilioTelefono(creado);
    }

    @Override
    @Transactional
    public EmpresaDomicilioTelefonoDto eliminarTelefono(String domicilioUuid, String telefonoUuid, String username) {
        if(StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(telefonoUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el telefono con uuid [{}]", telefonoUuid);

        EmpresaDomicilioTelefono empresaDomicilioTelefono = empresaDomicilioTelefonoRepository.findByUuidAndEliminadoFalse(telefonoUuid);
        if(empresaDomicilioTelefono == null) {
            logger.warn("El domicilio no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        empresaDomicilioTelefono.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaDomicilioTelefono, usuarioDto.getId());

        EmpresaDomicilioTelefono creado = empresaDomicilioTelefonoRepository.save(empresaDomicilioTelefono);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilioTelefono(creado);
    }
}
