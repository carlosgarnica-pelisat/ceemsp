package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaLicenciaColectivaDomicilio;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaLicenciaColectivaDomicilioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaLicenciaColectivaDomicilioServiceImpl implements EmpresaLicenciaColectivaDomicilioService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaLicenciaColectivaDomicilioService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final EmpresaLicenciaColectivaDomicilioRepository empresaLicenciaColectivaDomicilioRepository;
    private final EmpresaLicenciaColectivaService empresaLicenciaColectivaService;
    private final EmpresaDomicilioService empresaDomicilioService;

    @Autowired
    public EmpresaLicenciaColectivaDomicilioServiceImpl(
            DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
            DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
            EmpresaLicenciaColectivaDomicilioRepository empresaLicenciaColectivaDomicilioRepository,
            EmpresaLicenciaColectivaService empresaLicenciaColectivaService,
            EmpresaDomicilioService empresaDomicilioService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaLicenciaColectivaDomicilioRepository = empresaLicenciaColectivaDomicilioRepository;
        this.empresaLicenciaColectivaService = empresaLicenciaColectivaService;
        this.empresaDomicilioService = empresaDomicilioService;
    }

    @Override
    public List<EmpresaDomicilioDto> obtenerDomiciliosPorLicenciaColectiva(String licenciaColectivaUuid) {
        if(StringUtils.isBlank(licenciaColectivaUuid)) {
            logger.warn("El uuid de la empresa o el de la licencia colectiva vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los domicilios de la licencia colectiva con el uuid [{}]", licenciaColectivaUuid);

        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(licenciaColectivaUuid, true);
        List<EmpresaLicenciaColectivaDomicilio> domicilios = empresaLicenciaColectivaDomicilioRepository.findAllByLicenciaColectivaAndEliminadoFalse(empresaLicenciaColectivaDto.getId());

        return domicilios.stream().map(d -> empresaDomicilioService.obtenerPorId(d.getDomicilio())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmpresaDomicilioDto guardarDomicilioEnLicenciaColectiva(String licenciaColectivaUuid, String username, EmpresaDomicilioDto empresaDomicilioDto) {
        if(StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(username) || empresaDomicilioDto == null) {
            logger.warn("El uuid de la empresa, la licencia, el usuario o el domicilio a registrar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Registrando el domicilio [{}] en la licencia [{}]", empresaDomicilioDto.getUuid(), licenciaColectivaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDomicilioDto domicilio = empresaDomicilioService.obtenerDomicilioPorUuid(username, empresaDomicilioDto.getUuid());
        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(licenciaColectivaUuid, true);

        EmpresaLicenciaColectivaDomicilio empresaLicenciaColectivaDomicilio = new EmpresaLicenciaColectivaDomicilio();
        empresaLicenciaColectivaDomicilio.setUuid(RandomStringUtils.randomAlphanumeric(12));
        empresaLicenciaColectivaDomicilio.setDomicilio(domicilio.getId());
        empresaLicenciaColectivaDomicilio.setLicenciaColectiva(empresaLicenciaColectivaDto.getId());

        daoHelper.fulfillAuditorFields(true, empresaLicenciaColectivaDomicilio, usuarioDto.getId());

        empresaLicenciaColectivaDomicilioRepository.save(empresaLicenciaColectivaDomicilio);

        return domicilio;
    }

    @Override
    @Transactional
    public EmpresaDomicilioDto eliminarDomicilioEnLicenciaColectiva(String licenciaColectivaUuid, String domicilioUuid, String username) {
        if(StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(domicilioUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminndo el domicilio registrado con la licencia colectiva");
        EmpresaDomicilioDto empresaDomicilioDto = empresaDomicilioService.obtenerDomicilioPorUuid(username, domicilioUuid);
        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(licenciaColectivaUuid, true);
        EmpresaLicenciaColectivaDomicilio empresaLicenciaColectivaDomicilio = empresaLicenciaColectivaDomicilioRepository.findByLicenciaColectivaAndDomicilioAndEliminadoFalse(empresaLicenciaColectivaDto.getId(), empresaDomicilioDto.getId());

        if(empresaLicenciaColectivaDomicilio == null) {
            logger.warn("No se encontro el domicilio de la licencia colectiva");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        empresaLicenciaColectivaDomicilio.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaLicenciaColectivaDomicilio, usuario.getId());
        empresaLicenciaColectivaDomicilioRepository.save(empresaLicenciaColectivaDomicilio);
        return null;
    }
}
