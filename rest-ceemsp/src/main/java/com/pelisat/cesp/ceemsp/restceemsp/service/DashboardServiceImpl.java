package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.DashboardDto;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private final EmpresaRepository empresaRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final VisitaRepository visitaRepository;

    @Autowired
    public DashboardServiceImpl(EmpresaRepository empresaRepository, IncidenciaRepository incidenciaRepository,
                                VisitaRepository visitaRepository) {
        this.empresaRepository = empresaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.visitaRepository = visitaRepository;
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

        return dashboardDto;
    }
}
