package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.ReporteMovimientoEnum;
import com.pelisat.cesp.ceemsp.database.type.ReporteTipoEnum;
import com.pelisat.cesp.ceemsp.database.type.RolTypeEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class EmpresaInformeMensualServiceImpl implements EmpresaInformeMensualService {
    private final EmpresaRepository empresaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaReporteMensualRepository empresaReporteMensualRepository;
    private final EmpresaReporteMensualMovimientoRepository empresaReporteMensualMovimientoRepository;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final PersonaRepository personaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final VehiculoTipoService vehiculoTipoService;
    private final VehiculoMarcaService vehiculoMarcaService;
    private final VehiculoSubmarcaService vehiculoSubmarcaService;
    private final PersonalPuestoDeTrabajoService personalPuestoDeTrabajoService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaInformeMensualService.class);
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;
    private final ArmaRepository armaRepository;
    private final ArmaClaseService armaClaseService;
    private final ArmaMarcaService armaMarcaService;
    private final CanRepository canRepository;
    private final CanRazaService canRazaService;

    @Autowired
    public EmpresaInformeMensualServiceImpl(EmpresaRepository empresaRepository, DaoToDtoConverter daoToDtoConverter,
                                            EmpresaReporteMensualRepository empresaReporteMensualRepository, UsuarioService usuarioService,
                                            DaoHelper<CommonModel> daoHelper, PersonaRepository personaRepository, ClienteRepository clienteRepository,
                                            VehiculoRepository vehiculoRepository, VehiculoTipoService vehiculoTipoService, VehiculoMarcaService vehiculoMarcaService,
                                            VehiculoSubmarcaService vehiculoSubmarcaService, PersonalPuestoDeTrabajoService personalPuestoDeTrabajoService,
                                            EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository,
                                            ArmaRepository armaRepository, ArmaClaseService armaClaseService, ArmaMarcaService armaMarcaService,
                                            CanRepository canRepository, CanRazaService canRazaService, EmpresaReporteMensualMovimientoRepository empresaReporteMensualMovimientoRepository) {
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaReporteMensualRepository = empresaReporteMensualRepository;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.personaRepository = personaRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoTipoService = vehiculoTipoService;
        this.vehiculoMarcaService = vehiculoMarcaService;
        this.vehiculoSubmarcaService = vehiculoSubmarcaService;
        this.personalPuestoDeTrabajoService = personalPuestoDeTrabajoService;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
        this.armaRepository = armaRepository;
        this.armaClaseService = armaClaseService;
        this.armaMarcaService = armaMarcaService;
        this.canRepository = canRepository;
        this.canRazaService = canRazaService;
        this.empresaReporteMensualMovimientoRepository = empresaReporteMensualMovimientoRepository;
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
    public File generarInformeMensualExcel(String uuid, String reporteUuid, String username) throws IOException {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid viene como nulo");
            throw new InvalidDataException();
        }

        logger.debug("Generando el informe mensual en excel");

        EmpresaReporteMensualDto reporte = descargarReporteUuid(uuid, reporteUuid);

        List<PersonaDto> personalActivo = obtenerActivosPersonalPorReporte(uuid, reporteUuid, username);
        List<PersonaDto> personalAlta = obtenerAltasPersonalPorReporte(uuid, reporteUuid, username);
        List<PersonaDto> personalBaja = obtenerBajasPersonalPorReporte(uuid, reporteUuid, username);

        List<VehiculoDto> vehiculosActivos = obtenerActivosVehiculosPorReporte(uuid, reporteUuid);
        List<VehiculoDto> vehiculosAlta = obtenerAltasVehiculoPorReporte(uuid, reporteUuid);
        List<VehiculoDto> vehiculosBaja = obtenerBajasVehiculoPorReporte(uuid, reporteUuid);

        // Generando paginas del excel
        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/informes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet informacionReporteSheet = workbook.createSheet("INFORMACION GENERAL");

        Sheet personalActivoSheet = workbook.createSheet("PERSONAL ACTIVO");
        Sheet personalAltasSheet = workbook.createSheet("PERSONAL ALTAS");
        Sheet personalBajasSheet = workbook.createSheet("PERSONAL BAJAS");

        Sheet vehiculosActivosSheet = workbook.createSheet("VEHICULOS ACTIVO");
        Sheet vehiculosAltasSheet = workbook.createSheet("VEHICULOS ALTAS");
        Sheet vehiculosBajasSheet = workbook.createSheet("VEHICULOS BAJAS");

        // Creando prestadores de servicios
        Row personalActivoEncabezadoReporteRow = personalActivoSheet.createRow(0);

        Cell personalActivoNoCell = personalActivoEncabezadoReporteRow.createCell(0);
        personalActivoNoCell.setCellStyle(style);
        Cell personalActivoPuestoEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(1);
        personalActivoPuestoEncabezadoCell.setCellStyle(style);
        Cell personalActivoTipoMovimientoEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(2);
        personalActivoTipoMovimientoEncabezadoCell.setCellStyle(style);
        Cell personalActivoFechaCreacionEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(3);
        personalActivoFechaCreacionEncabezadoCell.setCellStyle(style);
        Cell personalActivoApellidoPaternoEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(4);
        personalActivoApellidoPaternoEncabezadoCell.setCellStyle(style);
        Cell personalActivoApellidoMaternoEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(5);
        personalActivoApellidoMaternoEncabezadoCell.setCellStyle(style);
        Cell personalActivoNombresEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(6);
        personalActivoNombresEncabezadoCell.setCellStyle(style);
        Cell personalActivoCurpEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(7);
        personalActivoCurpEncabezadoCell.setCellStyle(style);
        Cell personalActivoEstatusCuipEncabezadoCell = personalActivoEncabezadoReporteRow.createCell(8);
        personalActivoEstatusCuipEncabezadoCell.setCellStyle(style);

        personalActivoNoCell.setCellValue("NO. CONSECUTIVO");
        personalActivoPuestoEncabezadoCell.setCellValue("PUESTO");
        personalActivoTipoMovimientoEncabezadoCell.setCellValue("TIPO MOVIMIENTO");
        personalActivoFechaCreacionEncabezadoCell.setCellValue("FECHA CREACION");
        personalActivoApellidoPaternoEncabezadoCell.setCellValue("APELLIDO PATERNO");
        personalActivoApellidoMaternoEncabezadoCell.setCellValue("APELLIDO MATERNO");
        personalActivoNombresEncabezadoCell.setCellValue("NOMBRES");
        personalActivoCurpEncabezadoCell.setCellValue("CURP");
        personalActivoEstatusCuipEncabezadoCell.setCellValue("ESTATUS CUIP");


        Row personalAltasEncabezadoReporteRow = personalAltasSheet.createRow(0);

        Cell personalAltasNoCell = personalAltasEncabezadoReporteRow.createCell(0);
        personalAltasNoCell.setCellStyle(style);
        Cell personalAltasPuestoEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(1);
        personalAltasPuestoEncabezadoCell.setCellStyle(style);
        Cell personalAltasTipoMovimientoEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(2);
        personalAltasTipoMovimientoEncabezadoCell.setCellStyle(style);
        Cell personalAltasFechaCreacionEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(3);
        personalAltasFechaCreacionEncabezadoCell.setCellStyle(style);
        Cell personalAltasApellidoPaternoEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(4);
        personalAltasApellidoPaternoEncabezadoCell.setCellStyle(style);
        Cell personalAltasApellidoMaternoEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(5);
        personalAltasApellidoMaternoEncabezadoCell.setCellStyle(style);
        Cell personalAltasNombresEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(6);
        personalAltasNombresEncabezadoCell.setCellStyle(style);
        Cell personalAltasCurpEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(7);
        personalAltasCurpEncabezadoCell.setCellStyle(style);
        Cell personalAltasEstatusCuipEncabezadoCell = personalAltasEncabezadoReporteRow.createCell(8);
        personalAltasEstatusCuipEncabezadoCell.setCellStyle(style);

        personalAltasNoCell.setCellValue("NO. CONSECUTIVO");
        personalAltasPuestoEncabezadoCell.setCellValue("PUESTO");
        personalAltasTipoMovimientoEncabezadoCell.setCellValue("TIPO MOVIMIENTO");
        personalAltasFechaCreacionEncabezadoCell.setCellValue("FECHA CREACION");
        personalAltasApellidoPaternoEncabezadoCell.setCellValue("APELLIDO PATERNO");
        personalAltasApellidoMaternoEncabezadoCell.setCellValue("APELLIDO MATERNO");
        personalAltasNombresEncabezadoCell.setCellValue("NOMBRES");
        personalAltasCurpEncabezadoCell.setCellValue("CURP");
        personalAltasEstatusCuipEncabezadoCell.setCellValue("ESTATUS CUIP");

        Row personalBajasEncabezadoReporteRow = personalBajasSheet.createRow(0);

        Cell personalBajasNoCell = personalBajasEncabezadoReporteRow.createCell(0);
        personalBajasNoCell.setCellStyle(style);
        Cell personalBajasPuestoEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(1);
        personalBajasPuestoEncabezadoCell.setCellStyle(style);
        Cell personalBajasTipoMovimientoEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(2);
        personalBajasTipoMovimientoEncabezadoCell.setCellStyle(style);
        Cell personalBajasFechaCreacionEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(3);
        personalBajasFechaCreacionEncabezadoCell.setCellStyle(style);
        Cell personalBajasApellidoPaternoEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(4);
        personalBajasApellidoPaternoEncabezadoCell.setCellStyle(style);
        Cell personalBajasApellidoMaternoEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(5);
        personalBajasApellidoMaternoEncabezadoCell.setCellStyle(style);
        Cell personalBajasNombresEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(6);
        personalBajasNombresEncabezadoCell.setCellStyle(style);
        Cell personalBajasCurpEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(7);
        personalBajasCurpEncabezadoCell.setCellStyle(style);
        Cell personalBajasEstatusCuipEncabezadoCell = personalBajasEncabezadoReporteRow.createCell(8);
        personalBajasEstatusCuipEncabezadoCell.setCellStyle(style);

        personalBajasNoCell.setCellValue("NO. CONSECUTIVO");
        personalBajasPuestoEncabezadoCell.setCellValue("PUESTO");
        personalBajasTipoMovimientoEncabezadoCell.setCellValue("TIPO MOVIMIENTO");
        personalBajasFechaCreacionEncabezadoCell.setCellValue("FECHA CREACION");
        personalBajasApellidoPaternoEncabezadoCell.setCellValue("APELLIDO PATERNO");
        personalBajasApellidoMaternoEncabezadoCell.setCellValue("APELLIDO MATERNO");
        personalBajasNombresEncabezadoCell.setCellValue("NOMBRES");
        personalBajasCurpEncabezadoCell.setCellValue("CURP");
        personalBajasEstatusCuipEncabezadoCell.setCellValue("ESTATUS CUIP");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        personalAlta.forEach(p -> {
            Row eRow = personalAltasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell puestoCell = eRow.createCell(1);
            puestoCell.setCellStyle(style);
            Cell tipoMovimientoCell = eRow.createCell(2);
            tipoMovimientoCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(3);
            fechaCreacionCell.setCellStyle(style);
            Cell apellidoPaternoCell = eRow.createCell(4);
            apellidoPaternoCell.setCellStyle(style);
            Cell apellidoMaternoCell = eRow.createCell(5);
            apellidoMaternoCell.setCellStyle(style);
            Cell nombresCell = eRow.createCell(6);
            nombresCell.setCellStyle(style);
            Cell curpCell = eRow.createCell(7);
            curpCell.setCellStyle(style);
            Cell estatusCuipCell = eRow.createCell(8);
            estatusCuipCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            puestoCell.setCellValue(p.getPuestoDeTrabajo().getNombre());
            tipoMovimientoCell.setCellValue("ALTA");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            apellidoPaternoCell.setCellValue(p.getApellidoPaterno());
            apellidoMaternoCell.setCellValue(p.getApellidoMaterno());
            nombresCell.setCellValue(p.getNombres());
            curpCell.setCellValue(p.getCurp());
            estatusCuipCell.setCellValue(p.getEstatusCuip() != null ? p.getEstatusCuip().getNombre() : "");

            consecutivo.incrementAndGet();
        });

        consecutivo.set(1);

        personalBaja.forEach(p -> {
            Row eRow = personalBajasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell puestoCell = eRow.createCell(1);
            puestoCell.setCellStyle(style);
            Cell tipoMovimientoCell = eRow.createCell(2);
            tipoMovimientoCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(3);
            fechaCreacionCell.setCellStyle(style);
            Cell apellidoPaternoCell = eRow.createCell(4);
            apellidoPaternoCell.setCellStyle(style);
            Cell apellidoMaternoCell = eRow.createCell(5);
            apellidoMaternoCell.setCellStyle(style);
            Cell nombresCell = eRow.createCell(6);
            nombresCell.setCellStyle(style);
            Cell curpCell = eRow.createCell(7);
            curpCell.setCellStyle(style);
            Cell estatusCuipCell = eRow.createCell(8);
            estatusCuipCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            puestoCell.setCellValue(p.getPuestoDeTrabajo().getNombre());
            tipoMovimientoCell.setCellValue("BAJA");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            apellidoPaternoCell.setCellValue(p.getApellidoPaterno());
            apellidoMaternoCell.setCellValue(p.getApellidoMaterno());
            nombresCell.setCellValue(p.getNombres());
            curpCell.setCellValue(p.getCurp());
            estatusCuipCell.setCellValue(p.getEstatusCuip() != null ? p.getEstatusCuip().getNombre() : "");

            consecutivo.incrementAndGet();
        });

        consecutivo.set(1);

        personalActivo.forEach(p -> {
            Row eRow = personalBajasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell puestoCell = eRow.createCell(1);
            puestoCell.setCellStyle(style);
            Cell tipoMovimientoCell = eRow.createCell(2);
            tipoMovimientoCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(3);
            fechaCreacionCell.setCellStyle(style);
            Cell apellidoPaternoCell = eRow.createCell(4);
            apellidoPaternoCell.setCellStyle(style);
            Cell apellidoMaternoCell = eRow.createCell(5);
            apellidoMaternoCell.setCellStyle(style);
            Cell nombresCell = eRow.createCell(6);
            nombresCell.setCellStyle(style);
            Cell curpCell = eRow.createCell(7);
            curpCell.setCellStyle(style);
            Cell estatusCuipCell = eRow.createCell(8);
            estatusCuipCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            puestoCell.setCellValue(p.getPuestoDeTrabajo().getNombre());
            tipoMovimientoCell.setCellValue("ACTIVO");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            apellidoPaternoCell.setCellValue(p.getApellidoPaterno());
            apellidoMaternoCell.setCellValue(p.getApellidoMaterno());
            nombresCell.setCellValue(p.getNombres());
            curpCell.setCellValue(p.getCurp());
            estatusCuipCell.setCellValue(p.getEstatusCuip() != null ? p.getEstatusCuip().getNombre() : "");

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public List<PersonaDto> obtenerActivosPersonalPorReporte(String uuid, String reporteUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        // Obteniendo los movimientos aplicables
        EmpresaReporteMensualMovimiento movimiento = empresaReporteMensualMovimientoRepository.getByReporteMensualAndTipoAndMovimientoAndEliminadoFalse(reporteMensual.getId(), ReporteTipoEnum.PERSONAL, ReporteMovimientoEnum.ACTIVOS);

        if(movimiento != null) {
            return null;
        } else {
            List<Personal> personalTotalMes = personaRepository.findAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndEliminadoFalse(
                    empresa.getId(),
                    Arrays.asList(3),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
            );

            if(usuarioDto.getRol() != RolTypeEnum.CEEMSP_READ_ONLY) {
                personalTotalMes.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndEliminadoFalse(
                        empresa.getId(),
                        Arrays.asList(3),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
                ));
            }

            List<Personal> altasPersonal = personaRepository.findAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                    empresa.getId(),
                    Arrays.asList(3),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
            );

            if(usuarioDto.getRol() != RolTypeEnum.CEEMSP_READ_ONLY) {
                altasPersonal.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                        empresa.getId(),
                        Arrays.asList(3),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
                ));
            }

            List<Personal> bajasPersonal = personaRepository.findAllByEmpresaAndPuestoInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                    empresa.getId(),
                    Arrays.asList(3),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
            );

            if(usuarioDto.getRol() != RolTypeEnum.CEEMSP_READ_ONLY) {
                bajasPersonal.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                        empresa.getId(),
                        Arrays.asList(3),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
                ));
            }

            List<Personal> temp = ListUtils.subtract(personalTotalMes, bajasPersonal);
            List<Personal> personalActivoMes = ListUtils.subtract(temp, altasPersonal);
            return personalActivoMes.stream()
                    .map(p -> {
                        PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                        dto.setPuestoDeTrabajo(personalPuestoDeTrabajoService.obtenerPorId(p.getPuesto()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<PersonaDto> obtenerAltasPersonalPorReporte(String uuid, String reporteUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensual reporteMensual = empresaReporteMensualRepository.getByUuidAndEliminadoFalse(reporteUuid);

        if(reporteMensual == null) {
            logger.warn("El reporte no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        // Obteniendo los movimientos aplicables
        EmpresaReporteMensualMovimiento movimiento = empresaReporteMensualMovimientoRepository.getByReporteMensualAndTipoAndMovimientoAndEliminadoFalse(reporteMensual.getId(), ReporteTipoEnum.PERSONAL, ReporteMovimientoEnum.ALTA);

        if(movimiento != null && StringUtils.isNotBlank(movimiento.getElementos())) {
            List<Integer> ids = Arrays.stream(movimiento.getElementos().split(", ")).map(Integer::parseInt).collect(Collectors.toList());
            List<Personal> altasPersonal = personaRepository.getAllByIdIn(ids);
            return altasPersonal.stream()
                    .map(p -> {
                        PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                        dto.setPuestoDeTrabajo(personalPuestoDeTrabajoService.obtenerPorId(p.getPuesto()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            List<Personal> altasPersonal = personaRepository.findAllByEmpresaAndPuestoInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                    empresa.getId(),
                    Arrays.asList(3),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
            );

            if(usuarioDto.getRol() != RolTypeEnum.CEEMSP_READ_ONLY) {
                altasPersonal.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaCreacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionGreaterThanAndEliminadoFalse(
                        empresa.getId(),
                        Arrays.asList(3),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
                ));
            }

            return altasPersonal.stream()
                    .map(p -> {
                        PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                        dto.setPuestoDeTrabajo(personalPuestoDeTrabajoService.obtenerPorId(p.getPuesto()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<PersonaDto> obtenerBajasPersonalPorReporte(String uuid, String reporteUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(username)) {
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

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaReporteMensualMovimiento movimiento = empresaReporteMensualMovimientoRepository.getByReporteMensualAndTipoAndMovimientoAndEliminadoFalse(reporteMensual.getId(), ReporteTipoEnum.PERSONAL, ReporteMovimientoEnum.BAJA);

        if (movimiento != null && StringUtils.isNotBlank(movimiento.getElementos())) {
            List<Integer> ids = Arrays.stream(movimiento.getElementos().split(", ")).map(Integer::parseInt).collect(Collectors.toList());
            List<Personal> bajasPersonal = personaRepository.getAllByIdIn(ids);

            return bajasPersonal.stream()
                    .map(p -> {
                        PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                        dto.setPuestoDeTrabajo(personalPuestoDeTrabajoService.obtenerPorId(p.getPuesto()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            List<Personal> bajasPersonal = personaRepository.findAllByEmpresaAndPuestoInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndCursosCapturadosTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                    empresa.getId(),
                    Arrays.asList(3),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
            );

            if(usuarioDto.getRol() != RolTypeEnum.CEEMSP_READ_ONLY) {
                bajasPersonal.addAll(personaRepository.findAllByEmpresaAndPuestoNotInAndFechaActualizacionLessThanAndPuestoTrabajoCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionGreaterThanAndEliminadoTrue(
                        empresa.getId(),
                        Arrays.asList(3),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                        YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
                ));
            }

            return bajasPersonal.stream()
                    .map(p -> {
                        PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                        dto.setPuestoDeTrabajo(personalPuestoDeTrabajoService.obtenerPorId(p.getPuesto()));
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ClienteDto> obtenerActivosClientesPorReporte(String uuid, String reporteUuid) {
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

        List<Cliente> clienteTotalMes = clienteRepository.findAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.now().minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
        );

        List<Cliente> altasClientes = clienteRepository.findAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Cliente> bajasClientes = clienteRepository.findAllByEmpresaAndDomicilioCapturadoTrueAndModalidadCapturadaTrueAndFormaEjecucionCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                empresa.getId(),
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
    public List<VehiculoDto> obtenerActivosVehiculosPorReporte(String uuid, String reporteUuid) {
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

        List<Vehiculo> vehiculosTotalMes = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.now().minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
        );

        List<Vehiculo> altasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Vehiculo> bajasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Vehiculo> temp = ListUtils.subtract(vehiculosTotalMes, bajasVehiculos);
        List<Vehiculo> vehiculosActivosMes = ListUtils.subtract(temp, altasVehiculos);
        return vehiculosActivosMes.stream()
                .map(v -> {
                    VehiculoDto dto = daoToDtoConverter.convertDaoToDtoVehiculo(v);
                    dto.setMarca(vehiculoMarcaService.obtenerPorId(v.getMarca()));
                    if (v.getSubmarca() > 0) {
                        dto.setSubmarca(vehiculoSubmarcaService.obtenerPorId(v.getSubmarca()));
                    }
                    dto.setTipo(vehiculoTipoService.obtenerPorId(v.getTipo()));
                    return dto;
                })
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

        List<Vehiculo> altasVehiculos = vehiculoRepository.findAllByEmpresaAndColoresCapturadoTrueAndFotografiaCapturadaTrueAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasVehiculos.stream()
                .map(v -> {
                    VehiculoDto dto = daoToDtoConverter.convertDaoToDtoVehiculo(v);
                    dto.setMarca(vehiculoMarcaService.obtenerPorId(v.getMarca()));
                    if (v.getSubmarca() > 0) {
                        dto.setSubmarca(vehiculoSubmarcaService.obtenerPorId(v.getSubmarca()));
                    }
                    dto.setTipo(vehiculoTipoService.obtenerPorId(v.getTipo()));
                    return dto;
                })
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
                .map(v -> {
                    VehiculoDto dto = daoToDtoConverter.convertDaoToDtoVehiculo(v);
                    dto.setMarca(vehiculoMarcaService.obtenerPorId(v.getMarca()));
                    if (v.getSubmarca() > 0) {
                        dto.setSubmarca(vehiculoSubmarcaService.obtenerPorId(v.getSubmarca()));
                    }
                    dto.setTipo(vehiculoTipoService.obtenerPorId(v.getTipo()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ArmaDto> obtenerActivosArmasPorReporte(String uuid, String reporteUuid, String modalidad) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(modalidad)) {
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

        AtomicReference<List<Arma>> armas = new AtomicReference<List<Arma>>();

        List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndModalidad(empresa.getId(), modalidadId);
        licenciasColectivas.forEach(lc -> {
            List<Arma> armas1Altas = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                    lc.getId(),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
            );

            List<Arma> armas1Bajas = armaRepository.getAllByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                    lc.getId(),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                    YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
            );

            List<Arma> armas1Totales = armaRepository.getAllByLicenciaColectivaAndFechaCreacionLessThanAndEliminadoFalse(
                    lc.getId(), YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59)
            );

            List<Arma> temp = ListUtils.subtract(armas1Totales, armas1Bajas);
            armas.set(ListUtils.subtract(temp, armas1Altas));
        });

        return armas.get().stream().map(a -> {
            ArmaDto dto = daoToDtoConverter.convertDaoToDtoArma(a);
            dto.setClase(armaClaseService.obtenerPorId(a.getClase()));
            dto.setMarca(armaMarcaService.obtenerPorId(a.getMarca()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ArmaDto> obtenerAltasArmasPorReporte(String uuid, String reporteUuid, String modalidad) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(modalidad)) {
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

        /*List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndModalidad(empresa.getId(), modalidadId);
        licenciasColectivas.forEach(lc -> armas.addAll(armaRepository.findAllByLicenciaColectivaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndEliminadoFalse(
                lc.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        ).stream().map(a -> {
            ArmaDto dto = daoToDtoConverter.convertDaoToDtoArma(a);
            dto.setNumeroOficio(lc.getNumeroOficio());
            dto.setClase(armaClaseService.obtenerPorId(a.getClase()));
            dto.setMarca(armaMarcaService.obtenerPorId(a.getMarca()));
            return dto;
        }).collect(Collectors.toList())));

        return armas;*/
        return null;
    }

    @Override
    public List<ArmaDto> obtenerBajasArmasPorReporte(String uuid, String reporteUuid, String modalidad) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(reporteUuid) || StringUtils.isBlank(modalidad)) {
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

        List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndModalidad(empresa.getId(), modalidadId);
        licenciasColectivas.forEach(lc -> armas.addAll(armaRepository.findAllByLicenciaColectivaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                lc.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        ).stream().map(a -> {
            ArmaDto dto = daoToDtoConverter.convertDaoToDtoArma(a);
            dto.setNumeroOficio(lc.getNumeroOficio());
            dto.setClase(armaClaseService.obtenerPorId(a.getClase()));
            dto.setMarca(armaMarcaService.obtenerPorId(a.getMarca()));
            return dto;
        }).collect(Collectors.toList())));

        return armas;
    }

    @Override
    public List<CanDto> obtenerActivosCanesPorReporte(String uuid, String reporteUuid) {
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

        List<Can> altasCanes = canRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalseAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        List<Can> bajasCanes = canRepository.findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return null;
    }

    @Override
    public List<CanDto> obtenerAltasCanesPorReporte(String uuid, String reporteUuid) {
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

        List<Can> altasCanes= canRepository.findAllByEmpresaAndFechaCreacionLessThanAndFechaCreacionGreaterThanAndFotografiaCapturadaTrueAndAdiestramientoCapturadoTrueAndConstanciaCapturadaTrueOrVacunacionCapturadaTrueAndEliminadoFalseAndEliminadoFalse(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return altasCanes.stream()
                .map(c -> {
                    CanDto dto = daoToDtoConverter.convertDaoToDtoCan(c);
                    dto.setRaza(canRazaService.obtenerPorId(c.getRaza()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CanDto> obtenerBajasCanesPorReporte(String uuid, String reporteUuid) {
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

        List<Can> bajasCanes = canRepository.findAllByEmpresaAndFechaActualizacionLessThanAndFechaActualizacionGreaterThanAndEliminadoTrue(
                empresa.getId(),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atEndOfMonth().atTime(23, 59, 59),
                YearMonth.from(reporteMensual.getFechaCreacion()).minusMonths(1).atDay(1).atStartOfDay()
        );

        return bajasCanes.stream()
                .map(c -> {
                    CanDto dto = daoToDtoConverter.convertDaoToDtoCan(c);
                    dto.setRaza(canRazaService.obtenerPorId(c.getRaza()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
