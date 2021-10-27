package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaDomicilioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
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
public class EmpresaDomicilioServiceImpl implements EmpresaDomicilioService{

    private final Logger logger = LoggerFactory.getLogger(EmpresaDomicilioService.class);

    private final EmpresaDomicilioRepository empresaDomicilioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;

    @Autowired
    public EmpresaDomicilioServiceImpl(
        EmpresaDomicilioRepository empresaDomicilioRepository,
        DaoToDtoConverter daoToDtoConverter,
        DtoToDaoConverter dtoToDaoConverter,
        UsuarioService usuarioService,
        EmpresaService empresaService
    ) {
        this.empresaDomicilioRepository = empresaDomicilioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
    }

    @Override
    public List<EmpresaDomicilioDto> obtenerPorEmpresaId(int empresaId) {
        if(empresaId < 1) {
            logger.warn("El id de la empresa no es correcto");
            throw new InvalidDataException();
        }

        List<EmpresaDomicilio> empresaDomicilios = empresaDomicilioRepository.findAllByEmpresaAndEliminadoFalse(empresaId);
        return empresaDomicilios.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaDomicilio).collect(Collectors.toList());
    }

    @Override
    public List<EmpresaDomicilioDto> obtenerPorEmpresaUuid(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa no es correcto");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        List<EmpresaDomicilio> empresaDomicilios = empresaDomicilioRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());
        return empresaDomicilios.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaDomicilio).collect(Collectors.toList());
    }

    @Override
    public EmpresaDomicilioDto guardar(String empresaUuid, String username, EmpresaDomicilioDto empresaDomicilioDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || empresaDomicilioDto == null) {
            logger.warn("La empresa a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Dando de alta nuevo domicilio en la empresa con uuid: [{}]", empresaUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        EmpresaDomicilio empresaDomicilio = dtoToDaoConverter.convertDtoToDaoEmpresaDomicilio(empresaDomicilioDto);

        empresaDomicilio.setEmpresa(empresaDto.getId());
        empresaDomicilio.setFechaCreacion(LocalDateTime.now());
        empresaDomicilio.setCreadoPor(usuario.getId());
        empresaDomicilio.setActualizadoPor(usuario.getId());
        empresaDomicilio.setFechaActualizacion(LocalDateTime.now());

        EmpresaDomicilio empresaDomicilioCreado = empresaDomicilioRepository.save(empresaDomicilio);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilio(empresaDomicilioCreado);
    }


}
