package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaDomicilioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
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
public class EmpresaDomicilioServiceImpl implements EmpresaDomicilioService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaDomicilioService.class);
    private final EmpresaDomicilioRepository empresaDomicilioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public EmpresaDomicilioServiceImpl(EmpresaDomicilioRepository empresaDomicilioRepository, DaoToDtoConverter daoToDtoConverter,
                                       DtoToDaoConverter dtoToDaoConverter, UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper) {
        this.empresaDomicilioRepository = empresaDomicilioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<EmpresaDomicilioDto> obtenerDomicilios(String empresaUsername) {
        if(StringUtils.isBlank(empresaUsername)) {
            logger.warn("El usuario de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los domicilios de la empresa ligada al usuario [{}]", empresaUsername);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(empresaUsername);
        List<EmpresaDomicilio> empresaDomicilios = empresaDomicilioRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return empresaDomicilios.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaDomicilio).collect(Collectors.toList());
    }

    @Override
    public EmpresaDomicilioDto obtenerDomicilioPorUuid(String empresaUsername, String domicilioUuid) {
        return null;
    }

    @Override
    public EmpresaDomicilioDto guardarDomicilio(String empresaUsername, EmpresaDomicilioDto empresaDomicilioDto) {
        return null;
    }
}
