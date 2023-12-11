package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaInformeMensualServiceImpl implements EmpresaInformeMensualService {
    private final EmpresaRepository empresaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaReporteMensualRepository empresaReporteMensualRepository;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final PersonaRepository personaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final Logger logger = LoggerFactory.getLogger(EmpresaInformeMensualService.class);

    @Autowired
    public EmpresaInformeMensualServiceImpl(EmpresaRepository empresaRepository, DaoToDtoConverter daoToDtoConverter,
                                            EmpresaReporteMensualRepository empresaReporteMensualRepository, UsuarioService usuarioService,
                                            DaoHelper<CommonModel> daoHelper, PersonaRepository personaRepository, ClienteRepository clienteRepository,
                                            VehiculoRepository vehiculoRepository) {
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaReporteMensualRepository = empresaReporteMensualRepository;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.personaRepository = personaRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public List<EmpresaReporteMensualDto> listarReportes(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);
        List<EmpresaReporteMensual> reportes = empresaReporteMensualRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());

        return reportes.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaReporteMensual)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaReporteMensualDto descargarReporteUuid(String uuid, String reporteUuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el reporte con el uuid [{}]", uuid);

        EmpresaReporteMensual reporte = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporte == null) {
            logger.warn("El reporte no existe con el uuid dado [{}]", uuid);
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaReporteMensual(reporte);
    }

    @Override
    public EmpresaReporteMensualDto eliminarReporteUuid(String uuid, String reporteUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando un reporte con uuid [{}]", uuid);

        EmpresaReporteMensual reporte = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporte == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        reporte.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, reporte, usuarioDto.getId());
        empresaReporteMensualRepository.save(reporte);
        return daoToDtoConverter.convertDaoToDtoEmpresaReporteMensual(reporte);
    }

    @Override
    public List<PersonaDto> obtenerAltasPersonalPorReporte(String uuid, String reporteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Personal> altasPersonal = personaRepository.findAllByEmpresaAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasPersonal.stream()
                .map(daoToDtoConverter::convertDaoToDtoPersona)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaDto> obtenerBajasPersonalPorReporte(String uuid, String reporteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Personal> bajasPersonal = personaRepository.findAllByEmpresaAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasPersonal.stream()
                .map(daoToDtoConverter::convertDaoToDtoPersona)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteDto> obtenerAltasClientePorReporte(String uuid, String reporteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Cliente> altasClientes = clienteRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasClientes.stream()
                .map(daoToDtoConverter::convertDaoToDtoCliente)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteDto> obtenerBajasClientePorReporte(String uuid, String reporteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Cliente> bajasClientes = clienteRepository.findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasClientes.stream()
                .map(daoToDtoConverter::convertDaoToDtoCliente)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehiculoDto> obtenerAltasVehiculoPorReporte(String uuid, String reporteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Vehiculo> altasVehiculos= vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasVehiculos.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculo)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehiculoDto> obtenerBajasVehiculoPorReporte(String uuid, String reporteUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Vehiculo> bajasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasVehiculos.stream()
                .map(daoToDtoConverter::convertDaoToDtoVehiculo)
                .collect(Collectors.toList());
    }


}
