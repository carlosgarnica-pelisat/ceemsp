package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.dto.metadata.IncidenciaArchivoMetadata;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.AlreadyActiveException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.UnsupportedEnvironmentException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.services.EmailService;
import com.pelisat.cesp.ceemsp.infrastructure.services.QRCodeService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IncidenciaServiceImpl implements IncidenciaService {

    private static final int MAX_NUMEROS = 10;

    private final Logger logger = LoggerFactory.getLogger(IncidenciaService.class);
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final EmpresaRepository empresaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final IncidenciaRepository incidenciaRepository;
    private final IncidenciaArmaRepository incidenciaArmaRepository;
    private final IncidenciaArchivoRepository incidenciaArchivoRepository;
    private final IncidenciaCanRepository incidenciaCanRepository;
    private final IncidenciaVehiculoRepository incidenciaVehiculoRepository;
    private final IncidenciaPersonaRepository incidenciaPersonaRepository;
    private final IncidenciaComentarioRepository incidenciaComentarioRepository;
    private final CanService canService;
    private final ArmaService armaService;
    private final EmpresaVehiculoService empresaVehiculoService;
    private final ClienteService clienteService;
    private final ClienteDomicilioService clienteDomicilioService;
    private final PersonaService personaService;
    private final ArchivosService archivosService;
    private final ArmaRepository armaRepository;
    private final EmailService emailService;
    private final AcuseRepository acuseRepository;
    private final PublicService publicService;
    private final NotificacionRepository notificacionRepository;
    private final PersonaRepository personaRepository;
    private final PersonalArmaRepository personalArmaRepository;
    private final QRCodeService qrCodeService;
    private final NotificacionArgosRepository notificacionArgosRepository;
    private final NotificacionEmpresaRepository notificacionEmpresaRepository;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    public IncidenciaServiceImpl(UsuarioService usuarioService, EmpresaService empresaService, DaoToDtoConverter daoToDtoConverter,
                                 DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, IncidenciaRepository incidenciaRepository,
                                 IncidenciaArmaRepository incidenciaArmaRepository, IncidenciaArchivoRepository incidenciaArchivoRepository,
                                 IncidenciaCanRepository incidenciaCanRepository, IncidenciaVehiculoRepository incidenciaVehiculoRepository,
                                 IncidenciaComentarioRepository incidenciaComentarioRepository, CanService canService,
                                 ArmaService armaService, EmpresaVehiculoService empresaVehiculoService, ClienteService clienteService,
                                 IncidenciaPersonaRepository incidenciaPersonaRepository, PersonaService personaService,
                                 ArchivosService archivosService, ArmaRepository armaRepository, EmailService emailService,
                                 EmpresaRepository empresaRepository, ClienteDomicilioService clienteDomicilioService,
                                 AcuseRepository acuseRepository, PublicService publicService, NotificacionRepository notificacionRepository,
                                 PersonaRepository personaRepository, PersonalArmaRepository personalArmaRepository, QRCodeService qrCodeService,
                                 NotificacionArgosRepository notificacionArgosRepository, NotificacionEmpresaRepository notificacionEmpresaRepository) {
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.incidenciaRepository = incidenciaRepository;
        this.incidenciaArmaRepository = incidenciaArmaRepository;
        this.incidenciaArchivoRepository = incidenciaArchivoRepository;
        this.incidenciaCanRepository = incidenciaCanRepository;
        this.incidenciaVehiculoRepository = incidenciaVehiculoRepository;
        this.incidenciaComentarioRepository = incidenciaComentarioRepository;
        this.incidenciaPersonaRepository = incidenciaPersonaRepository;
        this.canService = canService;
        this.armaService = armaService;
        this.empresaVehiculoService = empresaVehiculoService;
        this.clienteService = clienteService;
        this.personaService = personaService;
        this.archivosService = archivosService;
        this.armaRepository = armaRepository;
        this.emailService = emailService;
        this.empresaRepository = empresaRepository;
        this.clienteDomicilioService = clienteDomicilioService;
        this.acuseRepository = acuseRepository;
        this.publicService = publicService;
        this.notificacionRepository = notificacionRepository;
        this.personaRepository = personaRepository;
        this.personalArmaRepository = personalArmaRepository;
        this.qrCodeService = qrCodeService;
        this.notificacionArgosRepository = notificacionArgosRepository;
        this.notificacionEmpresaRepository = notificacionEmpresaRepository;
    }

    @Override
    public List<IncidenciaDto> obtenerIncidenciasPorEmpresa(String uuidEmpresa) {
        if(StringUtils.isBlank(uuidEmpresa)) {
            logger.warn("El uuid esta como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las incidencias con el uuid de la empresa [{}]", uuidEmpresa);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuidEmpresa);
        List<Incidencia> incidencias = incidenciaRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        List<IncidenciaDto> response = incidencias.stream().map(incidencia -> {
            IncidenciaDto incidenciaDto = daoToDtoConverter.convertDaoToDtoIncidencia(incidencia);
            if(incidencia.getAsignado() != null) {
                incidenciaDto.setAsignado(usuarioService.getUserById(incidencia.getAsignado()));
            }
            return incidenciaDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public IncidenciaDto obtenerIncidenciaPorUuid(String empresaUuid, String incidenciaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid)) {
            logger.warn("El uuid de la empresa o de la incidencia vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los detalles de la incidencia con el uuid [{}]", empresaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        // Obteniendo cada entidad involucrada dentro de la incidencia
        IncidenciaDto incidenciaDto = daoToDtoConverter.convertDaoToDtoIncidencia(incidencia);

        if(incidencia.getAsignado() != null && incidencia.getAsignado() > 0) {
            incidenciaDto.setAsignado(usuarioService.getUserById(incidencia.getAsignado()));
        }

        if(incidencia.getCliente() != null && incidencia.getCliente() > 0) {
            incidenciaDto.setCliente(clienteService.obtenerClientePorId(incidencia.getCliente()));
        }

        if(incidencia.getClienteDomicilio() != null && incidencia.getClienteDomicilio() > 0) {
            incidenciaDto.setClienteDomicilio(clienteDomicilioService.obtenerPorId(incidencia.getClienteDomicilio()));
        }

        List<IncidenciaComentario> incidenciaComentarios = incidenciaComentarioRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());
        List<IncidenciaPersona> incidendiaPersonas = incidenciaPersonaRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());
        List<IncidenciaArma> incidenciaArmas = incidenciaArmaRepository.getAllByIncidencia(incidencia.getId());
        List<IncidenciaVehiculo> incidenciaVehiculos = incidenciaVehiculoRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());
        List<IncidenciaCan> incidenciaCanes = incidenciaCanRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());
        List<IncidenciaArchivo> incidenciaArchivos = incidenciaArchivoRepository.getAllByIncidenciaAndEliminadoFalse(incidencia.getId());

        // realizando el parsing de cada uno de los elementos por medio del servicio y despues, agregarlos
        incidenciaDto.setComentarios(incidenciaComentarios.stream().map(c -> {
            IncidenciaComentarioDto incidenciaComentarioDto = new IncidenciaComentarioDto();
            incidenciaComentarioDto.setId(c.getId());
            incidenciaComentarioDto.setUuid(c.getUuid());
            incidenciaComentarioDto.setComentario(c.getComentario());
            incidenciaComentarioDto.setFecha(c.getFechaCreacion().toString());
            incidenciaComentarioDto.setUsuario(usuarioService.getUserById(c.getCreadoPor()));
            return incidenciaComentarioDto;
        }).collect(Collectors.toList()));

        incidenciaDto.setPersonasInvolucradas(incidendiaPersonas.stream().map(p -> {
            PersonaDto personaDto = personaService.obtenerPorId(p.getPersona());
            personaDto.setEliminadoIncidencia(p.getEliminado());
            personaDto.setFechaCreacionIncidencia(p.getFechaCreacion().toString());
            if(p.getEliminado()) {
                personaDto.setFechaEliminacionIncidencia(p.getFechaActualizacion().toString());
            }
            return personaDto;
        }).collect(Collectors.toList()));

        incidenciaDto.setArmasInvolucradas(incidenciaArmas.stream().map(a -> {
            ArmaDto arma = armaService.obtenerArmaPorId(empresaUuid, a.getArma());
            arma.setEliminadoIncidencia(a.getEliminado());
            arma.setFechaCreacionIncidencia(a.getFechaCreacion().toString());
            arma.setRazonBajaIncidencia(a.getMotivoEliminacion());
            if(a.getEliminado()) {
                arma.setFechaEliminacionIncidencia(a.getFechaActualizacion().toString());
            }
            return arma;
        }).collect(Collectors.toList()));

        incidenciaDto.setCanesInvolucrados(incidenciaCanes.stream().map(c -> {
            CanDto canDto = canService.obtenerCanPorId(c.getCan());
            canDto.setEliminadoIncidencia(c.getEliminado());
            canDto.setFechaCreacionIncidencia(c.getFechaCreacion().toString());
            if(c.getEliminado()) {
                canDto.setFechaEliminacionIncidencia(c.getFechaActualizacion().toString());
            }
            return canDto;
        }).collect(Collectors.toList()));

        incidenciaDto.setVehiculosInvolucrados(incidenciaVehiculos.stream().map(v -> {
            VehiculoDto vehiculoDto = empresaVehiculoService.obtenerVehiculoPorId(empresaUuid, v.getVehiculo());
            vehiculoDto.setEliminadoIncidencia(v.getEliminado());
            vehiculoDto.setFechaCreacionIncidencia(v.getFechaCreacion().toString());
            if(v.getEliminado()) {
                vehiculoDto.setFechaEliminacionIncidencia(v.getFechaActualizacion().toString());
            }
            return vehiculoDto;
        }).collect(Collectors.toList()));

        incidenciaDto.setArchivos(incidenciaArchivos.stream().map(a -> {
            IncidenciaArchivoMetadata incidenciaArchivoMetadata = new IncidenciaArchivoMetadata();
            incidenciaArchivoMetadata.setNombreArchivo(a.getRutaArchivo());
            incidenciaArchivoMetadata.setId(a.getId());
            incidenciaArchivoMetadata.setUuid(a.getUuid());
            incidenciaArchivoMetadata.setFechaCreacion(a.getFechaCreacion().toString());
            incidenciaArchivoMetadata.setFechaActualizacion(a.getFechaActualizacion().toString());
            String[] tokens = a.getRutaArchivo().split("[\\\\|/]");
            incidenciaArchivoMetadata.setNombreArchivo(tokens[tokens.length - 1]);
            return incidenciaArchivoMetadata;
        }).collect(Collectors.toList()));
        return incidenciaDto;
    }

    @Transactional
    @Override
    public IncidenciaDto guardarIncidencia(String empresaUuid, String username, IncidenciaDto incidenciaDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(username) || incidenciaDto == null) {
            logger.warn("El uuid, el usuario o la incidencia vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nueva incidendia");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);

        Incidencia incidencia = new Incidencia();
        incidencia.setNumero(RandomStringUtils.randomNumeric(MAX_NUMEROS));
        incidencia.setFechaIncidencia(LocalDate.parse(incidenciaDto.getFechaIncidencia()));
        incidencia.setStatus(IncidenciaStatusEnum.ABIERTA);

        if(incidenciaDto.isRelevancia()) {
            incidencia.setRelevancia(true);

            if(incidenciaDto.getCliente() != null) {
                incidencia.setCliente(incidenciaDto.getCliente().getId());
            }

            if(incidenciaDto.getClienteDomicilio() != null) {
                incidencia.setClienteDomicilio(incidenciaDto.getClienteDomicilio().getId());
            }

            incidencia.setLatitud(incidenciaDto.getLatitud());
            incidencia.setLongitud(incidenciaDto.getLongitud());
            incidencia.setEmpresa(empresa.getId());
            daoHelper.fulfillAuditorFields(true, incidencia, usuarioDto.getId());
        } else {
            incidencia.setRelevancia(false);
        }

        Incidencia incidenciaCreada = incidenciaRepository.save(incidencia);

        if(incidenciaDto.getPersonasInvolucradas() != null && incidenciaDto.getPersonasInvolucradas().size() > 0) {
            logger.info("Hay personas involucradas");
            List<IncidenciaPersona> personas = incidenciaDto.getPersonasInvolucradas().stream().map(persona -> {
                IncidenciaPersona incidenciaPersona = new IncidenciaPersona();
                incidenciaPersona.setUuid(RandomStringUtils.randomAlphanumeric(12));
                incidenciaPersona.setIncidencia(incidenciaCreada.getId());
                incidenciaPersona.setPersona(persona.getId());
                daoHelper.fulfillAuditorFields(true, incidenciaPersona, usuarioDto.getId());
                return incidenciaPersona;
            }).collect(Collectors.toList());
            incidenciaPersonaRepository.saveAll(personas);
        }

        if(incidenciaDto.getCanesInvolucrados() != null && incidenciaDto.getCanesInvolucrados().size() > 0) {
            logger.info("Hay canes involucrados");
            List<IncidenciaCan> canes = incidenciaDto.getCanesInvolucrados().stream().map(can -> {
                IncidenciaCan incidenciaCan = new IncidenciaCan();
                incidenciaCan.setUuid(RandomStringUtils.randomAlphanumeric(12));
                incidenciaCan.setIncidencia(incidenciaCreada.getId());
                incidenciaCan.setCan(can.getId());
                daoHelper.fulfillAuditorFields(true, incidenciaCan, usuarioDto.getId());
                return incidenciaCan;
            }).collect(Collectors.toList());
            incidenciaCanRepository.saveAll(canes);
        }

        if(incidenciaDto.getArmasInvolucradas() != null && incidenciaDto.getArmasInvolucradas().size() > 0) {
            logger.info("Hay armas involucradas");
            List<IncidenciaArma> armas = incidenciaDto.getArmasInvolucradas().stream().map(armaDto -> {
                Arma arma = armaRepository.getOne(armaDto.getId());

                if(arma == null) {
                    logger.warn("El arma no existe en la base de datos");
                    throw new InvalidDataException();
                }

                if(arma.getStatus() == ArmaStatusEnum.ASIGNADA) {
                    Personal personaConArma = null;
                    if(arma.getTipo() == ArmaTipoEnum.CORTA) {
                        personaConArma = personaRepository.getByArmaCortaAndEliminadoFalse(arma.getId());
                        personaConArma.setArmaCorta(null);
                    } else if(arma.getTipo() == ArmaTipoEnum.LARGA) {
                        personaConArma = personaRepository.getByArmaLargaAndEliminadoFalse(arma.getId());
                        personaConArma.setArmaLarga(null);
                    }

                    List<PersonalArma> personalArmas = personalArmaRepository.getAllByPersonalAndEliminadoFalse(personaConArma.getId());
                    PersonalArma armaEncontrada = null;

                    for(PersonalArma pa : personalArmas) {
                        Arma armaTemporal = armaRepository.getOne(pa.getArma());
                        if(armaTemporal.getTipo() == armaDto.getTipo()) {
                            armaEncontrada = pa;
                        }
                    }

                    if(armaEncontrada != null) {
                        armaEncontrada.setEliminado(true);
                        armaEncontrada.setMotivoBajaAsignacion("Esta arma se ha desasignado al elemento por involucracion en la incidencia: " + incidencia.getNumero() + " registrada a las " + LocalDate.now());
                        daoHelper.fulfillAuditorFields(true, armaEncontrada, usuarioDto.getId());
                        personalArmaRepository.save(armaEncontrada);
                    }

                    daoHelper.fulfillAuditorFields(false, personaConArma, usuarioDto.getId());
                    personaRepository.save(personaConArma);
                }

                arma.setStatus(armaDto.getStatus());
                arma.setEliminado(true);
                arma.setMotivoBaja(armaDto.getStatus().toString());
                arma.setFechaBaja(LocalDate.now());
                arma.setObservacionesBaja("Se ha eliminado el arma temporalmente con status " + armaDto.getStatus().toString() + " por el reporte de incidencia " + incidencia.getNumero() + " registrada a las " + LocalDate.now());
                daoHelper.fulfillAuditorFields(false, arma, usuarioDto.getId());
                armaRepository.save(arma);

                IncidenciaArma incidenciaArma = new IncidenciaArma();
                incidenciaArma.setUuid(RandomStringUtils.randomAlphanumeric(12));
                incidenciaArma.setIncidencia(incidenciaCreada.getId());
                incidenciaArma.setArma(armaDto.getId());
                incidenciaArma.setStatus(armaDto.getStatus());
                daoHelper.fulfillAuditorFields(true, incidenciaArma, usuarioDto.getId());
                return incidenciaArma;
            }).collect(Collectors.toList());
            incidenciaArmaRepository.saveAll(armas);
        }

        if(incidenciaDto.getVehiculosInvolucrados() != null && incidenciaDto.getVehiculosInvolucrados().size() > 0) {
            logger.info("Hay vehiculos involucradas");
            List<IncidenciaVehiculo> vehiculos = incidenciaDto.getVehiculosInvolucrados().stream().map(vehiculoDto -> {
                IncidenciaVehiculo incidenciaVehiculo = new IncidenciaVehiculo();
                incidenciaVehiculo.setIncidencia(incidenciaCreada.getId());
                incidenciaVehiculo.setVehiculo(vehiculoDto.getId());
                daoHelper.fulfillAuditorFields(true, incidenciaVehiculo, usuarioDto.getId());
                return incidenciaVehiculo;
            }).collect(Collectors.toList());
            incidenciaVehiculoRepository.saveAll(vehiculos);
        }

        if(incidenciaDto.getComentarios() != null && incidenciaDto.getComentarios().size() > 0) {
            logger.info("Hay comentarios");
            List<IncidenciaComentario> comentarios = incidenciaDto.getComentarios().stream().map(incidenciaComentario -> {
                IncidenciaComentario comentario = new IncidenciaComentario();
                comentario.setComentario(incidenciaComentario.getComentario());
                comentario.setIncidencia(incidenciaCreada.getId());
                daoHelper.fulfillAuditorFields(true, comentario, usuarioDto.getId());
                return comentario;
            }).collect(Collectors.toList());
            incidenciaComentarioRepository.saveAll(comentarios);
        }

        if(multipartFile != null) {
            logger.info("Hay archivo");
            IncidenciaArchivo incidenciaArchivo = new IncidenciaArchivo();
            incidenciaArchivo.setIncidencia(incidenciaCreada.getId());
            daoHelper.fulfillAuditorFields(true, incidenciaArchivo, usuarioDto.getId());
            try {
                String ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_INCIDENCIA, empresaUuid);
                incidenciaArchivo.setRutaArchivo(ruta);
                incidenciaArchivoRepository.save(incidenciaArchivo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo.", ex);
            }
        }

        try {
            NotificacionArgos notificacionArgos = new NotificacionArgos();
            notificacionArgos.setUuid(RandomStringUtils.randomAlphanumeric(12));
            notificacionArgos.setTipo(NotificacionArgosTipoEnum.GENERAL);
            notificacionArgos.setMotivo("Nueva incidencia creada");
            notificacionArgos.setDescripcion("La empresa " + empresa.getRazonSocial() + " ha creado la incidencia " + incidenciaCreada.getNumero());
            notificacionArgos.setUbicacion("/home/empresas/" + empresa.getUuid() + "/incidencias?uuid=" + incidencia.getUuid());
            daoHelper.fulfillAuditorFields(true, notificacionArgos, usuarioDto.getId());
            notificacionArgosRepository.save(notificacionArgos);

            Notificacion notificacion = new Notificacion();
            notificacion.setTipo(NotificacionEmailEnum.NUEVA_INCIDENCIA);
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
            daoHelper.fulfillAuditorFields(true, notificacion, usuarioDto.getId());
            Notificacion notificacionCreada = notificacionRepository.save(notificacion);
            Map<String, CommonModel> mapaCorreo = new HashMap<>();
            mapaCorreo.put("empresa", empresa);
            mapaCorreo.put("notificacion", notificacionCreada);
            emailService.sendEmail(NotificacionEmailEnum.NUEVA_INCIDENCIA, empresa.getCorreoElectronico(), mapaCorreo);
        } catch(Exception mex) {
            logger.warn("El correo no se ha podido enviar. Motivo: {}", mex);
        }

        return daoToDtoConverter.convertDaoToDtoIncidencia(incidenciaCreada);
    }

    @Override
    @Transactional
    public IncidenciaDto autoasignarIncidencia(String empresaUuid, String incidenciaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros no es valido");
            throw new InvalidDataException();
        }

        logger.info("Autoasignando la incidencia al usuario [{}]", username);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        incidencia.setStatus(IncidenciaStatusEnum.ASIGNADA);
        incidencia.setAsignado(usuarioDto.getId());
        incidencia.setFechaActualizacion(LocalDateTime.now());
        incidencia.setActualizadoPor(usuarioDto.getId());
        incidenciaRepository.save(incidencia);

        return daoToDtoConverter.convertDaoToDtoIncidencia(incidencia);
    }

    @Override
    @Transactional
    public IncidenciaDto asignarIncidencia(String empresaUuid, String incidenciaUuid, UsuarioDto usuarioDto, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || usuarioDto == null) {
            logger.warn("Alguno de los parametros no es valido");
            throw new InvalidDataException();
        }

        logger.info("Autoasignando la incidencia al usuario [{}]", username);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        incidencia.setStatus(IncidenciaStatusEnum.ASIGNADA);
        incidencia.setAsignado(usuarioDto.getId());
        incidencia.setFechaActualizacion(LocalDateTime.now());
        incidencia.setActualizadoPor(usuarioDto.getId());
        incidenciaRepository.save(incidencia);

        return daoToDtoConverter.convertDaoToDtoIncidencia(incidencia);
    }

    @Override
    @Transactional
    public IncidenciaDto agregarComentario(String empresaUuid, String incidenciaUuid, String username, IncidenciaDto incidenciaDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || incidenciaDto == null) {
            logger.warn("Alguno de los parametros no es valido");
            throw new InvalidDataException();
        }

        logger.info("Agregando comentario a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);
        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        incidencia.setStatus(incidenciaDto.getStatus());
        daoHelper.fulfillAuditorFields(false, incidencia, usuarioDto.getId());
        incidenciaRepository.save(incidencia);

        if(incidenciaDto.getComentarios() != null && incidenciaDto.getComentarios().size() > 0) {
            logger.info("Agregando los comentarios");
            List<IncidenciaComentario> comentarios = incidenciaDto.getComentarios().stream().map(incidenciaComentario -> {
                IncidenciaComentario comentario = new IncidenciaComentario();
                comentario.setComentario(incidenciaComentario.getComentario());
                comentario.setIncidencia(incidencia.getId());
                daoHelper.fulfillAuditorFields(true, comentario, usuarioDto.getId());
                return comentario;
            }).collect(Collectors.toList());
            incidenciaComentarioRepository.saveAll(comentarios);
        }

        try {
            NotificacionEmailEnum notificacionEmailEnum = null;
            NotificacionEmpresa notificacionEmpresa = new NotificacionEmpresa();
            notificacionEmpresa.setEmpresa(empresa.getId());
            notificacionEmpresa.setUuid(RandomStringUtils.randomAlphanumeric(12));
            notificacionEmpresa.setTipo(NotificacionEmpresaTipoEnum.INDIVIDUAL);
            notificacionEmpresa.setUbicacion("/home/incidencias?uuid=" + incidencia.getUuid());


            if(incidencia.getStatus() == IncidenciaStatusEnum.ACCION_PENDIENTE) {
                notificacionEmpresa.setMotivo("Incidencia con accion pendiente");
                notificacionEmpresa.setDescripcion("La incidencia " + incidencia.getNumero() + " requiere de tu atencion. Haz click para ver mas detalles ");
                daoHelper.fulfillAuditorFields(true, notificacionEmpresa, usuarioDto.getId());
                notificacionEmpresaRepository.save(notificacionEmpresa);

                notificacionEmailEnum = NotificacionEmailEnum.INCIDENCIA_ACCION_PENDIENTE;
            } else if(incidencia.getStatus() == IncidenciaStatusEnum.PROCEDENTE) {

                notificacionEmailEnum = NotificacionEmailEnum.INCIDENCIA_PROCEDENTE;

                // Generacion acuse incidencia
                Acuse acuse = new Acuse();
                acuse.setTipo(AcuseTipoEnum.ACUSE_RECIBO_INCIDENCIA);
                acuse.setEmpresa(empresa.getId());
                acuse.setNumero("CESP/DSSP/ACUSE/" + publicService.buscarProximoNumeroAcuse() + "/" + LocalDate.now().getYear());
                acuse.setIncidencia(incidencia.getId());
                String cadenaOriginal = generarCadenaOriginalAcuse(acuse, empresa);
                acuse.setCadenaOriginal(cadenaOriginal);
                acuse.setSelloSalt(RandomStringUtils.randomAlphanumeric(10));

                // Generando el sello del acuse
                MessageDigest digest = MessageDigest.getInstance("SHA-512");
                digest.reset();
                digest.update(acuse.getSelloSalt().getBytes(StandardCharsets.UTF_8));
                byte[] bytes = digest.digest(cadenaOriginal.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                for(int i = 0; i < bytes.length; i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                acuse.setSello(sb.toString());
                daoHelper.fulfillAuditorFields(true, acuse, usuarioDto.getId());
                Acuse acuseCreado = acuseRepository.save(acuse);

                // Enviando el acuse de recibo
                String path;
                if(StringUtils.equals("local", activeProfile)) {
                    path = "http://localhost:4250/validar-acuse?sello=";
                } else if (StringUtils.equals("dev", activeProfile)) {
                    path = "https://argos.jalisco.gob.mx/validar-acuse?sello=";
                } else {
                    throw new UnsupportedEnvironmentException();
                }

                Map<String, Object> mapaCorreo = new HashMap<>();
                mapaCorreo.put("empresa", empresa);
                mapaCorreo.put("acuse", acuseCreado);
                mapaCorreo.put("qr", qrCodeService.generarQRAcuseBase64(path + acuseCreado.getSello()));

                emailService.sendEmail(NotificacionEmailEnum.ACUSE_RECIBO_INCIDENCIA, empresa.getCorreoElectronico(), mapaCorreo);

                notificacionEmpresa.setMotivo("Incidencia procedente");
                notificacionEmpresa.setDescripcion("La incidencia " + incidencia.getNumero() + " se ha marcado como procedente. Haz clic para ver mas informacion ");
                daoHelper.fulfillAuditorFields(true, notificacionEmpresa, usuarioDto.getId());
                notificacionEmpresaRepository.save(notificacionEmpresa);
            } else if(incidencia.getStatus() == IncidenciaStatusEnum.IMPROCEDENTE) {
                notificacionEmailEnum = NotificacionEmailEnum.INCIDENCIA_IMPROCEDENTE;

                notificacionEmpresa.setMotivo("Incidencia improcedente");
                notificacionEmpresa.setDescripcion("La incidencia " + incidencia.getNumero() + " se ha marcado como improcedente. Haz clic para ver mas informacion ");
                daoHelper.fulfillAuditorFields(true, notificacionEmpresa, usuarioDto.getId());
                notificacionEmpresaRepository.save(notificacionEmpresa);
            }

            Notificacion notificacion = new Notificacion();
            notificacion.setTipo(notificacionEmailEnum);
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
            daoHelper.fulfillAuditorFields(true, notificacion, usuarioDto.getId());
            Notificacion notificacionCreada = notificacionRepository.save(notificacion);

            Map<String, CommonModel> mapaCorreo = new HashMap<>();
            mapaCorreo.put("empresa", empresa);
            mapaCorreo.put("notificacion", notificacionCreada);
            emailService.sendEmail(notificacionEmailEnum, empresa.getCorreoElectronico(), mapaCorreo);
        } catch(Exception mex) {
            logger.warn("El correo no se ha podido enviar. Motivo: {}", mex);
        }

        return daoToDtoConverter.convertDaoToDtoIncidencia(incidencia);
    }

    @Override
    public IncidenciaDto eliminarIncidencia(String empresaUuid, String incidenciaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la incidencia con el uuid [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);
        if(incidencia == null) {
            logger.warn("La incidencia no fue encontrada");
            throw new NotFoundResourceException();
        }

        int personasRegistradas = incidenciaPersonaRepository.countByIncidenciaAndEliminadoFalse(incidencia.getId());
        int armasRegistradas = incidenciaArmaRepository.countByIncidenciaAndEliminadoFalse(incidencia.getId());
        int vehiculosRegistrados = incidenciaVehiculoRepository.countByIncidenciaAndEliminadoFalse(incidencia.getId());
        int canesRegistrados = incidenciaCanRepository.countByIncidenciaAndEliminadoFalse(incidencia.getId());

        if(personasRegistradas > 0 || armasRegistradas > 0 || vehiculosRegistrados > 0 || canesRegistrados > 0) {
            logger.warn("Alguno de los entes se encuentran todavia activos.");
            throw new AlreadyActiveException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        incidencia.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, incidencia, usuarioDto.getId());
        incidenciaRepository.save(incidencia);

        return daoToDtoConverter.convertDaoToDtoIncidencia(incidencia);
    }


    private String generarCadenaOriginalAcuse(Acuse acuse, Empresa empresa) {
        return "||" +
                empresa.getId() + "|" +
                empresa.getUuid() + "|" +
                empresa.getRfc() + "|" +
                empresa.getRegistro() + "|" +
                acuse.getUuid() + "|" +
                acuse.getNumero() + "|" +
                acuse.getIncidencia() + "|" +
                acuse.getTipo() + "||";
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
