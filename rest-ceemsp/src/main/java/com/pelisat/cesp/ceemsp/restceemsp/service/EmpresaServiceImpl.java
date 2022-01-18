package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.EmpresaModalidad;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaModalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoPersonaEnum;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final UsuarioService usuarioService;
    private final EmpresaRepository empresaRepository;
    private final EmpresaModalidadRepository empresaModalidadRepository;
    private final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public EmpresaServiceImpl(UsuarioService usuarioService, EmpresaRepository empresaRepository, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaModalidadRepository empresaModalidadRepository,
                              DaoHelper<CommonModel> daoHelper) {
        this.usuarioService = usuarioService;
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaModalidadRepository = empresaModalidadRepository;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<EmpresaDto> obtenerTodas() {
        List<Empresa> empresas = empresaRepository.getAllByEliminadoFalse();
        return empresas.stream().map(daoToDtoConverter::convertDaoToDtoEmpresa).collect(Collectors.toList());
    }

    @Override
    public EmpresaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la empresa a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresa);
    }

    @Override
    public EmpresaDto obtenerPorId(int id) {
        return null;
    }

    @Override
    @Transactional
    public EmpresaDto crearEmpresa(EmpresaDto empresaDto, String username) {
        if(StringUtils.isBlank(username) || empresaDto == null) {
            logger.warn("La empresa a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Dando de alta a una nueva empresa con razon social: [{}]", empresaDto.getNombreComercial());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        Empresa empresa = dtoToDaoConverter.convertDtoToDaoEmpresa(empresaDto);

        empresa.setFechaCreacion(LocalDateTime.now());
        empresa.setCreadoPor(usuario.getId());
        empresa.setActualizadoPor(usuario.getId());
        empresa.setFechaActualizacion(LocalDateTime.now());
        empresa.setStatus(EmpresaStatusEnum.ACTIVA);

        Empresa empresaCreada = empresaRepository.save(empresa);

        List<EmpresaModalidad> empresaModalidades = empresaDto.getModalidades().stream()
                .map(m -> {
                    EmpresaModalidad empresaModalidad = new EmpresaModalidad();
                    empresaModalidad.setUuid(RandomStringUtils.randomAlphanumeric(12));
                    empresaModalidad.setEmpresa(empresaCreada.getId());
                    empresaModalidad.setFechaCreacion(LocalDateTime.now());
                    empresaModalidad.setCreadoPor(usuario.getId());
                    empresaModalidad.setActualizadoPor(usuario.getId());
                    empresaModalidad.setFechaActualizacion(LocalDateTime.now());
                    empresaModalidad.setModalidad(m.getModalidad().getId());
                    if(m.getSubmodalidad() != null) {
                        empresaModalidad.setSubmodalidad(m.getSubmodalidad().getId());
                    }


                    return empresaModalidad;
                }).collect(Collectors.toList());

        List<EmpresaModalidad> createdModalidades = empresaModalidadRepository.saveAll(empresaModalidades);

        EmpresaDto response = daoToDtoConverter.convertDaoToDtoEmpresa(empresaCreada);
        response.setModalidades(createdModalidades.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaModalidad).collect(Collectors.toList()));

        return response;
    }

    @Override
    public EmpresaDto modificarEmpresa(EmpresaDto empresaDto, String username, String uuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(uuid) || empresaDto == null) {
            logger.warn("La empresa a modificar, el uuid o el usuario vienen como nulos o vacios.");
            throw new InvalidDataException();
        }

        logger.info("Modificando la empresa uuid");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);
        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresa.setNombreComercial(empresaDto.getNombreComercial());
        empresa.setRazonSocial(empresaDto.getRazonSocial());
        empresa.setRfc(empresaDto.getRfc());
        empresa.setCorreoElectronico(empresaDto.getCorreoElectronico());
        empresa.setTelefono(empresaDto.getTelefono());
        empresa.setTipoPersona(empresaDto.getTipoPersona());
        if(empresa.getTipoPersona() == TipoPersonaEnum.FISICA) {
            empresa.setCurp(empresaDto.getCurp());
            empresa.setSexo(empresaDto.getSexo());
        }

        daoHelper.fulfillAuditorFields(false, empresa, usuarioDto.getId());
        empresaRepository.save(empresa);

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresa);
    }

    @Override
    public EmpresaDto cambiarStatusEmpresa(EmpresaDto empresaDto, String username, String uuid) {
        if(empresaDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(uuid)) {
            logger.warn("El objeto, el usuario o el uuid vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        if(StringUtils.isBlank(empresaDto.getObservaciones()) || empresaDto.getStatus() == null) {
            logger.warn("El objeto viene con campos invalidos");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);
        if(empresa == null) {
            logger.warn("La empresa no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        empresa.setStatus(empresaDto.getStatus());
        empresa.setObservaciones(empresaDto.getObservaciones());

        Empresa empresaNuevoStatus = empresaRepository.save(empresa);

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresaNuevoStatus);
    }

    @Override
    public EmpresaDto eliminarEmpresa(String username, String uuid) {
        return null;
    }
}
