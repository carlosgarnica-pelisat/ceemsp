package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ReporteArgosDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.ReporteArgos;
import com.pelisat.cesp.ceemsp.database.repository.ReporteArgosRepository;
import com.pelisat.cesp.ceemsp.database.type.ReporteArgosStatusEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteArgosServiceImpl implements ReporteArgosService {
    private final ReporteArgosRepository reporteArgosRepository;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final Logger logger = LoggerFactory.getLogger(ReporteArgosService.class);

    @Autowired
    public ReporteArgosServiceImpl(ReporteArgosRepository reporteArgosRepository, DaoHelper<CommonModel> daoHelper,
                                   UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter) {
        this.reporteArgosRepository = reporteArgosRepository;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
    }
    @Override
    public List<ReporteArgosDto> obtenerReportes() {
        return reporteArgosRepository.findAll()
                .stream()
                .map(daoToDtoConverter::convertDaoToDtoReporteArgos)
                .collect(Collectors.toList());
    }

    @Override
    public File descargarReporte(String reporteUuid) {
        if(StringUtils.isBlank(reporteUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando el reporte con uuid [{}]", reporteUuid);

        ReporteArgos reporteArgos = reporteArgosRepository.findByUuidAndEliminadoFalse(reporteUuid);

        if(reporteArgos == null) {
            logger.warn("El reporte no se encontro en la base de datos");
            throw new NotFoundResourceException();
        }

        if(reporteArgos.getStatus() != ReporteArgosStatusEnum.COMPLETADO) {
            logger.warn("El reporte no se encuentra en un status valido");
            throw new NotFoundResourceException();
        }

        return new File(reporteArgos.getRutaArchivo());
    }

    @Override
    public ReporteArgosDto obtenerReportePorUuid(String reporteUuid) {
        if(StringUtils.isBlank(reporteUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando el reporte con uuid [{}]", reporteUuid);

        ReporteArgos reporteArgos = reporteArgosRepository.findByUuidAndEliminadoFalse(reporteUuid);

        if(reporteArgos == null) {
            logger.warn("El reporte no se encontro en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoReporteArgos(reporteArgos);
    }

    @Override
    public ReporteArgosDto crearReporte(ReporteArgosDto reporteArgosDto, String username) {
        if(reporteArgosDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo reporte");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        ReporteArgos reporteArgos = new ReporteArgos();
        reporteArgos.setUuid(RandomStringUtils.randomAlphanumeric(12));
        reporteArgos.setTipo(reporteArgosDto.getTipo());
        reporteArgos.setStatus(ReporteArgosStatusEnum.PENDIENTE);
        if(StringUtils.isNotBlank(reporteArgosDto.getFechaInicio()) && StringUtils.isNotBlank(reporteArgosDto.getFechaFin())) {
            reporteArgos.setFechaInicio(LocalDate.parse(reporteArgosDto.getFechaInicio()));
            reporteArgos.setFechaFin(LocalDate.parse(reporteArgosDto.getFechaFin()));
        }
        daoHelper.fulfillAuditorFields(true, reporteArgos, usuarioDto.getId());

        reporteArgosRepository.save(reporteArgos);

        return daoToDtoConverter.convertDaoToDtoReporteArgos(reporteArgos);
    }

    @Override
    public ReporteArgosDto eliminarReporte(String reporteUuid, String username) {
        return null;
    }
}
