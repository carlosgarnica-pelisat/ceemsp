package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.dto.metadata.IncidenciaArchivoMetadata;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.services.EmailService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmpresaIncidenciaServiceImpl implements EmpresaIncidenciaService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaIncidenciaService.class);
    private final DaoHelper daoHelper;
    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaClienteService empresaClienteService;
    private final EmpresaClienteDomicilioService empresaClienteDomicilioService;
    private final EmpresaArmaService empresaArmaService;
    private final EmpresaPersonalService empresaPersonalService;
    private final EmpresaCanService empresaCanService;
    private final EmpresaVehiculoService empresaVehiculoService;
    private final IncidenciaArmaRepository incidenciaArmaRepository;
    private final IncidenciaComentarioRepository incidenciaComentarioRepository;
    private final IncidenciaPersonaRepository incidenciaPersonaRepository;
    private final IncidenciaCanRepository incidenciaCanRepository;
    private final IncidenciaVehiculoRepository incidenciaVehiculoRepository;
    private final IncidenciaArchivoRepository incidenciaArchivoRepository;
    private final ArmaRepository armaRepository;
    private final PersonaRepository personaRepository;
    private final PersonalArmaRepository personalArmaRepository;
    private final ArchivosService archivosService;
    private final NotificacionRepository notificacionRepository;
    private final EmpresaRepository empresaRepository;
    private final EmailService emailService;
    private final PublicService publicService;
    private final NotificacionArgosRepository notificacionArgosRepository;
    private static final int MAX_NUMEROS = 10;

    @Autowired
    public EmpresaIncidenciaServiceImpl(IncidenciaRepository incidenciaRepository, UsuarioService usuarioService,
                                        DaoToDtoConverter daoToDtoConverter, EmpresaClienteService empresaClienteService,
                                        EmpresaClienteDomicilioService empresaClienteDomicilioService, EmpresaArmaService empresaArmaService,
                                        EmpresaPersonalService empresaPersonalService, EmpresaCanService empresaCanService,
                                        EmpresaVehiculoService empresaVehiculoService, IncidenciaArmaRepository incidenciaArmaRepository,
                                        IncidenciaComentarioRepository incidenciaComentarioRepository, IncidenciaPersonaRepository incidenciaPersonaRepository,
                                        IncidenciaCanRepository incidenciaCanRepository, IncidenciaVehiculoRepository incidenciaVehiculoRepository,
                                        IncidenciaArchivoRepository incidenciaArchivoRepository, DaoHelper daoHelper,
                                        ArmaRepository armaRepository, PersonaRepository personaRepository,
                                        PersonalArmaRepository personalArmaRepository, NotificacionRepository notificacionRepository,
                                        ArchivosService archivosService, EmpresaRepository empresaRepository, EmailService emailService,
                                        PublicService publicService, NotificacionArgosRepository notificacionArgosRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaClienteService = empresaClienteService;
        this.empresaClienteDomicilioService = empresaClienteDomicilioService;
        this.empresaArmaService = empresaArmaService;
        this.empresaPersonalService = empresaPersonalService;
        this.empresaCanService = empresaCanService;
        this.empresaVehiculoService = empresaVehiculoService;
        this.incidenciaArmaRepository = incidenciaArmaRepository;
        this.incidenciaComentarioRepository = incidenciaComentarioRepository;
        this.incidenciaPersonaRepository = incidenciaPersonaRepository;
        this.incidenciaCanRepository = incidenciaCanRepository;
        this.incidenciaVehiculoRepository = incidenciaVehiculoRepository;
        this.incidenciaArchivoRepository = incidenciaArchivoRepository;
        this.daoHelper = daoHelper;
        this.armaRepository = armaRepository;
        this.personaRepository = personaRepository;
        this.personalArmaRepository = personalArmaRepository;
        this.archivosService = archivosService;
        this.notificacionRepository = notificacionRepository;
        this.empresaRepository = empresaRepository;
        this.publicService = publicService;
        this.emailService = emailService;
        this.notificacionArgosRepository = notificacionArgosRepository;
    }

    @Override
    public List<IncidenciaDto> obtenerIncidenciasPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid esta como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las incidencias con el uuid de la empresa [{}]", username);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Incidencia> incidencias = incidenciaRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

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
    public IncidenciaDto obtenerIncidenciaPorUuid(String username, String incidenciaUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(incidenciaUuid)) {
            logger.warn("El uuid de la empresa o de la incidencia vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los detalles de la incidencia con el uuid [{}]", username);

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
            incidenciaDto.setCliente(empresaClienteService.obtenerClientePorId(incidencia.getCliente()));
        }

        if(incidencia.getClienteDomicilio() != null && incidencia.getClienteDomicilio() > 0) {
            incidenciaDto.setClienteDomicilio(empresaClienteDomicilioService.obtenerPorId(incidencia.getClienteDomicilio()));
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

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

        incidenciaDto.setPersonasInvolucradas(incidendiaPersonas.stream().map(p -> empresaPersonalService.obtenerPorId(p.getPersona())).collect(Collectors.toList()));
        incidenciaDto.setArmasInvolucradas(incidenciaArmas.stream().map(a -> {
            ArmaDto arma = empresaArmaService.obtenerArmaPorId(a.getArma());
            arma.setEliminadoIncidencia(a.getEliminado());
            arma.setFechaCreacionIncidencia(a.getFechaCreacion().toString());
            if(a.getEliminado()) {
                arma.setFechaEliminacionIncidencia(a.getFechaActualizacion().toString());
            }
            return arma;
        }).collect(Collectors.toList()));

        incidenciaDto.setCanesInvolucrados(incidenciaCanes.stream().map(c -> {
            CanDto canDto = empresaCanService.obtenerCanPorId(c.getCan());
            return canDto;
        }).collect(Collectors.toList()));
        incidenciaDto.setVehiculosInvolucrados(incidenciaVehiculos.stream().map(v -> empresaVehiculoService.obtenerVehiculoPorId(v.getVehiculo())).collect(Collectors.toList()));
        incidenciaDto.setArchivos(incidenciaArchivos.stream().map(a -> {
            IncidenciaArchivoMetadata incidenciaArchivoMetadata = new IncidenciaArchivoMetadata();
            incidenciaArchivoMetadata.setNombreArchivo(a.getRutaArchivo());
            incidenciaArchivoMetadata.setId(a.getId());
            incidenciaArchivoMetadata.setUuid(a.getUuid());
            String[] tokens = a.getRutaArchivo().split("[\\\\|/]");
            incidenciaArchivoMetadata.setNombreArchivo(tokens[tokens.length - 1]);
            return incidenciaArchivoMetadata;
        }).collect(Collectors.toList()));
        return incidenciaDto;
    }

    @Override
    @Transactional
    public IncidenciaDto guardarIncidencia(String username, IncidenciaDto incidenciaDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(username) || incidenciaDto == null) {
            logger.warn("El uuid, el usuario o la incidencia vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nueva incidendia");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

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
            incidencia.setEmpresa(usuarioDto.getEmpresa().getId());
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
                String ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_INCIDENCIA, usuarioDto.getEmpresa().getUuid());
                incidenciaArchivo.setRutaArchivo(ruta);
                incidenciaArchivoRepository.save(incidenciaArchivo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo.", ex);
            }
        }

        try {
            Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(usuarioDto.getEmpresa().getUuid());

            NotificacionArgos notificacionArgos = new NotificacionArgos();
            notificacionArgos.setTipo(NotificacionArgosTipoEnum.GENERAL);
            notificacionArgos.setMotivo("Nueva incidencia creada");
            notificacionArgos.setDescripcion("La empresa " + empresa.getRazonSocial() + " ha creado la incidencia " + incidenciaCreada.getNumero());
            notificacionArgos.setUbicacion("/home/empresas/" + empresa.getUuid() + "/incidencias?uuid=" + incidencia.getUuid());
            daoHelper.fulfillAuditorFields(true, notificacionArgos, usuarioDto.getId());
            notificacionArgosRepository.save(notificacionArgos);

            Notificacion notificacion = new Notificacion();
            notificacion.setTipo(NotificacionEmailEnum.NUEVA_INCIDENCIA);
            notificacion.setEmpresa(usuarioDto.getEmpresa().getId());
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
            emailService.sendEmail(NotificacionEmailEnum.NUEVA_INCIDENCIA, usuarioDto.getEmpresa().getCorreoElectronico(), mapaCorreo);
        } catch(Exception mex) {
            logger.warn("El correo no se ha podido enviar. Motivo: {}", mex);
        }

        return daoToDtoConverter.convertDaoToDtoIncidencia(incidenciaCreada);
    }

    @Override
    @Transactional
    public IncidenciaDto agregarComentario(String incidenciaUuid, String username, IncidenciaDto incidenciaDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || incidenciaDto == null) {
            logger.warn("Alguno de los parametros no es valido");
            throw new InvalidDataException();
        }

        logger.info("Agregando comentario a la incidencia [{}]", incidenciaUuid);

        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);

        if(incidencia == null) {
            logger.warn("La incidencia no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        incidencia.setStatus(IncidenciaStatusEnum.CONTESTADA);
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

        if(multipartFile != null) {
            logger.info("Hay archivo");
            IncidenciaArchivo incidenciaArchivo = new IncidenciaArchivo();
            incidenciaArchivo.setIncidencia(incidencia.getId());
            daoHelper.fulfillAuditorFields(true, incidenciaArchivo, usuarioDto.getId());
            try {
                String ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_INCIDENCIA, usuarioDto.getEmpresa().getUuid());
                incidenciaArchivo.setRutaArchivo(ruta);
                incidenciaArchivoRepository.save(incidenciaArchivo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo.", ex);
            }
        }

        try {
            Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(usuarioDto.getEmpresa().getUuid());

            if(incidencia.getAsignado() != null) {
                UsuarioDto asignado = usuarioService.getUserById(incidencia.getAsignado());
                NotificacionArgos notificacionArgos = new NotificacionArgos();
                notificacionArgos.setUuid(RandomStringUtils.randomAlphanumeric(12));
                notificacionArgos.setTipo(NotificacionArgosTipoEnum.INDIVIDUAL);
                notificacionArgos.setUsuario(asignado.getId());
                notificacionArgos.setMotivo("Incidencia respondida por la empresa");
                notificacionArgos.setDescripcion("La empresa " + empresa.getRazonSocial() + " ha respondido la incidencia con el numero " + incidencia.getNumero());
                notificacionArgos.setUbicacion("/home/empresas/" + empresa.getUuid() + "/incidencias?uuid=" + incidencia.getUuid());
                daoHelper.fulfillAuditorFields(true, notificacionArgos, usuarioDto.getId());
                notificacionArgosRepository.save(notificacionArgos);
            } else {

                NotificacionArgos notificacionArgos = new NotificacionArgos();
                notificacionArgos.setUuid(RandomStringUtils.randomAlphanumeric(12));
                notificacionArgos.setTipo(NotificacionArgosTipoEnum.GENERAL);
                notificacionArgos.setMotivo("Incidencia respondida por la empresa");
                notificacionArgos.setDescripcion("La empresa " + empresa.getRazonSocial() + " ha respondido la incidencia con el numero " + incidencia.getNumero());
                notificacionArgos.setUbicacion("/home/empresas/" + empresa.getUuid() + "/incidencias?uuid=" + incidencia.getUuid());
                daoHelper.fulfillAuditorFields(true, notificacionArgos, usuarioDto.getId());
                notificacionArgosRepository.save(notificacionArgos);
            }


            NotificacionEmailEnum notificacionEmailEnum = NotificacionEmailEnum.INCIDENCIA_RESPONDIDA;

            Notificacion notificacion = new Notificacion();
            notificacion.setTipo(notificacionEmailEnum);
            notificacion.setEmpresa(usuarioDto.getEmpresa().getId());
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
