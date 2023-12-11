package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.AcuerdoDto;
import com.pelisat.cesp.ceemsp.database.dto.DashboardEmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.database.model.Acuerdo;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private final IncidenciaRepository incidenciaRepository;
    private final VisitaRepository visitaRepository;
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
    private final AcuerdoRepository acuerdoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final ClienteRepository clienteRepository;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;

    @Autowired
    public DashboardServiceImpl(IncidenciaRepository incidenciaRepository, VisitaRepository visitaRepository, UsuarioService usuarioService,
                                PersonaRepository personaRepository, AcuerdoRepository acuerdoRepository, DaoToDtoConverter daoToDtoConverter,
                                ClienteRepository clienteRepository,
                                EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.visitaRepository = visitaRepository;
        this.usuarioService = usuarioService;
        this.personaRepository = personaRepository;
        this.acuerdoRepository = acuerdoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.clienteRepository = clienteRepository;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
    }

    @Override
    public DashboardEmpresaDto obtenerDatosDashboard(String username) {
        if(username == null) {
            logger.warn("Algunos de los campos vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        DashboardEmpresaDto dashboardEmpresaDto = new DashboardEmpresaDto();
        dashboardEmpresaDto.setIncidenciasPendientes(incidenciaRepository.countAllByStatusAndEmpresaAndEliminadoFalse(IncidenciaStatusEnum.ACCION_PENDIENTE, usuarioDto.getEmpresa().getId()));
        dashboardEmpresaDto.setIncidenciasAbiertas(incidenciaRepository.countAllByStatusAndEmpresaAndEliminadoFalse(IncidenciaStatusEnum.ABIERTA, usuarioDto.getEmpresa().getId()));
        dashboardEmpresaDto.setIncidenciasProcedentes(incidenciaRepository.countAllByStatusAndEmpresaAndEliminadoFalse(IncidenciaStatusEnum.PROCEDENTE, usuarioDto.getEmpresa().getId()));
        dashboardEmpresaDto.setIncidenciasImprocedentes(incidenciaRepository.countAllByStatusAndEmpresaAndEliminadoFalse(IncidenciaStatusEnum.IMPROCEDENTE, usuarioDto.getEmpresa().getId()));
        dashboardEmpresaDto.setIncidenciasContestadas(incidenciaRepository.countAllByStatusAndEmpresaAndEliminadoFalse(IncidenciaStatusEnum.CONTESTADA, usuarioDto.getEmpresa().getId()));

        dashboardEmpresaDto.setPersonalActivo(personaRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId()).size());
        dashboardEmpresaDto.setAcuerdosProximosAVencer(acuerdoRepository.getAllByEmpresaAndFechaFinGreaterThanEqualAndEliminadoFalse(usuarioDto.getEmpresa().getId(), LocalDate.now()).size());
        dashboardEmpresaDto.setAcuerdosTotales(acuerdoRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId()).size());
        dashboardEmpresaDto.setRequerimientosProximosAVencer(visitaRepository.getAllByEmpresaAndFechaTerminoGreaterThanEqualAndEliminadoFalse(usuarioDto.getEmpresa().getId(), LocalDate.now()).size());

        List<Visita> visitas = visitaRepository.getAllByFechaVisitaGreaterThanAndEmpresaAndFechaVisitaLessThanAndEliminadoFalse(LocalDate.now(), usuarioDto.getEmpresa().getId(), LocalDate.now().plusDays(15));
        List<Acuerdo> acuerdosProximos = acuerdoRepository.getAllByEmpresaAndFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), LocalDate.now(), LocalDate.now().plusDays(15));
        List<Visita> visitasRequerimientosProximos = visitaRepository.getAllByEmpresaAndFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), LocalDate.now(), LocalDate.now().plusDays(15));
        dashboardEmpresaDto.setClientesProximosAVencer(clienteRepository.countByEmpresaAndFechaFinGreaterThanAndFechaFinLessThanAndEliminadoFalse(usuarioDto.getEmpresa().getId(), LocalDate.now(), LocalDate.now().plusDays(15)));

        if(usuarioDto.getEmpresa().getTipoTramite() == TipoTramiteEnum.EAFJAL) {
            dashboardEmpresaDto.setDiasRestantesLicenciaFederal(Duration.ofDays(DAYS.between(LocalDate.now(), LocalDate.parse(usuarioDto.getEmpresa().getFechaFin()))).toDays());
            dashboardEmpresaDto.setLicenciasColectivasProximasAVencer(empresaLicenciaColectivaRepository.getAllByEmpresaAndFechaTerminoLessThanAndFechaTerminoGreaterThanAndEliminadoFalse(LocalDate.now(), LocalDate.parse(usuarioDto.getEmpresa().getFechaFin()).plusDays(14), usuarioDto.getEmpresa().getId()).size());
        }

        dashboardEmpresaDto.setListaAcuerdosProximosAVencer(acuerdosProximos.stream().map(a -> {
            AcuerdoDto acuerdoDto = daoToDtoConverter.convertDaoToDtoAcuerdo(a);
            return acuerdoDto;
        }).collect(Collectors.toList()));
        dashboardEmpresaDto.setProximasVisitas(visitas.stream().map(v -> {
            VisitaDto visita = daoToDtoConverter.convertDaoToDtoVisita(v);
            visita.setResponsable(usuarioService.getUserById(v.getResponsable()));
            return visita;
        }).collect(Collectors.toList()));
        dashboardEmpresaDto.setListaRequerimientosProximosAVencer(visitasRequerimientosProximos.stream().map(v -> {
            VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(v);
            return visitaDto;
        }).collect(Collectors.toList()));

        return dashboardEmpresaDto;
    }
}
