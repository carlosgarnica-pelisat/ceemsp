package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaReporteMensualDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmailEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.*;
import com.pelisat.cesp.ceemsp.infrastructure.services.EmailService;
import com.pelisat.cesp.ceemsp.infrastructure.services.QRCodeService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    public EmpresaReporteMensualServiceImpl(DaoToDtoConverter daoToDtoConverter, EmpresaReporteMensualRepository empresaReporteMensualRepository,
                                            PersonaRepository personaRepository, ClienteRepository clienteRepository, VehiculoRepository vehiculoRepository,
                                            EmpresaEquipoRepository empresaEquipoRepository, UniformeRepository uniformeRepository, IncidenciaRepository incidenciaRepository,
                                            EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository, CanRepository canRepository,
                                            UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper, EmpresaEquipoMovimientoRepository empresaEquipoMovimientoRepository,
                                            EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository, ArmaRepository armaRepository,
                                            QRCodeService qrCodeService, EmailService emailService, VentanaRepository ventanaRepository,
                                            EmpresaRepository empresaRepository, PublicService publicService) {
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
        empresaReporteMensualRepository.save(reporteCreado);

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

    private EmpresaReporteMensual generarReporteMensual(UsuarioDto usuarioDto) {
        EmpresaReporteMensual empresaReporteMensual = new EmpresaReporteMensual();

        LocalDateTime inicioMes = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime finMes = YearMonth.now().minusMonths(1).atEndOfMonth().atTime(23, 59, 59);

        // Precargando informacion del personal, comenzando siempre con los totales y de alli haciendo calculo "a reversa"
        empresaReporteMensual.setPersonalAltas(personaRepository.countByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        ));
        empresaReporteMensual.setPersonalBajas(personaRepository.countByEmpresaAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        ));
        empresaReporteMensual.setPersonalTotal(personaRepository.countByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes));
        empresaReporteMensual.setPersonalActivos(empresaReporteMensual.getPersonalTotal() + empresaReporteMensual.getPersonalBajas() - empresaReporteMensual.getPersonalAltas());

        // Precargando informacion para clientes
        empresaReporteMensual.setClientesAltas(clienteRepository.countByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        ));
        empresaReporteMensual.setClientesBajas(clienteRepository.countByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        ));
        empresaReporteMensual.setClientesTotal(clienteRepository.countByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes));
        empresaReporteMensual.setClientesActivos(empresaReporteMensual.getClientesTotal() + empresaReporteMensual.getClientesBajas() - empresaReporteMensual.getClientesAltas());

        // Precargando informacion para vehiculos
        empresaReporteMensual.setVehiculosAltas(vehiculoRepository.countByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        ));
        empresaReporteMensual.setVehiculosBajas(vehiculoRepository.countByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                usuarioDto.getEmpresa().getId(),
                finMes,
                inicioMes
        ));
        empresaReporteMensual.setVehiculosTotal(vehiculoRepository.countByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes));
        empresaReporteMensual.setVehiculosActivos(empresaReporteMensual.getVehiculosTotal() + empresaReporteMensual.getVehiculosBajas() - empresaReporteMensual.getVehiculosAltas());

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

            total.set(ultimoMovimiento != null ? (totalTemporal + ultimoMovimiento.getCantidadActual()) : (totalTemporal + 0));
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
            empresaReporteMensual.setCanesAltas(canRepository.countByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                    usuarioDto.getEmpresa().getId(),
                    finMes,
                    inicioMes
            ));
            empresaReporteMensual.setCanesBajas(canRepository.countByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                    usuarioDto.getEmpresa().getId(),
                    finMes,
                    inicioMes
            ));
            empresaReporteMensual.setCanesTotal(canRepository.countByEmpresaAndFechaCreacionLessThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), finMes));

            List<Can> canesAltas = canRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
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
                        empresaReporteMensual.setArmas1Total(armaRepository.countByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(
                                lc.getId(),
                                finMes
                        ));
                        empresaReporteMensual.setArmas1Altas(armaRepository.countByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                                lc.getId(),
                                finMes,
                                inicioMes
                        ));
                        empresaReporteMensual.setArmas1Bajas(armaRepository.countByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                                lc.getId(),
                                finMes,
                                inicioMes
                        ));
                        empresaReporteMensual.setArmas1Activas(empresaReporteMensual.getArmas1Total() + empresaReporteMensual.getArmas1Bajas() - empresaReporteMensual.getArmas1Altas());
                        break;
                    case 7: // Modalidad Seguridad privada en los bienes
                        empresaReporteMensual.setArmas2Total(armaRepository.countByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(
                                lc.getId(),
                                finMes
                        ));
                        empresaReporteMensual.setArmas2Altas(armaRepository.countByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                                lc.getId(),
                                finMes,
                                inicioMes
                        ));
                        empresaReporteMensual.setArmas2Bajas(armaRepository.countByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                                lc.getId(),
                                finMes,
                                inicioMes
                        ));
                        empresaReporteMensual.setArmas2Activas(empresaReporteMensual.getArmas2Total() + empresaReporteMensual.getArmas2Bajas() - empresaReporteMensual.getArmas2Altas());
                        break;
                    case 8: // Modalidad en traslado de Bienes o Valores
                        empresaReporteMensual.setArmas3Total(armaRepository.countByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(
                                lc.getId(),
                                finMes
                        ));
                        empresaReporteMensual.setArmas3Altas(armaRepository.countByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                                lc.getId(),
                                finMes,
                                inicioMes
                        ));
                        empresaReporteMensual.setArmas3Bajas(armaRepository.countByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                                lc.getId(),
                                finMes,
                                inicioMes
                        ));
                        empresaReporteMensual.setArmas3Activas(empresaReporteMensual.getArmas3Total() + empresaReporteMensual.getArmas3Bajas() - empresaReporteMensual.getArmas3Altas());
                        break;
                }
            });
        }

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
