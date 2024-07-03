package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;
    private final IncidenciaRepository incidenciaRepository;
    private final VisitaRepository visitaRepository;
    private final AcuerdoRepository acuerdoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaReporteMensualRepository empresaReporteMensualRepository;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;
    private final EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final EmpresaService empresaService;

    @Autowired
    public DashboardServiceImpl(EmpresaRepository empresaRepository, IncidenciaRepository incidenciaRepository,
                                VisitaRepository visitaRepository, DaoToDtoConverter daoToDtoConverter,
                                UsuarioService usuarioService, AcuerdoRepository acuerdoRepository,
                                EmpresaReporteMensualRepository empresaReporteMensualRepository,
                                EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository,
                                EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository,
                                EmpresaEscrituraRepository empresaEscrituraRepository, EmpresaService empresaService) {
        this.empresaRepository = empresaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.visitaRepository = visitaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
        this.acuerdoRepository = acuerdoRepository;
        this.empresaReporteMensualRepository = empresaReporteMensualRepository;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.empresaService = empresaService;
    }

    @Override
    public DashboardDto obtenerDatosDashboard(String username) {
        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        DashboardDto dashboardDto = new DashboardDto();

        List<Empresa> empresasTodas = empresaRepository.getAllByEliminadoFalse();
        List<EmpresaReporteMensual> informesMensualesMes = empresaReporteMensualRepository.findByFechaCreacionGreaterThanAndFechaCreacionLessThanAndEliminadoFalse(YearMonth.now().atDay(1).atTime(0, 0, 0), YearMonth.now().atEndOfMonth().atTime(23, 59, 59));
        List<EmpresaEscrituraApoderado> empresaescrituraApoderadosProximosAVencer = empresaEscrituraApoderadoRepository.getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate.now().plusDays(15), LocalDate.now().minusDays(30));

        List<Integer> idsEmpresasConInformesMensuales = informesMensualesMes.stream()
                        .map(EmpresaReporteMensual::getEmpresa)
                        .collect(Collectors.toList());

        dashboardDto.setEmpresasConInformeMensual(empresasTodas.stream()
                .filter(e -> e.getStatus() == EmpresaStatusEnum.ACTIVA)
                .filter(e -> idsEmpresasConInformesMensuales.contains(e.getId()))
                .map(daoToDtoConverter::convertDaoToDtoEmpresa)
                .collect(Collectors.toList()));

        dashboardDto.setEmpresasSinInformeMensual(empresasTodas.stream()
                .filter(e -> e.getStatus() == EmpresaStatusEnum.ACTIVA)
                .filter(e -> !idsEmpresasConInformesMensuales.contains(e.getId()))
                .map(daoToDtoConverter::convertDaoToDtoEmpresa)
                .collect(Collectors.toList()));

        dashboardDto.setTotalEmpresasRegistradas(empresaRepository.countAllByEliminadoFalse());

        dashboardDto.setEmpresasRegistroFederal(empresaRepository.countAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum.EAFJAL));
        dashboardDto.setEmpresasAutorizacionEstatal(empresaRepository.countAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum.SPSMD));
        dashboardDto.setEmpresasAutorizacionProvisional(empresaRepository.countAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum.AP));
        dashboardDto.setEmpresasServiciosPropios(empresaRepository.countAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum.RESPRO));

        dashboardDto.setEmpresasActivas(empresaRepository.countAllByStatusAndEliminadoFalse(EmpresaStatusEnum.ACTIVA));
        dashboardDto.setEmpresasPerdidaEficacia(empresaRepository.countAllByStatusAndEliminadoFalse(EmpresaStatusEnum.PERDIDA_EFICACIA));
        dashboardDto.setEmpresasSuspendidas(empresaRepository.countAllByStatusAndEliminadoFalse(EmpresaStatusEnum.SUSPENDIDA));
        dashboardDto.setEmpresasRevocadas(empresaRepository.countAllByStatusAndEliminadoFalse(EmpresaStatusEnum.REVOCADA));
        dashboardDto.setEmpresasClausuradas(empresaRepository.countAllByStatusAndEliminadoFalse(EmpresaStatusEnum.CLAUSURADA));

        List<Visita> visitas = visitaRepository.getAllByFechaVisitaGreaterThanAndFechaVisitaLessThanAndEliminadoFalse(LocalDate.now(), LocalDate.now().plusDays(15));
        List<Acuerdo> acuerdosProximos = acuerdoRepository.getAllByFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(LocalDate.now().minusDays(3650), LocalDate.now().plusDays(15));
        List<Empresa> licenciasFederalesProximas = empresaRepository.getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate.now().plusDays(15), LocalDate.now().minusDays(3650));
        List<Visita> visitasRequerimientosProximos = visitaRepository.getAllByFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.now(), LocalDate.now().plusDays(15));
        List<EmpresaLicenciaColectiva> empresasLicenciasColectivasProximas = empresaLicenciaColectivaRepository.getAllByFechaFinLessThanAndFechaFinGreaterThanAndEliminadoFalse(LocalDate.now().plusDays(15), LocalDate.now().minusDays(3650));
        List<Incidencia> misIncidencias = incidenciaRepository.getAllByStatusAndAsignadoAndEliminadoFalse(IncidenciaStatusEnum.ASIGNADA, usuario.getId());
        List<Incidencia> incidenciasAbiertas = incidenciaRepository.getAllByStatusInAndEliminadoFalse(Arrays.asList(IncidenciaStatusEnum.ABIERTA, IncidenciaStatusEnum.ASIGNADA, IncidenciaStatusEnum.CONTESTADA, IncidenciaStatusEnum.ACCION_PENDIENTE));

        dashboardDto.setAcuerdosProximosAVencer(acuerdosProximos.stream()
                .filter(x -> !x.getEliminado()).map(a -> {
            AcuerdoDto acuerdoDto = daoToDtoConverter.convertDaoToDtoAcuerdo(a);
            Empresa empresa = empresaRepository.getOne(a.getEmpresa());
            acuerdoDto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresa));
            return acuerdoDto;
        }).collect(Collectors.toList()));
        dashboardDto.setProximasVisitas(visitas.stream().map(v -> {
            VisitaDto visita = daoToDtoConverter.convertDaoToDtoVisita(v);
            visita.setResponsable(usuarioService.getUserById(v.getResponsable()));
            return visita;
        }).collect(Collectors.toList()));
        dashboardDto.setRequerimientosProximosAVencer(visitasRequerimientosProximos.stream().map(v -> {
            VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(v);
            return visitaDto;
        }).collect(Collectors.toList()));
        dashboardDto.setApoderadosProximosAVencer(empresaescrituraApoderadosProximosAVencer.stream().map(a -> {
            EmpresaEscrituraApoderadoDto eea = daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(a);
            EmpresaEscritura empresaEscritura = empresaEscrituraRepository.getOne(a.getEscritura());
            eea.setEscritura(daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscritura));
            eea.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(empresaEscritura.getEmpresa())));
            return eea;
        }).collect(Collectors.toList()));
        dashboardDto.setLicenciasParticularesProximasAVencer(empresasLicenciasColectivasProximas.stream().map(e -> {
            EmpresaLicenciaColectivaDto elc = daoToDtoConverter.convertDaoToDtoEmpresaLicenciaColectiva(e);
            elc.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(e.getEmpresa())));
            return elc;
        }).collect(Collectors.toList()));
        dashboardDto.setLicenciasFederalesProximasAVencer(licenciasFederalesProximas.stream().map(daoToDtoConverter::convertDaoToDtoEmpresa).collect(Collectors.toList()));
        dashboardDto.setIncidenciasAbiertas(incidenciasAbiertas.stream().map(i -> {
            IncidenciaDto incidenciaDto = daoToDtoConverter.convertDaoToDtoIncidencia(i);
            incidenciaDto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(i.getEmpresa())));
            return incidenciaDto;
        }).collect(Collectors.toList()));
        dashboardDto.setMisIncidencias(misIncidencias.stream().map(i -> {
            IncidenciaDto incidenciaDto = daoToDtoConverter.convertDaoToDtoIncidencia(i);
            incidenciaDto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(i.getEmpresa())));
            return incidenciaDto;
        }).collect(Collectors.toList()));

        dashboardDto.setCantidadAcuerdosProximosAVencer(dashboardDto.getAcuerdosProximosAVencer().size());
        dashboardDto.setCantidadLicenciasFederalesProximasAVencer(licenciasFederalesProximas.size());
        dashboardDto.setCantidadRequerimientosProximosAVencer(visitasRequerimientosProximos.size());
        dashboardDto.setCantidadLicenciasParticularesProximasAVencer(empresasLicenciasColectivasProximas.size());

        return dashboardDto;
    }

    @Override
    public ConteoMensualDto obtenerMovimientosMes(String fechaInicio, String fechaFin) {
        if(StringUtils.isBlank(fechaInicio) || StringUtils.isBlank(fechaFin)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        List<ConteoMensualDto> conteos = empresaReporteMensualRepository.getSumReportesMensualesByMonthAndYear(LocalDate.parse(fechaInicio).atStartOfDay(), LocalDate.parse(fechaFin).atTime(23, 59, 59));

        if(conteos.isEmpty()) {
            return null;
        }

        return conteos.get(0);
    }

    @Override
    public List<EmpresaReporteMensualDto> obtenerMovimientosMesEmpresas(String fechaInicio, String fechaFin) {
        if(StringUtils.isBlank(fechaInicio) || StringUtils.isBlank(fechaFin)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        List<EmpresaReporteMensual> reportesMensualesPorMes = empresaReporteMensualRepository.findByFechaCreacionGreaterThanAndFechaCreacionLessThanAndEliminadoFalse(LocalDate.parse(fechaInicio).atStartOfDay().plusMonths(1), LocalDate.parse(fechaFin).atTime(23, 59, 59).plusMonths(1));
        return reportesMensualesPorMes.stream()
                .map(r -> {
                    EmpresaReporteMensualDto empresaReporteMensualDto = daoToDtoConverter.convertDaoToDtoEmpresaReporteMensual(r);
                    empresaReporteMensualDto.setEmpresa(empresaService.obtenerPorId(r.getEmpresa()));
                    return empresaReporteMensualDto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<EmpresaDto> obtenerEmpresasConMovimientosInformesMensuales(String fechaInicio, String fechaFin) {
        if(StringUtils.isBlank(fechaInicio) || StringUtils.isBlank(fechaFin)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        List<Empresa> empresasTodas = empresaRepository.getAllByEliminadoFalse();
        List<EmpresaReporteMensual> informesMensualesMes = empresaReporteMensualRepository.findByFechaCreacionGreaterThanAndFechaCreacionLessThanAndEliminadoFalse(LocalDate.parse(fechaInicio).atStartOfDay(), LocalDate.parse(fechaFin).atTime(23, 59, 59));
        List<Integer> idsEmpresasConInformesMensuales = informesMensualesMes.stream()
                .map(EmpresaReporteMensual::getEmpresa)
                .collect(Collectors.toList());

        return empresasTodas.stream()
                .filter(e -> e.getStatus() == EmpresaStatusEnum.ACTIVA)
                .filter(e -> idsEmpresasConInformesMensuales.contains(e.getId()))
                .map(daoToDtoConverter::convertDaoToDtoEmpresa)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaDto> obtenerEmpresasSinMovimientosInformesMensuales(String fechaInicio, String fechaFin) {
        if(StringUtils.isBlank(fechaInicio) || StringUtils.isBlank(fechaFin)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        List<Empresa> empresasTodas = empresaRepository.getAllByEliminadoFalse();
        List<EmpresaReporteMensual> informesMensualesMes = empresaReporteMensualRepository.findByFechaCreacionGreaterThanAndFechaCreacionLessThanAndEliminadoFalse(LocalDate.parse(fechaInicio).atStartOfDay(), LocalDate.parse(fechaFin).atTime(23, 59, 59));
        List<Integer> idsEmpresasConInformesMensuales = informesMensualesMes.stream()
                .map(EmpresaReporteMensual::getEmpresa)
                .collect(Collectors.toList());

        return empresasTodas.stream()
                .filter(e -> e.getStatus() == EmpresaStatusEnum.ACTIVA)
                .filter(e -> !idsEmpresasConInformesMensuales.contains(e.getId()))
                .map(daoToDtoConverter::convertDaoToDtoEmpresa)
                .collect(Collectors.toList());
    }
}
