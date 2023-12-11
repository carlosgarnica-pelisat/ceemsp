package com.pelisat.cesp.ceemsp.restceemsp.task;

import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.NotificacionCorreoEnum;
import com.pelisat.cesp.ceemsp.database.type.NotificacionInternalEmailEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProximosVencimientosTask<T extends CommonModel> {

    private final Logger logger = LoggerFactory.getLogger(ProximosVencimientosTask.class);
    private final VisitaRepository visitaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcuerdoRepository acuerdoRepository;
    private final EmailService emailService;
    private final EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository;
    private static final String CORREO_ELECTRONICO = "argos.cesp@jalisco.gob.mx";
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;

    @Autowired
    public ProximosVencimientosTask(VisitaRepository visitaRepository, EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository, AcuerdoRepository acuerdoRepository,
                                    EmailService emailService,
                                    EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository,
                                    EmpresaEscrituraRepository empresaEscrituraRepository,
                                    EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository) {
        this.visitaRepository = visitaRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.acuerdoRepository = acuerdoRepository;
        this.emailService = emailService;
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
    }

    private void generarNotificacionAcuerdosAVencer15Dias() {
        logger.info("Notificando a usuario del CEEMSP vencimiento de los acuerdos en los proximos 15 dias");
        List<Acuerdo> acuerdos15Dias = acuerdoRepository.getAllByFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(LocalDate.now(), LocalDate.now().plusDays(15));
        List<Acuerdo> acuerdosVencidos = acuerdoRepository.getAllByFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(LocalDate.MIN, LocalDate.now().minusDays(1));

        acuerdos15Dias.addAll(acuerdosVencidos);
        List<Map> mapaAcuerdos = acuerdos15Dias.stream().map(acuerdo -> {
            Empresa empresa = empresaRepository.getOne(acuerdo.getEmpresa());

            Map<String, String> map = new HashMap<>();
            map.put("nombreComercialEmpresa", empresa.getNombreComercial());
            map.put("razonSocialEmpresa", empresa.getRazonSocial());
            map.put("registroEmpresa", empresa.getRegistro());
            map.put("tipoAcuerdo", acuerdo.getTipo().toString());
            map.put("fechaVencimiento", acuerdo.getFechaFin().toString());
            return map;
        }).collect(Collectors.toList());

        try {
            emailService.sendInternalMail(NotificacionInternalEmailEnum.ACUERDOS_VENCIMIENTO_15_DIAS, CORREO_ELECTRONICO, mapaAcuerdos);
        } catch(Exception ex) {
            logger.error("No se ha podido mandar el correo. Motivo: {}", ex);
        }
    }

    private void generarNotificacionRequerimientosAVencer15Dias() {
        logger.info("Notificando a usuarios del CEEMSP vencimiento de los requerimientos en los proximos 15 dias");
        List<Visita> visitasRequerimiento15Dias = visitaRepository.getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.now(), LocalDate.now().plusDays(15));
        List<Visita> visitasRequerimientoVencido = visitaRepository.getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.MIN, LocalDate.now().minusDays(1));

        visitasRequerimiento15Dias.addAll(visitasRequerimientoVencido);
        List<Map> mapaRequerimientos = visitasRequerimiento15Dias.stream().map(requerimiento -> {
            Empresa empresa = empresaRepository.getOne(requerimiento.getEmpresa());

            Map<String, String> map = new HashMap<>();
            map.put("nombreComercialEmpresa", empresa.getNombreComercial());
            map.put("razonSocialEmpresa", empresa.getRazonSocial());
            map.put("registroEmpresa", empresa.getRegistro());
            map.put("fechaVisita", requerimiento.getFechaVisita().toString());
            map.put("fechaVencimiento", requerimiento.getFechaTermino().toString());
            map.put("vencido", LocalDate.now().isAfter(requerimiento.getFechaTermino()) ? "SI" : "NO");
            return map;
        }).collect(Collectors.toList());

        try {
            emailService.sendInternalMail(NotificacionInternalEmailEnum.REQUERIMIENTOS_VENCIMIENTO_15_DIAS, CORREO_ELECTRONICO, mapaRequerimientos);
        } catch(Exception ex) {
            logger.error("No se ha podido mandar el correo. Motivo: {}", ex);
        }
    }

    private void generarNotificacionApoderadosLegalesAVencer15Dias() {
        logger.info("Notificando a usuarios del CEEMSP vencimiento de los requerimientos en los proximos 15 dias");
        List<EmpresaEscrituraApoderado> apoderados15Dias = empresaEscrituraApoderadoRepository.getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.now(), LocalDate.now().plusDays(15));
        List<EmpresaEscrituraApoderado> apoderadosVencidos = empresaEscrituraApoderadoRepository.getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.MIN, LocalDate.now().minusDays(1));

        apoderados15Dias.addAll(apoderadosVencidos);
        List<Map> mapaRequerimientos = apoderados15Dias.stream().map(apoderado -> {
            EmpresaEscritura escritura = empresaEscrituraRepository.getOne(apoderado.getEscritura());

            if(escritura == null) {
                logger.warn("La escritura no existe en la base de datos");
                throw new NotFoundResourceException();
            }

            Empresa empresa = empresaRepository.getOne(escritura.getEmpresa());
            if(empresa == null) {
                logger.warn("La empresa no existe en la base de datos");
                throw new NotFoundResourceException();
            }

            Map<String, String> map = new HashMap<>();
            map.put("nombreComercialEmpresa", empresa.getNombreComercial());
            map.put("razonSocialEmpresa", empresa.getRazonSocial());
            map.put("registroEmpresa", empresa.getRegistro());
            map.put("nombreApoderado", apoderado.getNombres() + " " + apoderado.getApellidos() + " " + apoderado.getApellidoMaterno());
            map.put("numeroEscritura", escritura.getNumeroEscritura());
            map.put("fecha", apoderado.getFechaFin().toString());
            map.put("vencido", LocalDate.now().isAfter(apoderado.getFechaFin()) ? "SI" : "NO");
            return map;
        }).collect(Collectors.toList());

        try {
            emailService.sendInternalMail(NotificacionInternalEmailEnum.APODERADOS_VENCIMIENTO_15_DIAS, CORREO_ELECTRONICO, mapaRequerimientos);
        } catch(Exception ex) {
            logger.error("No se ha podido mandar el correo. Motivo: {}", ex);
        }
    }

    private void generarNotificacionLicenciasFederales15Dias() {
        logger.info("Notificando a usuarios del CEEMSP vencimiento de las licencias federales en los proximos 15 dias");

        List<Empresa> empresas15Dias = empresaRepository.getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.now(), LocalDate.now().plusDays(15));
        List<Empresa> empresasVencidas = empresaRepository.getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.MIN, LocalDate.now().minusDays(1));

        empresas15Dias.addAll(empresasVencidas);
        List<Map> mapaRequerimientos = empresas15Dias.stream().map(empresa -> {

            Map<String, String> map = new HashMap<>();
            map.put("nombreComercialEmpresa", empresa.getNombreComercial());
            map.put("razonSocialEmpresa", empresa.getRazonSocial());
            map.put("registroEmpresa", empresa.getRegistro());
            map.put("fecha", empresa.getFechaFin().toString());
            map.put("vencido", LocalDate.now().isAfter(empresa.getFechaFin()) ? "SI" : "NO");
            return map;
        }).collect(Collectors.toList());

        try {
            emailService.sendInternalMail(NotificacionInternalEmailEnum.EMPRESAS_LICENCIA_FEDERAL_VENCIMIENTO_15_DIAS, CORREO_ELECTRONICO, mapaRequerimientos);
        } catch(Exception ex) {
            logger.error("No se ha podido mandar el correo. Motivo: {}", ex);
        }
    }

    private void generarNotificacionLicenciasParticulares15Dias() {
        logger.info("Notificando a usuarios del CEEMSP vencimiento de las licencias federales en los proximos 15 dias");

        List<EmpresaLicenciaColectiva> licenciasColectivas15Dias = empresaLicenciaColectivaRepository.getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate.now().plusDays(15), LocalDate.now().minusDays(3650));

        List<Map> mapaRequerimientos = licenciasColectivas15Dias.stream().map(empresaLicenciaColectiva -> {
            Empresa empresa = empresaRepository.getOne(empresaLicenciaColectiva.getEmpresa());

            if(empresa == null) {
                logger.warn("La empresa no fue encontrada en la base de datos");
                throw new NotFoundResourceException();
            }

            Map<String, String> map = new HashMap<>();
            map.put("nombreComercialEmpresa", empresa.getNombreComercial());
            map.put("razonSocialEmpresa", empresa.getRazonSocial());
            map.put("registroEmpresa", empresa.getRegistro());
            map.put("numeroLicencia", empresaLicenciaColectiva.getNumeroOficio());
            map.put("fecha", empresaLicenciaColectiva.getFechaFin().toString());
            map.put("vencido", LocalDate.now().isAfter(empresaLicenciaColectiva.getFechaFin()) ? "SI" : "NO");
            return map;
        }).collect(Collectors.toList());

        try {
            emailService.sendInternalMail(NotificacionInternalEmailEnum.EMPRESAS_LICENCIA_COLECTIVA_VENCIMIENTO_15_DIAS, CORREO_ELECTRONICO, mapaRequerimientos);
        } catch(Exception ex) {
            logger.error("No se ha podido mandar el correo. Motivo: {}", ex);
        }
    }

    @Transactional
    public void enviarNotificacion(NotificacionCorreoEnum notificacionCorreoEnum) {
        if(notificacionCorreoEnum == null) {
            logger.warn("El tipo de notificacion viene como nulo o invalido");
            throw new InvalidDataException();
        }

        switch (notificacionCorreoEnum) {
            case ACUERDOS_VENCER_15_DIAS:
                generarNotificacionAcuerdosAVencer15Dias();
                break;
            case APODERADOS_LEGALES_VENCER_15_DIAS:
                generarNotificacionApoderadosLegalesAVencer15Dias();
                break;
            case EMPRESAS_INFORMES_MENSUALES_NO_PRESENTADOS:

                break;
            case LICENCIAS_FEDERALES_VENCER_15_DIAS:
                generarNotificacionLicenciasFederales15Dias();
                break;
            case LICENCIAS_PARTICULARES_COLECTIVAS_VENCER_15_DIAS:
                generarNotificacionLicenciasParticulares15Dias();
                break;
            case REQUERIMIENTOS_VENCER_15_DIAS:
                generarNotificacionRequerimientosAVencer15Dias();
                break;

        }
    }

    @Scheduled(cron = "0 0 7 * * *")
    @Transactional
    public void obtenerProximosElementosAVencer() {
        generarNotificacionRequerimientosAVencer15Dias();
        generarNotificacionAcuerdosAVencer15Dias();
        generarNotificacionLicenciasFederales15Dias();
        generarNotificacionLicenciasParticulares15Dias();
        generarNotificacionApoderadosLegalesAVencer15Dias();
    }

    /*@Scheduled(fixedRate = 300000L)
    public void notificarProximosElementosAVencer() {
        logger.info("Se estan enviando los correos electronicos de los elementos proximos a vencer");
        logger.info("Enviando licencias federales a vencer en los proximos 15 dias");
        logger.info("Enviando acuerdos a vencer en los proximos 15 dias");
        logger.info("Enviando requerimientos proximos a vencer en los proximos 15 dias");
        logger.info("Enviando licencias particulares colectivas a vencer en los proximos 15 dias");

        logger.info("Enviando licencias federales a vencer en los proximos 7 dias");

        logger.info("Enviando licencias federales a vencer el dia de hoy");
    }*/
}
