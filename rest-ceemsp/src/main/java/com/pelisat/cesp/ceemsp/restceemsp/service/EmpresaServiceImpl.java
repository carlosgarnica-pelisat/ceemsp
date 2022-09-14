package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaFormaEjecucionRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaModalidadRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.database.type.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.DuplicatedEnterpriseException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.NotificacionEmailService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaModalidadRepository empresaModalidadRepository;
    private final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final NotificacionEmailService notificacionEmailService;
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;

    @Autowired
    public EmpresaServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaModalidadRepository empresaModalidadRepository,
                              DaoHelper<CommonModel> daoHelper, NotificacionEmailService notificacionEmailService,
                              EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaModalidadRepository = empresaModalidadRepository;
        this.daoHelper = daoHelper;
        this.notificacionEmailService = notificacionEmailService;
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
    }

    @Override
    public List<EmpresaDto> obtenerTodas() {
        List<Empresa> empresas = empresaRepository.getAllByEliminadoFalse();
        return empresas.stream().map(e -> {
            EmpresaDto empresaDto  = daoToDtoConverter.convertDaoToDtoEmpresa(e);
            Usuario usuarioEmpresa = usuarioRepository.getUsuarioByEmpresaAndEliminadoFalse(e.getId());
            empresaDto.setUsuario(daoToDtoConverter.convertDaoToDtoUser(usuarioEmpresa));
            return empresaDto;
        }).collect(Collectors.toList());
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

        EmpresaDto empresaDto = daoToDtoConverter.convertDaoToDtoEmpresa(empresa);
        Usuario usuarioEmpresa = usuarioRepository.getUsuarioByEmpresaAndEliminadoFalse(empresa.getId());
        empresaDto.setUsuario(daoToDtoConverter.convertDaoToDtoUser(usuarioEmpresa));
        List<EmpresaFormaEjecucion> formasEjecucion = this.empresaFormaEjecucionRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());
        AtomicBoolean tieneArmas = new AtomicBoolean(false);
        AtomicBoolean tieneCanes = new AtomicBoolean(false);

        formasEjecucion.forEach(x -> {
            if(x.getFormaEjecucion() == FormaEjecucionEnum.ARMAS) tieneArmas.set(true);
            if(x.getFormaEjecucion() == FormaEjecucionEnum.CANES) tieneCanes.set(true);
        });

        empresaDto.setTieneArmas(tieneArmas.get());
        empresaDto.setTieneCanes(tieneCanes.get());
        return empresaDto;
    }

    @Override
    public EmpresaDto obtenerPorId(int id) {
        if(id < 1) {
            logger.warn("El id de la empresa a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getOne(id);

        if(empresa == null || empresa.getEliminado()) {
            logger.warn("La empresa no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresa);
    }

    @Override
    @Transactional
    public EmpresaDto crearEmpresa(EmpresaDto empresaDto, String username, MultipartFile multipartFile) {
        if(StringUtils.isBlank(username) || empresaDto == null) {
            logger.warn("La empresa a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Dando de alta a una nueva empresa con razon social: [{}]", empresaDto.getNombreComercial());

        Usuario usuario = usuarioRepository.getUsuarioByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        Empresa existeEmpresa = empresaRepository.getFirstByRegistroAndEliminadoFalse(empresaDto.getRegistro());

        if(existeEmpresa != null) {
            logger.warn("Ya hay una empresa con este registro");
            throw new DuplicatedEnterpriseException();
        }

        Empresa empresa = dtoToDaoConverter.convertDtoToDaoEmpresa(empresaDto);

        empresa.setFechaCreacion(LocalDateTime.now());
        empresa.setCreadoPor(usuario.getId());
        empresa.setActualizadoPor(usuario.getId());
        empresa.setFechaActualizacion(LocalDateTime.now());
        empresa.setStatus(EmpresaStatusEnum.ACTIVA);
        if(StringUtils.isNotBlank(empresaDto.getFechaInicio())) {
            empresa.setFechaInicio(LocalDate.parse(empresaDto.getFechaInicio()));
        }
        if(StringUtils.isNotBlank(empresaDto.getFechaFin())) {
            empresa.setFechaFin(LocalDate.parse(empresaDto.getFechaFin()));
        }
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

        if(empresaDto.getUsuario() != null) {
            Usuario usuarioEmpresa = new Usuario();
            usuarioEmpresa.setEmpresa(empresaCreada.getId());
            usuarioEmpresa.setNombres(empresaDto.getUsuario().getNombres());
            usuarioEmpresa.setApellidos(empresaDto.getUsuario().getApellidos());
            usuarioEmpresa.setApellidoMaterno(empresaDto.getUsuario().getApellidoMaterno());
            usuarioEmpresa.setEmail(empresaDto.getUsuario().getEmail());
            usuarioEmpresa.setUsername(empresaDto.getUsuario().getUsername());
            usuarioEmpresa.setPassword(empresaDto.getUsuario().getPassword());
            usuarioEmpresa.setRol(RolTypeEnum.ENTERPRISE_USER);

            daoHelper.fulfillAuditorFields(true, usuarioEmpresa, usuario.getId());

            usuarioRepository.save(usuarioEmpresa);
        }

        List<EmpresaModalidad> createdModalidades = empresaModalidadRepository.saveAll(empresaModalidades);

        /*try {
            notificacionEmailService.enviarEmail(NotificacionEmailEnum.EMPRESA_REGISTRADA, empresaDto, usuario, empresaDto.getUsuario());
        } catch(MessagingException mex) {
            logger.warn("El correo no se ha podido enviar. Motivo: {}", mex);
        }*/

        EmpresaDto response = daoToDtoConverter.convertDaoToDtoEmpresa(empresaCreada);
        response.setModalidades(createdModalidades.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaModalidad).collect(Collectors.toList()));

        return response;
    }

    @Transactional
    @Override
    public EmpresaDto modificarEmpresa(EmpresaDto empresaDto, String username, String uuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(uuid) || empresaDto == null) {
            logger.warn("La empresa a modificar, el uuid o el usuario vienen como nulos o vacios.");
            throw new InvalidDataException();
        }

        logger.info("Modificando la empresa con el uuid [{}]", uuid);

        Usuario usuario = usuarioRepository.getUsuarioByEmail(username);
        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);
        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        Usuario usuarioEmpresa = usuarioRepository.getUsuarioByEmpresaAndEliminadoFalse(empresa.getId());
        if(usuarioEmpresa == null) {
            logger.warn("El usuario registrado a la empresa en la base de datos no existe");
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
        empresa.setObservaciones(empresaDto.getObservaciones());
        empresa.setRegistro(empresaDto.getRegistro());

        if(empresa.getRegistro() != usuarioEmpresa.getUsername()) {
            usuarioEmpresa.setUsername(empresaDto.getRegistro());
            daoHelper.fulfillAuditorFields(false, usuarioEmpresa, usuario.getId());
            usuarioRepository.save(usuarioEmpresa);
        }

        daoHelper.fulfillAuditorFields(false, empresa, usuario.getId());
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
