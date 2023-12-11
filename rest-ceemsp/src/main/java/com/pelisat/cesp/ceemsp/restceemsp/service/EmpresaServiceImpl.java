package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteEmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.NextRegisterDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.AlreadyExistsUserException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.DuplicatedEnterpriseException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.services.EmailService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final EmailService emailService;
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;
    private final ArchivosService archivosService;
    private final PublicService publicService;

    private final ValidacionService validacionService;
    private final NotificacionRepository notificacionRepository;

    @Autowired
    public EmpresaServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaModalidadRepository empresaModalidadRepository,
                              DaoHelper<CommonModel> daoHelper, NotificacionEmailService notificacionEmailService,
                              EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository, EmailService emailService,
                              ArchivosService archivosService, PublicService publicService, ValidacionService validacionService,
                              NotificacionRepository notificacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaModalidadRepository = empresaModalidadRepository;
        this.daoHelper = daoHelper;
        this.notificacionEmailService = notificacionEmailService;
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
        this.emailService = emailService;
        this.archivosService = archivosService;
        this.publicService = publicService;
        this.validacionService = validacionService;
        this.notificacionRepository = notificacionRepository;
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
    public List<EmpresaDto> obtenerPorStatus(EmpresaStatusEnum status) {
        List<Empresa> empresas = empresaRepository.getAllByStatus(status);
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
    public File obtenerLogo(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el logo para la empresa con el uuid [{}]", uuid);

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(empresa.getRutaLogo());
    }

    @Override
    public File obtenerDocumentoRegistroFederal(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el logo para la empresa con el uuid [{}]", uuid);

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(empresa.getTipoTramite() != TipoTramiteEnum.EAFJAL) {
            logger.warn("El tipo de tramite no es compatible. Se espera EAFJAL pero esta empresa es [{}]", empresa.getTipoTramite());
            throw new NotFoundResourceException();
        }

        return new File(empresa.getRutaRegistroFederal());
    }

    @Override
    @Transactional
    public EmpresaDto crearEmpresa(EmpresaDto empresaDto, String username, MultipartFile multipartFile, MultipartFile logo) throws Exception {
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

        Usuario existeUsuario = usuarioRepository.getUsuarioByEmailAndEliminadoFalse(empresaDto.getUsuario().getEmail());
        if(existeUsuario != null) {
            logger.warn("El usuario ya se encuentra registrado con este correo electronico");
            throw new AlreadyExistsUserException();
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

        if(logo != null) {
            logger.info("Se subio un logo con la empresa. Creando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(logo, TipoArchivoEnum.LOGO_EMPRESA, empresa.getUuid());
                empresa.setRutaLogo(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se pudo guardar el logo. {}", ex);
                throw new InvalidDataException();
            }
        }

        if(multipartFile != null && empresa.getTipoTramite() == TipoTramiteEnum.EAFJAL) {
            logger.info("Se subio el documento de registro federal. Creando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.EMPRESA_REGISTRO_FEDERAL, empresa.getUuid());
                empresa.setRutaRegistroFederal(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el documento de registro federal. {}", ex);
                throw new InvalidDataException();
            }
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
                    if(empresaCreada.getTipoTramite() == TipoTramiteEnum.EAFJAL) {
                        empresaModalidad.setNumeroRegistroFederal(empresaCreada.getRegistroFederal());
                        empresaModalidad.setFechaInicio(empresaCreada.getFechaInicio());
                        empresaModalidad.setFechaFin(empresaCreada.getFechaFin());
                    }

                    return empresaModalidad;
                }).collect(Collectors.toList());

        if(empresaDto.getUsuario() != null) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(empresaDto.getUsuario().getPassword().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < hash.length; i++) {
                sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }

            Usuario usuarioEmpresa = new Usuario();
            usuarioEmpresa.setEmpresa(empresaCreada.getId());
            usuarioEmpresa.setNombres(empresaDto.getUsuario().getNombres());
            usuarioEmpresa.setApellidos(empresaDto.getUsuario().getApellidos());
            usuarioEmpresa.setApellidoMaterno(empresaDto.getUsuario().getApellidoMaterno());
            usuarioEmpresa.setEmail(empresaDto.getUsuario().getEmail());
            usuarioEmpresa.setUsername(empresaDto.getUsuario().getUsername());
            usuarioEmpresa.setPassword(sb.toString());
            usuarioEmpresa.setRol(RolTypeEnum.ENTERPRISE_USER);

            daoHelper.fulfillAuditorFields(true, usuarioEmpresa, usuario.getId());

            usuarioRepository.save(usuarioEmpresa);
        }

        List<EmpresaModalidad> createdModalidades = empresaModalidadRepository.saveAll(empresaModalidades);

        try {
            Notificacion notificacion = new Notificacion();
            notificacion.setTipo(NotificacionEmailEnum.EMPRESA_REGISTRADA);
            notificacion.setEmpresa(empresa.getId());
            notificacion.setNumero("CESP/DSSP/AVISO/" + publicService.buscarProximoNumeroNotificacion() + "/" + LocalDate.now().getYear());
            String cadenaOriginal = generarCadenaOriginalNotificacion(notificacion, empresa);
            notificacion.setCadenaOriginal(cadenaOriginal);
            notificacion.setSelloSalt(RandomStringUtils.randomAlphanumeric(10));

            // Generando el sello del acuse
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(notificacion.getSelloSalt().getBytes(StandardCharsets.UTF_8));
            byte[] bytes = digest.digest(cadenaOriginal.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            notificacion.setSello(sb.toString());
            daoHelper.fulfillAuditorFields(true, notificacion, usuario.getId());
            Notificacion notificacionCreada = notificacionRepository.save(notificacion);

            Usuario usuarioAEnviar = new Usuario();
            usuarioAEnviar.setEmail(empresaDto.getUsuario().getEmail());
            usuarioAEnviar.setPassword(empresaDto.getUsuario().getPassword());

            Map<String, CommonModel> mapaCorreo = new HashMap<>();
            mapaCorreo.put("empresa", empresaCreada);
            mapaCorreo.put("notificacion", notificacionCreada);
            mapaCorreo.put("usuario", usuarioAEnviar);

            emailService.sendEmail(NotificacionEmailEnum.EMPRESA_REGISTRADA, empresaDto.getCorreoElectronico(), mapaCorreo);
        } catch(Exception mex) {
            logger.warn("El correo no se ha podido enviar. Motivo: {}", mex);
        }

        EmpresaDto response = daoToDtoConverter.convertDaoToDtoEmpresa(empresaCreada);
        response.setModalidades(createdModalidades.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaModalidad).collect(Collectors.toList()));

        return response;
    }

    @Transactional
    @Override
    public EmpresaDto modificarEmpresa(EmpresaDto empresaDto, String username, String uuid, MultipartFile acuerdo, MultipartFile logo) {
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

        // TODO: Validaciones para RFC, CURP y registro si estos han cambiado
        if(!StringUtils.equals(empresa.getCurp(), empresaDto.getCurp()) || !StringUtils.equals(empresa.getRfc(), empresaDto.getRfc()) || !StringUtils.equals(empresa.getRegistro(), empresaDto.getRegistro())) {
            ExisteEmpresaDto existeEmpresaDto = new ExisteEmpresaDto();

            if(!StringUtils.equals(empresa.getCurp(), empresaDto.getCurp())) {
                existeEmpresaDto.setCurp(empresaDto.getCurp());
            }
            if(!StringUtils.equals(empresa.getRfc(), empresaDto.getRfc())) {
                existeEmpresaDto.setRfc(empresaDto.getRfc());
            }
            if(!StringUtils.equals(empresa.getRegistro(), empresaDto.getRegistro())) {
                existeEmpresaDto.setRegistro(empresaDto.getRegistro());
            }

            ExisteEmpresaDto validacionEmpresa = validacionService.buscarExistenciaEmpresa(existeEmpresaDto);

            if(validacionEmpresa.getExiste()) {
                logger.warn("La empresa parece estar duplicada por alguno de los parametros");
                throw new DuplicatedEnterpriseException();
            }
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

        if(empresa.getTipoTramite() == TipoTramiteEnum.EAFJAL) {
            if(!StringUtils.equals(empresa.getRegistroFederal(), empresaDto.getRegistroFederal()) ||
                    !StringUtils.equals(empresa.getFechaInicio().toString(),  empresaDto.getFechaInicio()) ||
                    !StringUtils.equals(empresa.getFechaFin().toString(), empresaDto.getFechaFin())) {
                logger.info("Los campos de registro federal han sido actualizados");
                empresa.setRegistroFederal(empresaDto.getRegistroFederal());
                empresa.setFechaInicio(LocalDate.parse(empresaDto.getFechaInicio()));
                empresa.setFechaFin(LocalDate.parse(empresaDto.getFechaFin()));
                empresaModalidadRepository.findAllByEmpresaAndEliminadoFalse(empresa.getId()).forEach(emr -> {
                    emr.setNumeroRegistroFederal(empresaDto.getRegistroFederal());
                    emr.setFechaInicio(LocalDate.parse(empresaDto.getFechaInicio()));
                    emr.setFechaFin(LocalDate.parse(empresaDto.getFechaFin()));
                    empresaModalidadRepository.save(emr);
                });
            }
        }

        if(logo != null) {
            logger.info("Se subio un logotipo. Eliminando y modificando");
            if(StringUtils.isNotBlank(empresa.getRutaLogo())) {
                archivosService.eliminarArchivo(empresa.getRutaLogo());
            }

            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(logo, TipoArchivoEnum.LOGO_EMPRESA, uuid);
                empresa.setRutaLogo(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el logotipo. {}", ex);
                throw new InvalidDataException();
            }
        }

        if(acuerdo != null && empresa.getTipoTramite() == TipoTramiteEnum.EAFJAL) {
            logger.info("Se subio el documento de registro federal. Eliminando y subiendo");
            archivosService.eliminarArchivo(empresa.getRutaRegistroFederal());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(acuerdo, TipoArchivoEnum.EMPRESA_REGISTRO_FEDERAL, uuid);
                empresa.setRutaRegistroFederal(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se pudo actualizar el documento de registro federal", ex);
                throw new InvalidDataException();
            }
        }

        daoHelper.fulfillAuditorFields(false, empresa, usuario.getId());
        empresaRepository.save(empresa);

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresa);
    }

    @Override
    @Transactional
    public void cambiarTipoTramiteEmpresa(EmpresaDto empresaDto, TipoTramiteEnum tramiteOrigen, TipoTramiteEnum tramiteDestino, String username) {
        if(empresaDto == null || tramiteOrigen == null || tramiteDestino == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        if(tramiteOrigen != TipoTramiteEnum.AP) {
            logger.warn("Esta funcion no es compatible con este tipo de tramite. Se esperaba AP");
            throw new InvalidDataException();
        }

        if(tramiteDestino != TipoTramiteEnum.SPSMD) {
            logger.warn("Esta funcion no es compatible con este tipo de tramite. Se esperaba SPSMD");
            throw new InvalidDataException();
        }

        Usuario usuario = usuarioRepository.getUsuarioByEmail(username);
        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaDto.getUuid());
        if(empresa == null) {
            logger.warn("La empresa no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        Usuario usuarioEmpresa = usuarioRepository.getUsuarioByEmpresaAndEliminadoFalse(empresa.getId());
        if(usuarioEmpresa == null) {
            logger.warn("La empresa no tiene usuario registrado");
            throw new NotFoundResourceException();
        }

        NextRegisterDto nextRegisterDto = new NextRegisterDto();
        nextRegisterDto.setTipo(tramiteDestino);
        NextRegisterDto siguienteRegistro = publicService.findNextRegister(nextRegisterDto);
        empresa.setTipoTramite(tramiteDestino);
        empresa.setRegistro("CESP/SPSMD/" + siguienteRegistro.getNumeroSiguiente() + "/" + LocalDate.now().getYear());

        usuarioEmpresa.setUsername("CESP/SPSMD/" + siguienteRegistro.getNumeroSiguiente() + "/" + LocalDate.now().getYear());

        daoHelper.fulfillAuditorFields(false, empresa, usuario.getId());
        daoHelper.fulfillAuditorFields(false, usuarioEmpresa, usuario.getId());
        empresaRepository.save(empresa);
        usuarioRepository.save(usuarioEmpresa);

        List<EmpresaModalidad> modalidades = empresaModalidadRepository.findAllByEmpresaAndEliminadoFalse(empresa.getId());
        modalidades.forEach(m -> {
            if(m.getModalidad() == 14) { // Proteccion y vigilancia
                m.setModalidad(1);
                if(m.getSubmodalidad() == 8) {
                    m.setSubmodalidad(1);
                } else if(m.getSubmodalidad() == 9) {
                    m.setSubmodalidad(2);
                }
            } else if(m.getModalidad() == 15) { // Custodia y vigilancia
                m.setModalidad(2);
            } else if(m.getModalidad() == 16) { // Traslado y proteccion
                m.setModalidad(3);
            } else if(m.getModalidad() == 17) { // Instalacion de blindajes
                m.setModalidad(4);
            } else if(m.getModalidad() == 18) { // Sistemas de alarmasaaa
                m.setModalidad(5);
            }
            daoHelper.fulfillAuditorFields(false, empresa, usuario.getId());
            empresaModalidadRepository.save(m);
        });
    }

    @Override
    public void cambiarVigenciaEmpresa(EmpresaDto empresaDto, LocalDate fechaInicio, LocalDate fechaFin, String username) {
        if(empresaDto == null || StringUtils.isBlank(username) || fechaFin == null || fechaInicio == null) {
            logger.warn("El objeto, el usuario o el uuid vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        Usuario usuario = usuarioRepository.getUsuarioByEmail(username);
        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaDto.getUuid());
        if(empresa == null) {
            logger.warn("La empresa no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        empresa.setVigenciaInicio(fechaInicio);
        empresa.setVigenciaFin(fechaFin);
        daoHelper.fulfillAuditorFields(false, empresa, usuario.getId());
        empresaRepository.save(empresa);
    }

    @Transactional
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

        Usuario usuario = usuarioRepository.getUsuarioByEmail(username);

        empresa.setStatus(empresaDto.getStatus());
        empresa.setObservaciones(empresaDto.getObservaciones());
        daoHelper.fulfillAuditorFields(false, empresa, usuario.getId());

        Empresa empresaNuevoStatus = empresaRepository.save(empresa);

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresaNuevoStatus);
    }

    @Override
    public EmpresaDto eliminarEmpresa(String username, String uuid) {
        return null;
    }

    private String generarCadenaOriginalNotificacion(Notificacion notificacion, Empresa empresa) {
        return "||" +
                empresa.getId() + "|" +
                empresa.getUuid() + "|" +
                empresa.getRfc() + "|" +
                empresa.getRegistro() + "|" +
                notificacion.getUuid() + "|" +
                notificacion.getNumero() + "|" +
                notificacion.getTipo() + "||";
    }
}
