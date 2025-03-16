package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.*;
import com.pelisat.cesp.ceemsp.infrastructure.services.EmailService;
import com.pelisat.cesp.ceemsp.infrastructure.services.QRCodeService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmpresaReporteMensualServiceImpl implements EmpresaReporteMensualService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaReporteMensualRepository empresaReporteMensualRepository;
    private final PersonaRepository personaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EmpresaEquipoRepository empresaEquipoRepository;
    private final EmpresaEquipoMovimientoRepository empresaEquipoMovimientoRepository;
    private final UniformeRepository uniformeRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;
    private final CanRepository canRepository;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;
    private final ArmaRepository armaRepository;
    private final Logger logger = LoggerFactory.getLogger(EmpresaReporteMensualService.class);
    @Value("${spring.profiles.active}")
    private String activeProfile;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;
    private final EmpresaRepository empresaRepository;
    private final VentanaRepository ventanaRepository;
    private final PublicService publicService;
    private final CatalogoService catalogoService;
    private final EmpresaReporteMensualMovimientoRepository empresaReporteMensualMovimientoRepository;

    @Autowired
    public EmpresaReporteMensualServiceImpl(DaoToDtoConverter daoToDtoConverter, EmpresaReporteMensualRepository empresaReporteMensualRepository,
                                            PersonaRepository personaRepository, ClienteRepository clienteRepository, VehiculoRepository vehiculoRepository,
                                            EmpresaEquipoRepository empresaEquipoRepository, UniformeRepository uniformeRepository, IncidenciaRepository incidenciaRepository,
                                            EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository, CanRepository canRepository,
                                            UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper, EmpresaEquipoMovimientoRepository empresaEquipoMovimientoRepository,
                                            EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository, ArmaRepository armaRepository,
                                            QRCodeService qrCodeService, EmailService emailService, VentanaRepository ventanaRepository,
                                            EmpresaRepository empresaRepository, PublicService publicService, CatalogoService catalogoService,
                                            EmpresaReporteMensualMovimientoRepository empresaReporteMensualMovimientoRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaReporteMensualRepository = empresaReporteMensualRepository;
        this.personaRepository = personaRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.empresaEquipoRepository = empresaEquipoRepository;
        this.uniformeRepository = uniformeRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
        this.canRepository = canRepository;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.empresaEquipoMovimientoRepository = empresaEquipoMovimientoRepository;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
        this.armaRepository = armaRepository;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
        this.empresaRepository = empresaRepository;
        this.ventanaRepository = ventanaRepository;
        this.publicService = publicService;
        this.catalogoService = catalogoService;
        this.empresaReporteMensualMovimientoRepository = empresaReporteMensualMovimientoRepository;
    }
    @Override
    public List<EmpresaReporteMensualDto> listarReportes(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<EmpresaReporteMensual> reportes = empresaReporteMensualRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return reportes.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaReporteMensual)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaReporteMensualDto descargarReporteUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el reporte con el uuid [{}]", uuid);

        EmpresaReporteMensual reporte = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(uuid);

        if(reporte == null) {
            logger.warn("El reporte no existe con el uuid dado [{}]", uuid);
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaReporteMensual(reporte);
    }

    @Override
    public EmpresaReporteMensualDto pregenerarReporte(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }
        logger.info("Pre generando el reporte");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaReporteMensual empresaReporteMensual = generarReporteMensual(usuarioDto);
        return daoToDtoConverter.convertDaoToDtoEmpresaReporteMensual(empresaReporteMensual);
    }

    @Override
    @Transactional
    public EmpresaReporteMensualDto guardarReporte(String username, EmpresaReporteMensualDto empresaReporteMensualDto) throws NoSuchAlgorithmException {
        if(StringUtils.isBlank(username) || empresaReporteMensualDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }
        logger.info("Pre generando el reporte");

        // validando si hay ventanas disponibles para subir el reporte
        Ventana ventana = ventanaRepository.getByFechaFinGreaterThanEqualAndEliminadoFalse(LocalDate.now());
        if(ventana == null) {
            logger.warn("No hay ventanas disponibles para cargar el reporte");
            throw new NotAvailableWindowException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        List<EmpresaReporteMensual> informesPorVentana = empresaReporteMensualRepository.getAllByEmpresaAndVentanaAndEliminadoFalse(usuarioDto.getEmpresa().getId(), ventana.getId());

        if(informesPorVentana.size() > 0) {
            logger.warn("Ya hay reportes registrados en esta ventana. Favor de esperar a que haya una nueva");
            throw new ReportAlreadyPresentedException();
        }

        EmpresaReporteMensual empresaReporteMensual = generarReporteMensual(usuarioDto);
        empresaReporteMensual.setReportaUniformes(empresaReporteMensualDto.getReportaUniformes());
        empresaReporteMensual.setEmpresa(usuarioDto.getEmpresa().getId());
        empresaReporteMensual.setNumero("CESP/DSSP/ACUSE/INF/" +  publicService.buscarProximoNumeroReporte() + "/" + LocalDate.now().getYear());
        daoHelper.fulfillAuditorFields(true, empresaReporteMensual, usuarioDto.getId());
        EmpresaReporteMensual reporteCreado = empresaReporteMensualRepository.save(empresaReporteMensual);

        reporteCreado.setCadenaOriginal(generarCadenaOriginalReporte(reporteCreado, usuarioDto.getEmpresa()));
        reporteCreado.setSelloSalt(RandomStringUtils.randomAlphanumeric(10));
        reporteCreado.setVentana(ventana.getId());

        // Generando el sello del acuse
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(reporteCreado.getSelloSalt().getBytes(StandardCharsets.UTF_8));
        byte[] bytes = digest.digest(reporteCreado.getCadenaOriginal().getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        reporteCreado.setSello(sb.toString());
        daoHelper.fulfillAuditorFields(false, reporteCreado, usuarioDto.getId());
        EmpresaReporteMensual nuevoReporte = empresaReporteMensualRepository.save(reporteCreado);

        // Guardando los movimientos relacionados al reporte
        for(EmpresaReporteMensualMovimiento m : reporteCreado.getMovimientos()) {
            m.setReporteMensual(nuevoReporte.getId());
            m.setUuid(RandomStringUtils.randomAlphanumeric(12));
            daoHelper.fulfillAuditorFields(true, m, usuarioDto.getId());
            empresaReporteMensualMovimientoRepository.save(m);
        }

        try {
            String path;
            if(StringUtils.equals("local", activeProfile)) {
                path = "http://localhost:4250/validar-reporte?sello=";
            } else if (StringUtils.equals("dev", activeProfile)) {
                path = "https://argos.jalisco.gob.mx/validar-reporte?sello=";
            } else {
                throw new UnsupportedEnvironmentException();
            }

            Map<String, Object> mapaCorreo = new HashMap<>();
            mapaCorreo.put("empresa", usuarioDto.getEmpresa());
            mapaCorreo.put("reporte", reporteCreado);
            mapaCorreo.put("qr", qrCodeService.generarQRAcuseBase64(path + reporteCreado.getSello()));

            emailService.sendEmail(NotificacionEmailEnum.ACUSE_INFORME_MENSUAL, usuarioDto.getEmpresa().getCorreoElectronico(), mapaCorreo);
        } catch(Exception ex) {
            logger.warn("El correo no se ha podido enviar. Motivo: {}", ex);
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaReporteMensual(reporteCreado);
    }

    @Override
    public List<PersonaDto> obtenerActivosPersonalPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Personal> personalTotalMes = personaRepository.findAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
        );
        personalTotalMes.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
        ));

        List<Personal> altasPersonal = personaRepository.findAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );
        altasPersonal.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        ));

        List<Personal> bajasPersonal = personaRepository.findAllByEmpresaAndPuestoInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );
        bajasPersonal.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        ));

        List<Personal> temp = ListUtils.subtract(personalTotalMes, bajasPersonal);
        List<Personal> personalActivoMes = ListUtils.subtract(temp, altasPersonal);
        return personalActivoMes.stream()
                .map(p -> {
                    PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                    dto.setPuestoDeTrabajo(catalogoService.obtenerPuestoPorId(p.getPuesto()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaDto> obtenerAltasPersonalPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Personal> altasPersonal = personaRepository.findAllByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasPersonal.stream()
                .map(p -> {
                    PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                    dto.setPuestoDeTrabajo(catalogoService.obtenerPuestoPorId(p.getPuesto()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaDto> obtenerBajasPersonalPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Personal> bajasPersonal = personaRepository.findAllByEmpresaAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasPersonal.stream()
                .map(p -> {
                    PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                    dto.setPuestoDeTrabajo(catalogoService.obtenerPuestoPorId(p.getPuesto()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteDto> obtenerActivosClientesPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Cliente> clienteTotalMes = clienteRepository.findAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.now().minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
        );

        List<Cliente> altasClientes = clienteRepository.findAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Cliente> bajasClientes = clienteRepository.findAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Cliente> temp = ListUtils.subtract(clienteTotalMes, bajasClientes);
        List<Cliente> clienteActivoMes = ListUtils.subtract(temp, altasClientes);
        return clienteActivoMes.stream()
                .map(p -> {
                    ClienteDto dto = daoToDtoConverter.convertDaoToDtoCliente(p);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteDto> obtenerAltasClientePorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Cliente> altasClientes = clienteRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasClientes.stream()
                .map(daoToDtoConverter::convertDaoToDtoCliente)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteDto> obtenerBajasClientePorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Cliente> bajasClientes = clienteRepository.findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasClientes.stream()
                .map(daoToDtoConverter::convertDaoToDtoCliente)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehiculoDto> obtenerActivosVehiculosPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Vehiculo> vehiculosTotalMes = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.now().minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
        );

        List<Vehiculo> altasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Vehiculo> bajasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Vehiculo> temp = ListUtils.subtract(vehiculosTotalMes, bajasVehiculos);
        List<Vehiculo> vehiculosActivosMes = ListUtils.subtract(temp, altasVehiculos);
        return vehiculosActivosMes.stream()
                .map(v -> {
                    VehiculoDto dto = daoToDtoConverter.convertDaoToDtoVehiculo(v);
                    dto.setMarca(catalogoService.obtenerMarcaPorId(v.getMarca()));
                    if (v.getSubmarca() > 0) {
                        dto.setSubmarca(catalogoService.obtenerSubmarcaPorId(v.getSubmarca()));
                    }
                    dto.setTipo(catalogoService.obtenerTipoVehiculoPorId(v.getTipo()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VehiculoDto> obtenerAltasVehiculoPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Vehiculo> altasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasVehiculos.stream()
                .map(v -> {
                    VehiculoDto dto = daoToDtoConverter.convertDaoToDtoVehiculo(v);
                    dto.setMarca(catalogoService.obtenerMarcaVehiculoPorId(v.getMarca()));
                    if (v.getSubmarca() > 0) {
                        dto.setSubmarca(catalogoService.obtenerSubmarcaPorId(v.getSubmarca()));
                    }
                    dto.setTipo(catalogoService.obtenerTipoVehiculoPorId(v.getTipo()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VehiculoDto> obtenerBajasVehiculoPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Vehiculo> bajasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasVehiculos.stream()
                .map(v -> {
                    VehiculoDto dto = daoToDtoConverter.convertDaoToDtoVehiculo(v);
                    dto.setMarca(catalogoService.obtenerMarcaVehiculoPorId(v.getMarca()));
                    if (v.getSubmarca() > 0) {
                        dto.setSubmarca(catalogoService.obtenerSubmarcaPorId(v.getSubmarca()));
                    }
                    dto.setTipo(catalogoService.obtenerTipoVehiculoPorId(v.getTipo()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ArmaDto> obtenerActivosArmasPorReporte(String username, String reporteUuid, String modalidad) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(modalidad)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        int modalidadId;
        if(StringUtils.equals(modalidad, "1")) {
            modalidadId = 6;
        } else if(StringUtils.equals(modalidad, "2")) {
            modalidadId = 7;
        } else if(StringUtils.equals(modalidad, "3")) {
            modalidadId = 8;
        } else {
            throw new InvalidDataException();
        }

        List<ArmaDto> armas = new ArrayList<>();

        List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndModalidad(usuarioDto.getEmpresa().getId(), modalidadId);
        licenciasColectivas.forEach(lc -> armas.addAll(armaRepository.findAllByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                lc.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        ).stream().map(a -> {
            ArmaDto dto = daoToDtoConverter.convertDaoToDtoArma(a);
            dto.setNumeroOficio(lc.getNumeroOficio());
            dto.setClase(catalogoService.obtenerArmaClasePorId(a.getClase()));
            dto.setMarca(catalogoService.obtenerArmaMarcaPorId(a.getMarca()));
            return dto;
        }).collect(Collectors.toList())));

        return armas;
    }

    @Override
    public List<ArmaDto> obtenerAltasArmasPorReporte(String username, String reporteUuid, String modalidad) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(modalidad)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        int modalidadId;
        if(StringUtils.equals(modalidad, "1")) {
            modalidadId = 6;
        } else if(StringUtils.equals(modalidad, "2")) {
            modalidadId = 7;
        } else if(StringUtils.equals(modalidad, "3")) {
            modalidadId = 8;
        } else {
            throw new InvalidDataException();
        }

        List<ArmaDto> armas = new ArrayList<>();

        List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndModalidad(usuarioDto.getEmpresa().getId(), modalidadId);
        licenciasColectivas.forEach(lc -> armas.addAll(armaRepository.findAllByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                lc.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        ).stream().map(a -> {
            ArmaDto dto = daoToDtoConverter.convertDaoToDtoArma(a);
            dto.setNumeroOficio(lc.getNumeroOficio());
            dto.setClase(catalogoService.obtenerArmaClasePorId(a.getClase()));
            dto.setMarca(catalogoService.obtenerArmaMarcaPorId(a.getMarca()));
            return dto;
        }).collect(Collectors.toList())));

        return armas;
    }

    @Override
    public List<ArmaDto> obtenerBajasArmasPorReporte(String username, String reporteUuid, String modalidad) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(modalidad)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        int modalidadId;
        if(StringUtils.equals(modalidad, "1")) {
            modalidadId = 6;
        } else if(StringUtils.equals(modalidad, "2")) {
            modalidadId = 7;
        } else if(StringUtils.equals(modalidad, "3")) {
            modalidadId = 8;
        } else {
            throw new InvalidDataException();
        }

        List<ArmaDto> armas = new ArrayList<>();

        List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndModalidad(usuarioDto.getEmpresa().getId(), modalidadId);
        licenciasColectivas.forEach(lc -> armas.addAll(armaRepository.findAllByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                lc.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        ).stream().map(a -> {
            ArmaDto dto = daoToDtoConverter.convertDaoToDtoArma(a);
            dto.setNumeroOficio(lc.getNumeroOficio());
            dto.setClase(catalogoService.obtenerArmaClasePorId(a.getClase()));
            dto.setMarca(catalogoService.obtenerArmaMarcaPorId(a.getMarca()));
            return dto;
        }).collect(Collectors.toList())));

        return armas;
    }

    @Override
    public List<CanDto> obtenerActivosCanesPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Can> altasCanes = canRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalseAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Can> bajasCanes = canRepository.findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return null;
    }

    @Override
    public List<CanDto> obtenerAltasCanesPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Can> altasCanes= canRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalseAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasCanes.stream()
                .map(c -> {
                    CanDto dto = daoToDtoConverter.convertDaoToDtoCan(c);
                    dto.setRaza(catalogoService.obtenerCanRazaPorId(c.getRaza()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CanDto> obtenerBajasCanesPorReporte(String username, String reporteUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Can> bajasCanes = canRepository.findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasCanes.stream()
                .map(c -> {
                    CanDto dto = daoToDtoConverter.convertDaoToDtoCan(c);
                    dto.setRaza(catalogoService.obtenerCanRazaPorId(c.getRaza()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private EmpresaReporteMensual generarReporteMensual(UsuarioDto usuarioDto) {
        List<EmpresaReporteMensualMovimiento> movimientos = new ArrayList<>();
        EmpresaReporteMensual empresaReporteMensual = new EmpresaReporteMensual();

        LocalDateTime inicioMes = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime finMes = YearMonth.now().minusMonths(1).atEndOfMonth().atTime(23, 59, 59);

        // Obteniendo el personal operativo con informacion incompleta
        List<Personal> personalOperativoAltas = personaRepository.getAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                finMes,
                inicioMes
        );
        List<Personal> personalOperativoBajas = personaRepository.getAllByEmpresaAndPuestoInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                finMes,
                inicioMes
        );
        List<Personal> personalOperativoTotal = personaRepository.getAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                finMes
        );

        // Obteniendo el personal no operativo con informacion completa
        List<Personal> personalNoOperativoAltas = personaRepository.getAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                finMes,
                inicioMes
        );
        List<Personal> personalNoOperativoBajas = personaRepository.getAllByEmpresaAndPuestoNotInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                finMes,
                inicioMes
        );
        List<Personal> personalNoOperativoTotal = personaRepository.getAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                Arrays.asList(3),
                finMes
        );

        // Precargando informacion del personal, comenzando siempre con los totales y de alli haciendo calculo "a reversa"
        empresaReporteMensual.setPersonalAltas(personalOperativoAltas.size() + personalNoOperativoAltas.size());
        empresaReporteMensual.setPersonalBajas(personalOperativoBajas.size() + personalNoOperativoBajas.size());
        empresaReporteMensual.setPersonalTotal(personalOperativoTotal.size() + personalNoOperativoTotal.size());
        empresaReporteMensual.setPersonalActivos(empresaReporteMensual.getPersonalTotal() + empresaReporteMensual.getPersonalBajas() - empresaReporteMensual.getPersonalAltas());

        // Uniendo los valores
        List<Personal> personalAltasTotal = Stream.concat(personalOperativoAltas.stream(), personalNoOperativoAltas.stream()).collect(Collectors.toList());
        List<Personal> personalBajasTotal  = Stream.concat(personalOperativoBajas.stream(), personalNoOperativoBajas.stream()).collect(Collectors.toList());
        List<Personal> personalTotal = Stream.concat(personalOperativoTotal.stream(), personalNoOperativoTotal.stream()).collect(Collectors.toList());
        List<Personal> tempPersonal = ListUtils.subtract(personalTotal, personalBajasTotal);
        List<Personal> personalActivos = ListUtils.subtract(tempPersonal, personalAltasTotal);

        // Generando el movimiento.
        EmpresaReporteMensualMovimiento movimientoPersonalActivos = new EmpresaReporteMensualMovimiento();
        movimientoPersonalActivos.setTipo(ReporteTipoEnum.PERSONAL);
        movimientoPersonalActivos.setMovimiento(ReporteMovimientoEnum.ACTIVOS);
        movimientoPersonalActivos.setElementos(personalActivos.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoPersonalAltas = new EmpresaReporteMensualMovimiento();
        movimientoPersonalAltas.setTipo(ReporteTipoEnum.PERSONAL);
        movimientoPersonalAltas.setMovimiento(ReporteMovimientoEnum.ALTA);
        movimientoPersonalAltas.setElementos(personalAltasTotal.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoPersonalBajas = new EmpresaReporteMensualMovimiento();
        movimientoPersonalBajas.setTipo(ReporteTipoEnum.PERSONAL);
        movimientoPersonalBajas.setMovimiento(ReporteMovimientoEnum.BAJA);
        movimientoPersonalBajas.setElementos(personalBajasTotal.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoPersonalTotal = new EmpresaReporteMensualMovimiento();
        movimientoPersonalTotal.setTipo(ReporteTipoEnum.PERSONAL);
        movimientoPersonalTotal.setMovimiento(ReporteMovimientoEnum.TOTAL);
        movimientoPersonalTotal.setElementos(personalTotal.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        movimientos.add(movimientoPersonalActivos);
        movimientos.add(movimientoPersonalAltas);
        movimientos.add(movimientoPersonalBajas);
        movimientos.add(movimientoPersonalTotal);

        // Precargando informacion para clientes
        List<Cliente> clientesAltas = clienteRepository.getAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        );

        List<Cliente> clientesBajas = clienteRepository.getAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        );

        List<Cliente> clientesTotales = clienteRepository.getAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes);

        empresaReporteMensual.setClientesAltas(clientesAltas.size());
        empresaReporteMensual.setClientesBajas(clientesBajas.size());
        empresaReporteMensual.setClientesTotal(clientesTotales.size());
        empresaReporteMensual.setClientesActivos(empresaReporteMensual.getClientesTotal() + empresaReporteMensual.getClientesBajas() - empresaReporteMensual.getClientesAltas());

        List<Cliente> tempClientes = ListUtils.subtract(clientesTotales, clientesBajas);
        List<Cliente> clientesActivos = ListUtils.subtract(tempClientes, clientesAltas);

        // Generando los movimientos
        EmpresaReporteMensualMovimiento movimientoClientesActivos = new EmpresaReporteMensualMovimiento();
        movimientoClientesActivos.setTipo(ReporteTipoEnum.CLIENTES);
        movimientoClientesActivos.setMovimiento(ReporteMovimientoEnum.ACTIVOS);
        movimientoClientesActivos.setElementos(clientesActivos.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoClientesAltas = new EmpresaReporteMensualMovimiento();
        movimientoClientesAltas.setTipo(ReporteTipoEnum.CLIENTES);
        movimientoClientesAltas.setMovimiento(ReporteMovimientoEnum.ALTA);
        movimientoClientesAltas.setElementos(clientesAltas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoClientesBajas = new EmpresaReporteMensualMovimiento();
        movimientoClientesBajas.setTipo(ReporteTipoEnum.CLIENTES);
        movimientoClientesBajas.setMovimiento(ReporteMovimientoEnum.BAJA);
        movimientoClientesBajas.setElementos(clientesBajas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoClientesTotal = new EmpresaReporteMensualMovimiento();
        movimientoClientesTotal.setTipo(ReporteTipoEnum.CLIENTES);
        movimientoClientesTotal.setMovimiento(ReporteMovimientoEnum.TOTAL);
        movimientoClientesTotal.setElementos(clientesTotales.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        movimientos.add(movimientoClientesActivos);
        movimientos.add(movimientoClientesAltas);
        movimientos.add(movimientoClientesBajas);
        movimientos.add(movimientoClientesTotal);

        // Precargando informacion para vehiculos
        List<Vehiculo> vehiculosAltas = vehiculoRepository.getAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        );

        List<Vehiculo> vehiculosBajas = vehiculoRepository.getAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        );

        List<Vehiculo> vehiculosTotales = vehiculoRepository.getAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes);

        empresaReporteMensual.setVehiculosAltas(vehiculosAltas.size());
        empresaReporteMensual.setVehiculosBajas(vehiculosBajas.size());
        empresaReporteMensual.setVehiculosTotal(vehiculosTotales.size());
        empresaReporteMensual.setVehiculosActivos(empresaReporteMensual.getVehiculosTotal() + empresaReporteMensual.getVehiculosBajas() - empresaReporteMensual.getVehiculosAltas());

        List<Vehiculo> tempVehiculos = ListUtils.subtract(vehiculosTotales, vehiculosBajas);
        List<Vehiculo> vehiculosActivos = ListUtils.subtract(tempVehiculos, vehiculosAltas);

        // Generando los movimientos de los vehiculos
        EmpresaReporteMensualMovimiento movimientoVehiculosActivos = new EmpresaReporteMensualMovimiento();
        movimientoVehiculosActivos.setTipo(ReporteTipoEnum.VEHICULOS);
        movimientoVehiculosActivos.setMovimiento(ReporteMovimientoEnum.ACTIVOS);
        movimientoVehiculosActivos.setElementos(vehiculosActivos.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoVehiculosAltas = new EmpresaReporteMensualMovimiento();
        movimientoVehiculosAltas.setTipo(ReporteTipoEnum.VEHICULOS);
        movimientoVehiculosAltas.setMovimiento(ReporteMovimientoEnum.ALTA);
        movimientoVehiculosAltas.setElementos(vehiculosAltas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoVehiculosBajas = new EmpresaReporteMensualMovimiento();
        movimientoVehiculosBajas.setTipo(ReporteTipoEnum.VEHICULOS);
        movimientoVehiculosBajas.setMovimiento(ReporteMovimientoEnum.BAJA);
        movimientoVehiculosBajas.setElementos(vehiculosBajas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        EmpresaReporteMensualMovimiento movimientoVehiculosTotal = new EmpresaReporteMensualMovimiento();
        movimientoVehiculosTotal.setTipo(ReporteTipoEnum.VEHICULOS);
        movimientoVehiculosTotal.setMovimiento(ReporteMovimientoEnum.TOTAL);
        movimientoVehiculosTotal.setElementos(vehiculosTotales.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

        movimientos.add(movimientoVehiculosActivos);
        movimientos.add(movimientoVehiculosAltas);
        movimientos.add(movimientoVehiculosBajas);
        movimientos.add(movimientoVehiculosTotal);

        // Precargando informacion del equipo
        AtomicInteger altas = new AtomicInteger(0);
        AtomicInteger bajas = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(0);

        List<EmpresaEquipo> equiposEmpresa = empresaEquipoRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());
        equiposEmpresa.forEach(ee -> {
            List<EmpresaEquipoMovimiento> empresaEquipoMovimientosMes = empresaEquipoMovimientoRepository.findAllByEmpresaEquipoAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                    ee.getId(),
                    finMes,
                    inicioMes
            );

            EmpresaEquipoMovimiento ultimoMovimiento = null;

            if(empresaEquipoMovimientosMes.size() > 0) {
                ultimoMovimiento = Collections.max(empresaEquipoMovimientosMes, Comparator.comparing(EmpresaEquipoMovimiento::getFechaCreacion));
            }

            int altasTemporales = altas.get();
            int bajasTemporales = bajas.get();
            int totalTemporal = total.get();

            altas.set(altasTemporales + empresaEquipoMovimientosMes.stream().reduce(0, (partial, movimiento) -> partial + movimiento.getAltas(), Integer::sum));
            bajas.set(bajasTemporales + empresaEquipoMovimientosMes.stream().reduce(0, (partial, movimiento) -> partial + movimiento.getBajas(), Integer::sum));

            //total.set(ultimoMovimiento != null ? (totalTemporal + ultimoMovimiento.getCantidadActual()) : (totalTemporal + 0));
            total.set(ee.getCantidad().intValue() + total.get());
        });

        empresaReporteMensual.setEquipoAltas(altas.get());
        empresaReporteMensual.setEquipoBajas(bajas.get());
        empresaReporteMensual.setEquipoTotal(total.get());
        empresaReporteMensual.setEquipoActivos(empresaReporteMensual.getEquipoTotal() + empresaReporteMensual.getEquipoBajas() - empresaReporteMensual.getEquipoAltas());

        // Precargando informacion para incidencias
        empresaReporteMensual.setIncidenciasTotal(incidenciaRepository.countAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes, inicioMes));
        empresaReporteMensual.setIncidenciasProcedentes(incidenciaRepository.countAllByEmpresaAndStatusAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), IncidenciaStatusEnum.PROCEDENTE, finMes, inicioMes));
        empresaReporteMensual.setIncidenciasImprocedentes(incidenciaRepository.countAllByEmpresaAndStatusAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), IncidenciaStatusEnum.IMPROCEDENTE, finMes, inicioMes));
        empresaReporteMensual.setIncidenciasReportadas(empresaReporteMensual.getIncidenciasTotal() - empresaReporteMensual.getIncidenciasProcedentes() - empresaReporteMensual.getIncidenciasImprocedentes());

        // Precargando informacion de los canes
        if(usuarioDto.getEmpresa().isTieneCanes()) {
            empresaReporteMensual.setCanesAltas(canRepository.countByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalse(
                    usuarioDto.getEmpresa().getId(),
                    finMes,
                    inicioMes
            ));
            empresaReporteMensual.setCanesBajas(canRepository.countByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoTrue(
                    usuarioDto.getEmpresa().getId(),
                    finMes,
                    inicioMes
            ));
            empresaReporteMensual.setCanesTotal(canRepository.countByEmpresaAndFechaCreacionLessThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes));

            List<Can> canesAltas = canRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalseAndEliminadoFalse(
                    usuarioDto.getEmpresa().getId(),
                    finMes,
                    inicioMes
            );

            List<Can> canesBajas = canRepository.findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                    usuarioDto.getEmpresa().getId(),
                    finMes,
                    inicioMes
            );

            List<Can> canesTotales = canRepository.findAllByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(
                    usuarioDto.getEmpresa().getId(),
                    finMes
            );

            List<Can> canesAltasBajas = new ArrayList<>();
            canesAltasBajas.addAll(canesAltas);
            canesAltasBajas.addAll(canesBajas);

            List<Can> canesActivos = canesTotales.stream()
                    .filter(c1 -> canesAltasBajas.stream().noneMatch(c2 -> c2.getUuid() == c1.getUuid()))
                    .collect(Collectors.toList());

            empresaReporteMensual.setCanesAsignados((int) canesActivos.stream().filter(o -> o.getStatus() == CanStatusEnum.ACTIVO).count());
            empresaReporteMensual.setCanesInstalaciones((int) canesActivos.stream().filter(o -> o.getStatus() == CanStatusEnum.INSTALACIONES).count());


        }

        // Precargando informacion de las armas
        if(usuarioDto.getEmpresa().isTieneArmas()) {
            List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());
            licenciasColectivas.forEach(lc -> {
                switch (lc.getModalidad()) {
                    case 6: // Modalidad Seguridad Privada a Personas
                        // Precargando informacion de armas con modalidad 6
                        List<Arma> armas1Altas = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                                lc.getId(), /// TODO: Verificar lc.getId
                                finMes,
                                inicioMes
                        );

                        List<Arma> armas1Bajas = armaRepository.getAllByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                                lc.getId(),
                                finMes,
                                inicioMes
                        );

                        List<Arma> armas1Totales = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(
                                lc.getId(), finMes
                        );

                        empresaReporteMensual.setArmas1Total(armas1Totales.size());
                        empresaReporteMensual.setArmas1Altas(armas1Altas.size());
                        empresaReporteMensual.setArmas1Bajas(armas1Bajas.size());
                        empresaReporteMensual.setArmas1Activas(empresaReporteMensual.getArmas1Total() + empresaReporteMensual.getArmas1Bajas() - empresaReporteMensual.getArmas1Altas());

                        List<Arma> tempArmas1 = ListUtils.subtract(armas1Totales, armas1Bajas);
                        List<Arma> armas1Activas = ListUtils.subtract(tempArmas1, armas1Altas);

                        // Generando el movimiento.
                        EmpresaReporteMensualMovimiento movimientoArmas1Activas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas1Activas.setTipo(ReporteTipoEnum.ARMAS_1);
                        movimientoArmas1Activas.setMovimiento(ReporteMovimientoEnum.ACTIVOS);
                        movimientoArmas1Activas.setElementos(armas1Activas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas1Altas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas1Altas.setTipo(ReporteTipoEnum.ARMAS_1);
                        movimientoArmas1Altas.setMovimiento(ReporteMovimientoEnum.ALTA);
                        movimientoArmas1Altas.setElementos(armas1Altas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas1Bajas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas1Bajas.setTipo(ReporteTipoEnum.ARMAS_1);
                        movimientoArmas1Bajas.setMovimiento(ReporteMovimientoEnum.BAJA);
                        movimientoArmas1Bajas.setElementos(armas1Bajas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas1Totales = new EmpresaReporteMensualMovimiento();
                        movimientoArmas1Totales.setTipo(ReporteTipoEnum.ARMAS_1);
                        movimientoArmas1Totales.setMovimiento(ReporteMovimientoEnum.TOTAL);
                        movimientoArmas1Totales.setElementos(armas1Totales.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        movimientos.add(movimientoArmas1Activas);
                        movimientos.add(movimientoArmas1Altas);
                        movimientos.add(movimientoArmas1Bajas);
                        movimientos.add(movimientoArmas1Totales);
                        break;
                    case 7: // Modalidad Seguridad privada en los bienes
                        // Precargando informacion de armas con modalidad 7
                        List<Arma> armas2Altas = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                                lc.getId(),
                                finMes,
                                inicioMes
                        );

                        List<Arma> armas2Bajas = armaRepository.getAllByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                                lc.getId(),
                                finMes,
                                inicioMes
                        );

                        List<Arma> armas2Totales = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(lc.getId(), finMes);

                        List<Arma> tempArmas2 = ListUtils.subtract(armas2Totales, armas2Bajas);
                        List<Arma> armas2Activas = ListUtils.subtract(tempArmas2, armas2Altas);

                        empresaReporteMensual.setArmas2Total(armas2Totales.size());
                        empresaReporteMensual.setArmas2Altas(armas2Altas.size());
                        empresaReporteMensual.setArmas2Bajas(armas2Bajas.size());
                        empresaReporteMensual.setArmas2Activas(empresaReporteMensual.getArmas2Total() + empresaReporteMensual.getArmas2Bajas() - empresaReporteMensual.getArmas2Altas());

                        // Generando el movimiento.
                        EmpresaReporteMensualMovimiento movimientoArmas2Activas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas2Activas.setTipo(ReporteTipoEnum.ARMAS_2);
                        movimientoArmas2Activas.setMovimiento(ReporteMovimientoEnum.ACTIVOS);
                        movimientoArmas2Activas.setElementos(armas2Activas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas2Altas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas2Altas.setTipo(ReporteTipoEnum.ARMAS_2);
                        movimientoArmas2Altas.setMovimiento(ReporteMovimientoEnum.ALTA);
                        movimientoArmas2Altas.setElementos(armas2Altas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas2Bajas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas2Bajas.setTipo(ReporteTipoEnum.ARMAS_2);
                        movimientoArmas2Bajas.setMovimiento(ReporteMovimientoEnum.BAJA);
                        movimientoArmas2Bajas.setElementos(armas2Bajas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas2Totales = new EmpresaReporteMensualMovimiento();
                        movimientoArmas2Totales.setTipo(ReporteTipoEnum.ARMAS_2);
                        movimientoArmas2Totales.setMovimiento(ReporteMovimientoEnum.TOTAL);
                        movimientoArmas2Totales.setElementos(armas2Totales.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        movimientos.add(movimientoArmas2Activas);
                        movimientos.add(movimientoArmas2Altas);
                        movimientos.add(movimientoArmas2Bajas);
                        movimientos.add(movimientoArmas2Totales);
                        break;
                    case 8: // Modalidad en traslado de Bienes o Valores
                        // Precargando informacion de armas con modalidad 8
                        List<Arma> armas3Altas = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                                lc.getId(),
                                finMes,
                                inicioMes
                        );

                        List<Arma> armas3Bajas = armaRepository.getAllByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                                lc.getId(),
                                finMes,
                                inicioMes
                        );

                        List<Arma> armas3Totales = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(
                                lc.getId(), finMes);


                        empresaReporteMensual.setArmas3Total(armas3Totales.size());
                        empresaReporteMensual.setArmas3Altas(armas3Altas.size());
                        empresaReporteMensual.setArmas3Bajas(armas3Bajas.size());
                        empresaReporteMensual.setArmas3Activas(empresaReporteMensual.getArmas3Total() + empresaReporteMensual.getArmas3Bajas() - empresaReporteMensual.getArmas3Altas());

                        List<Arma> tempArmas3 = ListUtils.subtract(armas3Totales, armas3Bajas);
                        List<Arma> armas3Activas = ListUtils.subtract(tempArmas3, armas3Altas);

                        // Generando el movimiento.
                        EmpresaReporteMensualMovimiento movimientoArmas3Activas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas3Activas.setTipo(ReporteTipoEnum.ARMAS_3);
                        movimientoArmas3Activas.setMovimiento(ReporteMovimientoEnum.ACTIVOS);
                        movimientoArmas3Activas.setElementos(armas3Activas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas3Altas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas3Altas.setTipo(ReporteTipoEnum.ARMAS_3);
                        movimientoArmas3Altas.setMovimiento(ReporteMovimientoEnum.ALTA);
                        movimientoArmas3Altas.setElementos(armas3Altas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas3Bajas = new EmpresaReporteMensualMovimiento();
                        movimientoArmas3Bajas.setTipo(ReporteTipoEnum.ARMAS_3);
                        movimientoArmas3Bajas.setMovimiento(ReporteMovimientoEnum.BAJA);
                        movimientoArmas3Bajas.setElementos(armas3Bajas.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        EmpresaReporteMensualMovimiento movimientoArmas3Totales = new EmpresaReporteMensualMovimiento();
                        movimientoArmas3Totales.setTipo(ReporteTipoEnum.ARMAS_3);
                        movimientoArmas3Totales.setMovimiento(ReporteMovimientoEnum.TOTAL);
                        movimientoArmas3Totales.setElementos(armas3Totales.stream().map(m -> m.getId().toString()).collect(Collectors.joining(", ")));

                        movimientos.add(movimientoArmas3Activas);
                        movimientos.add(movimientoArmas3Altas);
                        movimientos.add(movimientoArmas3Bajas);
                        movimientos.add(movimientoArmas3Totales);

                        break;

                }
            });
        }

        empresaReporteMensual.setMovimientos(movimientos);

        return empresaReporteMensual;
    }

    private String generarCadenaOriginalReporte(EmpresaReporteMensual reporteMensual, EmpresaDto empresa) {
        return "||" +
                empresa.getId() + "|" +
                empresa.getUuid() + "|" +
                empresa.getRfc() + "|" +
                empresa.getRegistro() + "|" +
                reporteMensual.getId() + "|" +
                reporteMensual.getUuid() + "||";
    }
}
