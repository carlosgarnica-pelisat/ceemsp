package com.pelisat.cesp.ceemsp.restceemsp.task;

import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.ReporteArgos;
import com.pelisat.cesp.ceemsp.database.repository.ReporteArgosRepository;
import com.pelisat.cesp.ceemsp.database.type.ReporteArgosStatusEnum;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.restceemsp.service.ReporteoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
public class ReporteoTask<T extends CommonModel> {
    private Logger logger = LoggerFactory.getLogger(ReporteoTask.class);
    private ReporteoService reporteoService;
    private ReporteArgosRepository reporteArgosRepository;
    private DaoHelper<CommonModel> daoHelper;
    private ArchivosService archivosService;

    @Autowired
    public ReporteoTask(ReporteoService reporteoService, ReporteArgosRepository reporteArgosRepository, DaoHelper<CommonModel> daoHelper,
                        ArchivosService archivosService) {
        this.reporteoService = reporteoService;
        this.reporteArgosRepository = reporteArgosRepository;
        this.daoHelper = daoHelper;
        this.archivosService = archivosService;
    }

    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void procesarReportesPendientes() {
        logger.info("Se estan procesando los reportes.");
        List<ReporteArgos> reportesPendientes = reporteArgosRepository.getAllByStatusAndEliminadoFalse(ReporteArgosStatusEnum.PENDIENTE);

        if(reportesPendientes.size() < 1) {
            logger.info("No hay reportes para procesar en este momento");
            return;
        }

        reportesPendientes.forEach(r -> {
            r.setStatus(ReporteArgosStatusEnum.PROCESANDO);
            daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
            reporteArgosRepository.save(r);

            switch (r.getTipo()) {
                case LISTADO_NOMINAL:
                    try {
                        File file = reporteoService.generarReporteListadoNominal(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case PADRON_ESTATAL:
                    try {
                        File file = reporteoService.generarReportePadronEmpresas(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case INTERCAMBIO_INFORMACION:
                    try {
                        File file = reporteoService.generarReporteIntercambioInformacion(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        logger.warn("Fallo: {}", ex);
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case ACUERDOS:
                    try {
                        File file = reporteoService.generarReporteAcuerdos(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case PERSONAL:
                    try {
                        File file = reporteoService.generarReportePersonal(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case ESCRITURAS:
                    try {
                        File file = reporteoService.generarReporteEscrituras(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case CANES:
                    try {
                        File file = reporteoService.generarReporteCanes(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case VEHICULOS:
                    try {
                        File file = reporteoService.generarReporteVehiculos(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case CLIENTES:
                    try {
                        File file = reporteoService.generarReporteClientes(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case ARMAS:
                    try {
                        File file = reporteoService.generarReporteArmas(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case LICENCIAS_COLECTIVAS:
                    try {
                        File file = reporteoService.generarReporteLicenciasColectivas(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
                case VISITAS:
                    try {
                        File file = reporteoService.generarReporteVisitas(r.getFechaInicio(), r.getFechaFin());
                        r.setStatus(ReporteArgosStatusEnum.COMPLETADO);
                        r.setRutaArchivo(file.getAbsolutePath());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    } catch(Exception ex) {
                        r.setStatus(ReporteArgosStatusEnum.ERROR);
                        r.setRazon(ex.getMessage());
                        daoHelper.fulfillAuditorFields(false, r, r.getActualizadoPor());
                        reporteArgosRepository.save(r);
                    }
                    break;
            }
        });
    }
}
