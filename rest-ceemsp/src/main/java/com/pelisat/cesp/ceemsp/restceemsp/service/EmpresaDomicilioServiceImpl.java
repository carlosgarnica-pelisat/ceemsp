package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaDomicilioRepository;
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
public class EmpresaDomicilioServiceImpl implements EmpresaDomicilioService{

    private final Logger logger = LoggerFactory.getLogger(EmpresaDomicilioService.class);
    private final EmpresaDomicilioRepository empresaDomicilioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final DaoHelper<CommonModel> daoHelper;
    private final EstadoService estadoService;
    private final MunicipioService municipioService;
    private final ColoniaService coloniaService;
    private final LocalidadService localidadService;
    private final CalleService calleService;

    @Autowired
    public EmpresaDomicilioServiceImpl(
        EmpresaDomicilioRepository empresaDomicilioRepository,
        DaoToDtoConverter daoToDtoConverter,
        DtoToDaoConverter dtoToDaoConverter,
        UsuarioService usuarioService,
        EmpresaService empresaService,
        DaoHelper<CommonModel> daoHelper,
        EstadoService estadoService,
        MunicipioService municipioService,
        LocalidadService localidadService,
        ColoniaService coloniaService,
        CalleService calleService
    ) {
        this.empresaDomicilioRepository = empresaDomicilioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.daoHelper = daoHelper;
        this.estadoService = estadoService;
        this.municipioService = municipioService;
        this.localidadService = localidadService;
        this.coloniaService = coloniaService;
        this.calleService = calleService;
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
    public EmpresaDomicilioDto obtenerPorId(int id) {
        if(id < 1) {
            logger.warn("El id viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDomicilio empresaDomicilio = empresaDomicilioRepository.getOne(id);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilio(empresaDomicilio);
    }

    @Override
    public EmpresaDomicilioDto obtenerPorUuid(String uuid, String domicilioUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(domicilioUuid)) {
            logger.warn("El uuid del domicilio o de la empresa viene como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el domicilio con uuid [{}]", domicilioUuid);

        EmpresaDomicilio empresaDomicilio = empresaDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(empresaDomicilio == null) {
            logger.warn("El domiciio de la empresa no existe en la bsase de datos");
            throw new NotFoundResourceException();
        }

        EmpresaDomicilioDto empresaDomicilioDto = daoToDtoConverter.convertDaoToDtoEmpresaDomicilio(empresaDomicilio);
        empresaDomicilioDto.setCalleCatalogo(calleService.obtenerCallePorId(empresaDomicilio.getCalleCatalogo()));
        empresaDomicilioDto.setColoniaCatalogo(coloniaService.obtenerColoniaPorId(empresaDomicilio.getColoniaCatalogo()));
        empresaDomicilioDto.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(empresaDomicilio.getLocalidadCatalogo()));
        empresaDomicilioDto.setEstadoCatalogo(estadoService.obtenerPorId(empresaDomicilio.getEstadoCatalogo()));
        empresaDomicilioDto.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(empresaDomicilio.getMunicipioCatalogo()));

        return empresaDomicilioDto;
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
        empresaDomicilio.setEstadoCatalogo(empresaDomicilioDto.getEstadoCatalogo().getId());
        empresaDomicilio.setMunicipioCatalogo(empresaDomicilioDto.getMunicipioCatalogo().getId());
        empresaDomicilio.setColoniaCatalogo(empresaDomicilioDto.getColoniaCatalogo().getId());
        empresaDomicilio.setLocalidadCatalogo(empresaDomicilioDto.getLocalidadCatalogo().getId());
        empresaDomicilio.setCalleCatalogo(empresaDomicilioDto.getCalleCatalogo().getId());

        empresaDomicilio.setDomicilio1(empresaDomicilioDto.getCalleCatalogo().getNombre());
        empresaDomicilio.setDomicilio2(empresaDomicilioDto.getColoniaCatalogo().getNombre());
        empresaDomicilio.setDomicilio3(empresaDomicilioDto.getMunicipioCatalogo().getNombre());
        empresaDomicilio.setEstado(empresaDomicilioDto.getEstadoCatalogo().getNombre());
        empresaDomicilio.setLocalidad(empresaDomicilioDto.getLocalidadCatalogo().getNombre());

        EmpresaDomicilio empresaDomicilioCreado = empresaDomicilioRepository.save(empresaDomicilio);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilio(empresaDomicilioCreado);
    }

    @Override
    public EmpresaDomicilioDto modificarEmpresaDomicilio(String empresaUuid, String domicilioUuid, String username, EmpresaDomicilioDto empresaDomicilioDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(username) || empresaDomicilioDto == null) {
            logger.warn("El uuid de la empresa, el domicilio, el usuario o el domicilio a modificar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando domicilio con el uuid [{}]", domicilioUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDomicilio empresaDomicilio = empresaDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(empresaDomicilio == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaDomicilio.setNombre(empresaDomicilioDto.getNombre());
        empresaDomicilio.setNumeroExterior(empresaDomicilioDto.getNumeroExterior());
        empresaDomicilio.setNumeroInterior(empresaDomicilioDto.getNumeroInterior());
        empresaDomicilio.setDomicilio4(empresaDomicilioDto.getDomicilio4());
        empresaDomicilio.setPais(empresaDomicilioDto.getPais());
        empresaDomicilio.setCodigoPostal(empresaDomicilioDto.getCodigoPostal());
        empresaDomicilio.setTelefonoFijo(empresaDomicilioDto.getTelefonoFijo());
        empresaDomicilio.setTelefonoMovil(empresaDomicilioDto.getTelefonoMovil());
        empresaDomicilio.setMatriz(empresaDomicilioDto.isMatriz());

        empresaDomicilio.setEstadoCatalogo(empresaDomicilioDto.getEstadoCatalogo().getId());
        empresaDomicilio.setMunicipioCatalogo(empresaDomicilioDto.getMunicipioCatalogo().getId());
        empresaDomicilio.setColoniaCatalogo(empresaDomicilioDto.getColoniaCatalogo().getId());
        empresaDomicilio.setLocalidadCatalogo(empresaDomicilioDto.getLocalidadCatalogo().getId());
        empresaDomicilio.setCalleCatalogo(empresaDomicilioDto.getCalleCatalogo().getId());

        empresaDomicilio.setDomicilio1(empresaDomicilioDto.getCalleCatalogo().getNombre());
        empresaDomicilio.setDomicilio2(empresaDomicilioDto.getColoniaCatalogo().getNombre());
        empresaDomicilio.setDomicilio3(empresaDomicilioDto.getMunicipioCatalogo().getNombre());
        empresaDomicilio.setEstado(empresaDomicilioDto.getEstadoCatalogo().getNombre());
        empresaDomicilio.setLocalidad(empresaDomicilioDto.getLocalidadCatalogo().getNombre());

        daoHelper.fulfillAuditorFields(false, empresaDomicilio, usuarioDto.getId());
        empresaDomicilioRepository.save(empresaDomicilio);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilio(empresaDomicilio);
    }

    @Override
    public EmpresaDomicilioDto eliminarEmpresaDomicilio(String empresaUuid, String domicilioUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa, el domicilio o el usuario vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.warn("Borrando el domicilio de la empresa con el uuid [{}]", domicilioUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDomicilio empresaDomicilio = empresaDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(empresaDomicilio == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaDomicilio.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaDomicilio, usuarioDto.getId());
        empresaDomicilioRepository.save(empresaDomicilio);

        return daoToDtoConverter.convertDaoToDtoEmpresaDomicilio(empresaDomicilio);
    }
}
