package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;
    private final IncidenciaRepository incidenciaRepository;
    private final VisitaRepository visitaRepository;
    private final DaoToDtoConverter daoToDtoConverter;

    @Autowired
    public DashboardServiceImpl(EmpresaRepository empresaRepository, IncidenciaRepository incidenciaRepository,
                                VisitaRepository visitaRepository, DaoToDtoConverter daoToDtoConverter,
                                UsuarioService usuarioService) {
        this.empresaRepository = empresaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.visitaRepository = visitaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
    }

    @Override
    public DashboardDto obtenerDatosDashboard() {
        DashboardDto dashboardDto = new DashboardDto();

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

        dashboardDto.setIncidenciasAbiertas(incidenciaRepository.countAllByStatusAndEliminadoFalse(IncidenciaStatusEnum.ABIERTA));

        List<Visita> visitas = visitaRepository.getAllByFechaVisitaGreaterThanEqualAndEliminadoFalse(LocalDate.now());
        dashboardDto.setProximasVisitas(visitas.stream().map(v -> {
            VisitaDto visita = daoToDtoConverter.convertDaoToDtoVisita(v);
            visita.setResponsable(usuarioService.getUserById(v.getResponsable()));
            return visita;
        }).collect(Collectors.toList()));

        return dashboardDto;
    }
}
