package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ReporteoServiceImpl implements ReporteoService {
    private final Logger logger = LoggerFactory.getLogger(ReporteoService.class);
    private final EmpresaRepository empresaRepository;
    private final EmpresaModalidadService empresaModalidadService;
    private final EmpresaEquipoRepository empresaEquipoRepository;
    private final PersonaRepository personaRepository;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final PersonalSubpuestoRepository personalSubpuestoRepository;
    private final int PRESTADORES_SERVICIOS_TOTAL_COLUMNAS = 12;
    private final int EQUIPAMIENTO_TOTAL_COLUMNAS = 6;
    private final int PERSONAL_TOTAL_COLUMNAS = 13;
    private final AcuerdoRepository acuerdoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final VisitaRepository visitaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;
    private final ModalidadRepository modalidadRepository;
    private final SubmodalidadRepository submodalidadRepository;
    private final PersonalPuestoRepository personalPuestoRepository;
    private final PersonalNacionalidadRepository personalNacionalidadRepository;
    private final CanRepository canRepository;
    private final CanRazaRepository canRazaRepository;
    private final EmpresaDomicilioRepository empresaDomicilioRepository;
    private final ArmaRepository armaRepository;
    private final ArmaClaseRepository armaClaseRepository;
    private final ArmaMarcaRepository armaMarcaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final VehiculoTipoRepository vehiculoTipoRepository;
    private final VehiculoMarcaRepository vehiculoMarcaRepository;
    private final VehiculoSubmarcaRepository vehiculoSubmarcaRepository;
    private final VehiculoUsoRepository vehiculoUsoRepository;
    private final EmpresaLicenciaColectivaDomicilioRepository empresaLicenciaColectivaDomicilioRepository;
    private final EmpresaModalidadRepository empresaModalidadRepository;
    private final ClienteModalidadRepository clienteModalidadRepository;

    @Autowired
    public ReporteoServiceImpl(EmpresaRepository empresaRepository, PersonaRepository personaRepository,
                               EmpresaModalidadService empresaModalidadService, EmpresaEquipoRepository empresaEquipoRepository,
                               EmpresaDomicilioService empresaDomicilioService, PersonalSubpuestoRepository personalSubpuestoRepository,
                               AcuerdoRepository acuerdoRepository,
                               EmpresaEscrituraRepository empresaEscrituraRepository,
                               VisitaRepository visitaRepository,
                               UsuarioRepository usuarioRepository,
                               EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository,
                               ModalidadRepository modalidadRepository,
                               SubmodalidadRepository submodalidadRepository,
                               PersonalPuestoRepository personalPuestoRepository,
                               PersonalNacionalidadRepository personalNacionalidadRepository,
                               CanRepository canRepository,
                               CanRazaRepository canRazaRepository,
                               EmpresaDomicilioRepository empresaDomicilioRepository,
                               ArmaRepository armaRepository,
                               ArmaClaseRepository armaClaseRepository,
                               ArmaMarcaRepository armaMarcaRepository,
                               ClienteRepository clienteRepository,
                               VehiculoRepository vehiculoRepository,
                               VehiculoTipoRepository vehiculoTipoRepository,
                               VehiculoMarcaRepository vehiculoMarcaRepository,
                               VehiculoSubmarcaRepository vehiculoSubmarcaRepository,
                               VehiculoUsoRepository vehiculoUsoRepository,
                               EmpresaLicenciaColectivaDomicilioRepository empresaLicenciaColectivaDomicilioRepository,
                               EmpresaModalidadRepository empresaModalidadRepository,
                               ClienteModalidadRepository clienteModalidadRepository) {
        this.empresaRepository = empresaRepository;
        this.personaRepository = personaRepository;
        this.empresaModalidadService = empresaModalidadService;
        this.empresaEquipoRepository = empresaEquipoRepository;
        this.empresaDomicilioService = empresaDomicilioService;
        this.personalSubpuestoRepository = personalSubpuestoRepository;
        this.acuerdoRepository = acuerdoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.visitaRepository = visitaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
        this.modalidadRepository = modalidadRepository;
        this.submodalidadRepository = submodalidadRepository;
        this.personalPuestoRepository = personalPuestoRepository;
        this.personalNacionalidadRepository = personalNacionalidadRepository;
        this.canRepository = canRepository;
        this.canRazaRepository = canRazaRepository;
        this.empresaDomicilioRepository = empresaDomicilioRepository;
        this.armaRepository = armaRepository;
        this.armaClaseRepository = armaClaseRepository;
        this.armaMarcaRepository = armaMarcaRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoTipoRepository = vehiculoTipoRepository;
        this.vehiculoMarcaRepository = vehiculoMarcaRepository;
        this.vehiculoSubmarcaRepository = vehiculoSubmarcaRepository;
        this.vehiculoUsoRepository = vehiculoUsoRepository;
        this.empresaLicenciaColectivaDomicilioRepository = empresaLicenciaColectivaDomicilioRepository;
        this.empresaModalidadRepository = empresaModalidadRepository;
        this.clienteModalidadRepository = clienteModalidadRepository;
    }


    @Override
    public File generarReporteListadoNominal(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet sheet = workbook.createSheet("Listado nominal");

        // Generando encabezado del archivo
        Row encabezadosUnidosRow = sheet.createRow(0);
        Cell personalOperativoCell = encabezadosUnidosRow.createCell(1);
        personalOperativoCell.setCellValue("Personal Operativo");

        Cell empresaDeSeguridadPrivadaCell = encabezadosUnidosRow.createCell(9);
        empresaDeSeguridadPrivadaCell.setCellValue("Empresa de Seguridad Privada");

        // Sigue el encabezado del archivo
        Row encabezadoReporteRow = sheet.createRow(1);
        Cell noConsecutivoEncabezadoCell = encabezadoReporteRow.createCell(0);
        noConsecutivoEncabezadoCell.setCellStyle(style);
        Cell cuipEncabezadoCell = encabezadoReporteRow.createCell(1);
        cuipEncabezadoCell.setCellStyle(style);
        Cell noRegistroElementoEncabezadoCell = encabezadoReporteRow.createCell(2);
        noRegistroElementoEncabezadoCell.setCellStyle(style);
        Cell rfcEncabezadoCell = encabezadoReporteRow.createCell(3);
        rfcEncabezadoCell.setCellStyle(style);
        Cell curpEncabezadoCell = encabezadoReporteRow.createCell(4);
        curpEncabezadoCell.setCellStyle(style);
        Cell apellidoPaternoEncabezadoCell = encabezadoReporteRow.createCell(5);
        apellidoPaternoEncabezadoCell.setCellStyle(style);
        Cell apellidoMaternoEncabezadoCell = encabezadoReporteRow.createCell(6);
        apellidoMaternoEncabezadoCell.setCellStyle(style);
        Cell nombresEncabezadoCell = encabezadoReporteRow.createCell(7);
        nombresEncabezadoCell.setCellStyle(style);
        Cell fechaIngresoCell = encabezadoReporteRow.createCell(8);
        fechaIngresoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(9);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);
        Cell noRegistroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(10);
        noRegistroEmpresaEncabezadoCell.setCellStyle(style);
        Cell modalidadesAutorizadasEncabezadoCell = encabezadoReporteRow.createCell(11);
        modalidadesAutorizadasEncabezadoCell.setCellStyle(style);

        noConsecutivoEncabezadoCell.setCellValue("No. Consecutivo");
        cuipEncabezadoCell.setCellValue("CUIP");
        noRegistroElementoEncabezadoCell.setCellValue("No. Registro del Elemento en el Estado");
        rfcEncabezadoCell.setCellValue("RFC");
        curpEncabezadoCell.setCellValue("CURP");
        apellidoPaternoEncabezadoCell.setCellValue("Apellido Paterno");
        apellidoMaternoEncabezadoCell.setCellValue("Apellido Materno");
        nombresEncabezadoCell.setCellValue("Nombres");
        fechaIngresoCell.setCellValue("Fecha de Ingreso");

        razonSocialEmpresaEncabezadoCell.setCellValue("Razon Social");
        noRegistroEmpresaEncabezadoCell.setCellValue("No. Registro de la Empresa Estatal");
        modalidadesAutorizadasEncabezadoCell.setCellValue("Modalidades autorizadas");

        // Mergeando las celdas para generar los encabezados grandes
        sheet.addMergedRegion(CellRangeAddress.valueOf("B1:I1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J1:L1"));

        // Populando la informacion del reporte
        AtomicInteger consecutivo = new AtomicInteger(2);
        List<Personal> personal = personaRepository.getAllByEliminadoFalse();
        personal.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            List<EmpresaModalidadDto> modalidades = empresaModalidadService.obtenerModalidadesEmpresa(empresa.getUuid());

            Row eRow = sheet.createRow(consecutivo.get());
            Cell personalConsecutivoCell = eRow.createCell(0);

            Cell cuipCell = eRow.createCell(1);
            cuipCell.setCellStyle(style);
            Cell noRegistroElemento = eRow.createCell(2);
            noRegistroElemento.setCellStyle(style);
            Cell rfcPersonaCell = eRow.createCell(3);
            rfcPersonaCell.setCellStyle(style);
            Cell curpCell = eRow.createCell(4);
            curpCell.setCellStyle(style);
            Cell aPaternoCell = eRow.createCell(5);
            aPaternoCell.setCellStyle(style);
            Cell aMaternoCell = eRow.createCell(6);
            aMaternoCell.setCellStyle(style);
            Cell nombresCell = eRow.createCell(7);
            nombresCell.setCellStyle(style);
            Cell fechaIngresoPersonaCell = eRow.createCell(8);
            fechaIngresoPersonaCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(9);
            razonSocialCell.setCellStyle(style);
            Cell numeroRegistroCell = eRow.createCell(10);
            numeroRegistroCell.setCellStyle(style);
            Cell modalidadesCell = eRow.createCell(11);
            modalidadesCell.setCellStyle(style);

            personalConsecutivoCell.setCellValue(consecutivo.get());
            cuipCell.setCellValue(p.getCuip() != null ? p.getCuip() : "NA");
            noRegistroElemento.setCellValue(p.getId());
            try {
                rfcPersonaCell.setCellValue(p.getRfc().substring(0, 10));
            } catch(StringIndexOutOfBoundsException s) {
                rfcPersonaCell.setCellValue("");
            }
            curpCell.setCellValue(p.getCurp());
            aPaternoCell.setCellValue(p.getApellidoPaterno());
            aMaternoCell.setCellValue(p.getApellidoMaterno());
            nombresCell.setCellValue(p.getNombres());
            fechaIngresoPersonaCell.setCellValue(p.getFechaIngreso().toString());
            razonSocialCell.setCellValue(empresa.getRazonSocial());
            numeroRegistroCell.setCellValue(empresa.getRazonSocial());
            modalidadesCell.setCellValue(modalidades.stream().map(m -> m.getModalidad().getNombre()).collect(Collectors.joining(", ")).toUpperCase());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);

        return new File(filepath);
    }

    @Override
    public File generarReportePadronEmpresas(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet sheet = workbook.createSheet("Empresas_Aut");

        Row encabezadoReporteRow = sheet.createRow(1);
        Cell noConsecutivoEncabezadoCell = encabezadoReporteRow.createCell(0);
        noConsecutivoEncabezadoCell.setCellStyle(style);
        Cell razonSocialEncabezadoCell = encabezadoReporteRow.createCell(1);
        razonSocialEncabezadoCell.setCellStyle(style);
        Cell tipoDeRazonSocialEncabezadoCell = encabezadoReporteRow.createCell(2);
        tipoDeRazonSocialEncabezadoCell.setCellStyle(style);
        Cell tipoPersonaEncabezadoCell = encabezadoReporteRow.createCell(3);
        tipoPersonaEncabezadoCell.setCellStyle(style);
        Cell rfcEncabezadoCell = encabezadoReporteRow.createCell(4);
        rfcEncabezadoCell.setCellStyle(style);
        Cell nombreComercialEncabezadoCell = encabezadoReporteRow.createCell(5);
        nombreComercialEncabezadoCell.setCellStyle(style);
        Cell numeroRegistroEstatalEncabezadoCell = encabezadoReporteRow.createCell(6);
        numeroRegistroEstatalEncabezadoCell.setCellStyle(style);
        Cell inicioEncabezadoCell = encabezadoReporteRow.createCell(7);
        inicioEncabezadoCell.setCellStyle(style);
        Cell terminoEncabezadoCell = encabezadoReporteRow.createCell(8);
        terminoEncabezadoCell.setCellStyle(style);
        Cell modalidad1EncabezadoCell = encabezadoReporteRow.createCell(9);
        modalidad1EncabezadoCell.setCellStyle(style);
        Cell modalidad2EncabezadoCell = encabezadoReporteRow.createCell(10);
        modalidad2EncabezadoCell.setCellStyle(style);
        Cell modalidad3EncabezadoCell = encabezadoReporteRow.createCell(11);
        modalidad3EncabezadoCell.setCellStyle(style);
        Cell modalidad4EncabezadoCell = encabezadoReporteRow.createCell(12);
        modalidad4EncabezadoCell.setCellStyle(style);
        Cell modalidad5EncabezadoCell = encabezadoReporteRow.createCell(13);
        modalidad5EncabezadoCell.setCellStyle(style);
        Cell modalidad6EncabezadoCell = encabezadoReporteRow.createCell(14);
        modalidad6EncabezadoCell.setCellStyle(style);
        Cell modalidad7EncabezadoCell = encabezadoReporteRow.createCell(15);
        modalidad7EncabezadoCell.setCellStyle(style);
        Cell calleEncabezadoCell = encabezadoReporteRow.createCell(16);
        calleEncabezadoCell.setCellStyle(style);
        Cell numeroExteriorEncabezadoCell = encabezadoReporteRow.createCell(17);
        numeroExteriorEncabezadoCell.setCellStyle(style);
        Cell numeroInteriorEncabezadoCell = encabezadoReporteRow.createCell(18);
        numeroInteriorEncabezadoCell.setCellStyle(style);
        Cell coloniaEncabezadoCell = encabezadoReporteRow.createCell(19);
        coloniaEncabezadoCell.setCellStyle(style);
        Cell codigoPostalEncabezadoCell = encabezadoReporteRow.createCell(20);
        codigoPostalEncabezadoCell.setCellStyle(style);
        Cell municipioEncabezadoCell = encabezadoReporteRow.createCell(21);
        municipioEncabezadoCell.setCellStyle(style);
        Cell numeroRegistroEncabezadoCell = encabezadoReporteRow.createCell(22);
        numeroRegistroEncabezadoCell.setCellStyle(style);

        noConsecutivoEncabezadoCell.setCellValue("No. Consecutivo");
        razonSocialEncabezadoCell.setCellValue("Razon Social");
        tipoDeRazonSocialEncabezadoCell.setCellValue("Tipo de Razon Social");
        tipoPersonaEncabezadoCell.setCellValue("Tipo de Persona");
        rfcEncabezadoCell.setCellValue("RFC");
        nombreComercialEncabezadoCell.setCellValue("Nombre comercial");
        numeroRegistroEstatalEncabezadoCell.setCellValue("Numero de Registro Estatal");
        inicioEncabezadoCell.setCellValue("Inicio DD/MM/AAAA");
        terminoEncabezadoCell.setCellValue("Termino DD/MM/AAAA");
        modalidad1EncabezadoCell.setCellValue("I. Seguridad Privada a Personas");
        modalidad2EncabezadoCell.setCellValue("II. Seguridad Privada en los Bienes");
        modalidad3EncabezadoCell.setCellValue("III. Seguridad Privada en el Traslado de Bienes y Valores");
        modalidad4EncabezadoCell.setCellValue("IV. Servicios de Alarmas y de Monitoreo Electronico");
        modalidad5EncabezadoCell.setCellValue("V. Seguridad de la Informacion");
        modalidad6EncabezadoCell.setCellValue("VI. Sistemas de Prevencion y Responsabilidades");
        modalidad7EncabezadoCell.setCellValue("VII. Actividad vinculada con Servicios de Seguridad Privada");
        calleEncabezadoCell.setCellValue("Calle");
        numeroExteriorEncabezadoCell.setCellValue("Numero Exterior");
        numeroInteriorEncabezadoCell.setCellValue("Numero Interior");
        coloniaEncabezadoCell.setCellValue("Colonia");
        codigoPostalEncabezadoCell.setCellValue("Codigo Postal");
        municipioEncabezadoCell.setCellValue("Municipio");
        numeroRegistroEncabezadoCell.setCellValue("Numero de Registro Federal");

        AtomicInteger consecutivo = new AtomicInteger(2);
        List<Empresa> empresas = empresaRepository.getAllByEliminadoFalse();
        empresas.forEach(e -> {
            List<EmpresaModalidadDto> modalidades = empresaModalidadService.obtenerModalidadesEmpresa(e.getUuid());
            EmpresaDomicilio domicilio = empresaDomicilioRepository.findFirstByEmpresaAndEliminadoFalse(e.getId());
            boolean hayModalidad1 = modalidades.stream().anyMatch(m -> m.getModalidad().getId() == 6);
            boolean hayModalidad2 = modalidades.stream().anyMatch(m -> m.getModalidad().getId() == 7);
            boolean hayModalidad3 = modalidades.stream().anyMatch(m -> m.getModalidad().getId() == 8);
            boolean hayModalidad4 = modalidades.stream().anyMatch(m -> m.getModalidad().getId() == 9);
            boolean hayModalidad5 = modalidades.stream().anyMatch(m -> m.getModalidad().getId() == 10);
            boolean hayModalidad6 = modalidades.stream().anyMatch(m -> m.getModalidad().getId() == 11);
            boolean hayModalidad7 = modalidades.stream().anyMatch(m -> m.getModalidad().getId() == 12);

            Row eRow = sheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(1);
            razonSocialCell.setCellStyle(style);
            Cell tipoRazonSocialCell = eRow.createCell(2);
            tipoRazonSocialCell.setCellStyle(style);
            Cell tipoPersonaCell = eRow.createCell(3);
            tipoPersonaCell.setCellStyle(style);
            Cell rfcCell = eRow.createCell(4);
            rfcCell.setCellStyle(style);
            Cell nombreComercialCell = eRow.createCell(5);
            nombreComercialCell.setCellStyle(style);
            Cell numeroRegistroCell = eRow.createCell(6);
            numeroRegistroCell.setCellStyle(style);
            Cell inicioCell = eRow.createCell(7);
            inicioCell.setCellStyle(style);
            Cell finCell = eRow.createCell(8);
            finCell.setCellStyle(style);
            Cell modalidad1Cell = eRow.createCell(9);
            modalidad1Cell.setCellStyle(style);
            Cell modalidad2Cell = eRow.createCell(10);
            modalidad2Cell.setCellStyle(style);
            Cell modalidad3Cell = eRow.createCell(11);
            modalidad3Cell.setCellStyle(style);
            Cell modalidad4Cell = eRow.createCell(12);
            modalidad4Cell.setCellStyle(style);
            Cell modalidad5Cell = eRow.createCell(13);
            modalidad5Cell.setCellStyle(style);
            Cell modalidad6Cell = eRow.createCell(14);
            modalidad6Cell.setCellStyle(style);
            Cell modalidad7Cell = eRow.createCell(15);
            modalidad7Cell.setCellStyle(style);
            Cell calleCell = eRow.createCell(16);
            calleCell.setCellStyle(style);
            Cell numeroExteriorCell = eRow.createCell(17);
            numeroExteriorCell.setCellStyle(style);
            Cell numeroInteriorCell = eRow.createCell(18);
            numeroInteriorCell.setCellStyle(style);
            Cell coloniaCell = eRow.createCell(19);
            coloniaCell.setCellStyle(style);
            Cell codigoPostalCell = eRow.createCell(20);
            codigoPostalCell.setCellStyle(style);
            Cell municipioCell = eRow.createCell(21);
            municipioCell.setCellStyle(style);
            Cell numeroRegistroFederalCell = eRow.createCell(22);
            numeroRegistroFederalCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            razonSocialCell.setCellValue(e.getRazonSocial());
            tipoRazonSocialCell.setCellValue(e.getTipoPersona() == TipoPersonaEnum.MORAL ? calcularTipoRazonSocial(e.getRazonSocial()) : "");
            tipoPersonaCell.setCellValue(e.getTipoPersona().getNombre());
            rfcCell.setCellValue(e.getRfc());
            nombreComercialCell.setCellValue(e.getNombreComercial());
            numeroRegistroCell.setCellValue(e.getRegistro());
            inicioCell.setCellValue(e.getVigenciaInicio() != null ? e.getVigenciaInicio().toString() : "");
            finCell.setCellValue(e.getVigenciaFin() != null ? e.getVigenciaFin().toString() : "");

            modalidad1Cell.setCellValue(hayModalidad1 ? "SI" : "NO");
            modalidad2Cell.setCellValue(hayModalidad2 ? "SI" : "NO");
            modalidad3Cell.setCellValue(hayModalidad3 ? "SI" : "NO");
            modalidad4Cell.setCellValue(hayModalidad4 ? "SI" : "NO");
            modalidad5Cell.setCellValue(hayModalidad5 ? "SI" : "NO");
            modalidad6Cell.setCellValue(hayModalidad6 ? "SI" : "NO");
            modalidad7Cell.setCellValue(hayModalidad7 ? "SI" : "NO");

            calleCell.setCellValue((domicilio != null) ? domicilio.getDomicilio1() : "");
            numeroExteriorCell.setCellValue((domicilio != null) ? domicilio.getNumeroExterior() : "");
            numeroInteriorCell.setCellValue((domicilio != null) ? domicilio.getNumeroInterior() : "");
            coloniaCell.setCellValue((domicilio != null) ? domicilio.getDomicilio2() : "");
            codigoPostalCell.setCellValue((domicilio != null) ? domicilio.getCodigoPostal() : "");
            municipioCell.setCellValue((domicilio != null) ? domicilio.getDomicilio3() : "");
            numeroRegistroFederalCell.setCellValue(e.getRegistroFederal());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);

        return new File(filepath);
    }

    @Override
    public File generarReporteIntercambioInformacion(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        // Obteniendo la informacion de las empresas, ya que esto siempre se va a requerir
        List<Empresa> empresas = empresaRepository.getAllByStatus(EmpresaStatusEnum.ACTIVA);
        List<Personal> personal = personaRepository.getAllByEliminadoFalse();

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet instruccionesSheet = workbook.createSheet("INSTRUCCIONES");

        Sheet prestadoresDeServiciosSheet = workbook.createSheet("PRESTADORES DE SERVICIOS");
        Sheet equipamientoSheet = workbook.createSheet("EQUIPAMIENTO");
        Sheet personalSheet = workbook.createSheet("PERSONAL");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = prestadoresDeServiciosSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell entidadCell = encabezadoReporteRow.createCell(1);
        entidadCell.setCellStyle(style);
        Cell empresaCell = encabezadoReporteRow.createCell(2);
        empresaCell.setCellStyle(style);
        Cell modalidadesCell = encabezadoReporteRow.createCell(3);
        modalidadesCell.setCellStyle(style);
        Cell permisoEstatalCell = encabezadoReporteRow.createCell(4);
        permisoEstatalCell.setCellStyle(style);
        Cell inicioVigenciaCell = encabezadoReporteRow.createCell(5);
        inicioVigenciaCell.setCellStyle(style);
        Cell finVigenciaCell = encabezadoReporteRow.createCell(6);
        finVigenciaCell.setCellStyle(style);
        Cell permisoFederalCell = encabezadoReporteRow.createCell(7);
        permisoFederalCell.setCellStyle(style);
        Cell rfcCell = encabezadoReporteRow.createCell(8);
        rfcCell.setCellStyle(style);
        Cell domicilioCell = encabezadoReporteRow.createCell(9);
        domicilioCell.setCellStyle(style);
        Cell telefonoCell = encabezadoReporteRow.createCell(10);
        telefonoCell.setCellStyle(style);
        Cell correosCell = encabezadoReporteRow.createCell(11);
        correosCell.setCellStyle(style);

        noCell.setCellValue("NO.");
        entidadCell.setCellValue("ENTIDAD");
        empresaCell.setCellValue("EMPRESA");
        modalidadesCell.setCellValue("MODALIDADES");
        permisoEstatalCell.setCellValue("PERMISO ESTATAL");
        inicioVigenciaCell.setCellValue("INICIO VIGENCIA");
        finVigenciaCell.setCellValue("FIN DE VIGENCIA");
        permisoFederalCell.setCellValue("PERMISO FEDERAL");
        rfcCell.setCellValue("RFC");
        domicilioCell.setCellValue("DOMICILIO");
        telefonoCell.setCellValue("TELEFONOS");
        correosCell.setCellValue("CORREOS");

        AtomicInteger consecutivo = new AtomicInteger(1);

        empresas.forEach(e -> {
            List<EmpresaModalidadDto> modalidades = empresaModalidadService.obtenerModalidadesEmpresa(e.getUuid());
            List<EmpresaDomicilioDto> domicilios = empresaDomicilioService.obtenerPorEmpresaId(e.getId());
            String domicilioString;

            if(domicilios.size() > 0) {
                EmpresaDomicilioDto domicilio = domicilios.get(0);
                domicilioString = domicilio.getDomicilio1() + " NUM. " +
                        domicilio.getNumeroExterior() + " " +
                        ((domicilio.getNumeroInterior() != null) ? domicilio.getNumeroInterior() : "") + " COL. " +
                        domicilio.getDomicilio2().toUpperCase() + " C.P. " +
                        domicilio.getCodigoPostal() + ", " +
                        domicilio.getDomicilio3().toUpperCase() + ", " +
                        domicilio.getEstado().toUpperCase() + ".";
            } else {
                domicilioString = "NA";
            }


            Row eRow = prestadoresDeServiciosSheet.createRow(consecutivo.get());
            Cell empresaConsecutivoCell = eRow.createCell(0);
            Cell empresaEntidadCell = eRow.createCell(1);
            Cell empresaRsCell = eRow.createCell(2);
            Cell empresaModalidadesCell = eRow.createCell(3);
            Cell empresaPermisoEstatalCell = eRow.createCell(4);
            Cell empresaInicioVigenciaCell = eRow.createCell(5);
            Cell empresaFinVigenciaCell = eRow.createCell(6);
            Cell empresaPermisoFederalCell = eRow.createCell(7);
            Cell empresaRfcCell = eRow.createCell(8);
            Cell empresaDomicilioCell = eRow.createCell(9);
            Cell empresaTelefonoCell = eRow.createCell(10);
            Cell empresaCorreoCell = eRow.createCell(11);

            empresaConsecutivoCell.setCellValue(consecutivo.get());
            empresaConsecutivoCell.setCellStyle(style);
            empresaEntidadCell.setCellValue("JALISCO");
            empresaEntidadCell.setCellStyle(style);
            empresaRsCell.setCellValue(e.getRazonSocial().toUpperCase());
            empresaRsCell.setCellStyle(style);
            empresaModalidadesCell.setCellValue(modalidades.stream().map(m -> m.getModalidad().getNombre()).collect(Collectors.joining(", ")).toUpperCase());
            empresaModalidadesCell.setCellStyle(style);
            empresaPermisoEstatalCell.setCellValue(e.getRegistro());
            empresaPermisoEstatalCell.setCellStyle(style);
            empresaInicioVigenciaCell.setCellValue(e.getVigenciaInicio() != null ? e.getVigenciaInicio().toString() : "NA");
            empresaInicioVigenciaCell.setCellStyle(style);
            empresaFinVigenciaCell.setCellValue(e.getVigenciaFin() != null ? e.getVigenciaFin().toString() : "NA");
            empresaFinVigenciaCell.setCellStyle(style);
            empresaPermisoFederalCell.setCellValue(e.getRegistroFederal() != null ? e.getRegistroFederal() : "NA");
            empresaPermisoFederalCell.setCellStyle(style);
            empresaRfcCell.setCellValue(e.getRfc());
            empresaRfcCell.setCellStyle(style);
            empresaDomicilioCell.setCellValue(domicilioString);
            empresaDomicilioCell.setCellStyle(style);
            empresaTelefonoCell.setCellValue(e.getTelefono());
            empresaTelefonoCell.setCellStyle(style);
            empresaCorreoCell.setCellValue(e.getCorreoElectronico().toUpperCase());
            empresaCorreoCell.setCellStyle(style);

            consecutivo.incrementAndGet();
        });

        // Creando Equipamiento
        Row encabezadoPrestadoresServicioRow = equipamientoSheet.createRow(0);
        Cell noCellEquipamiento = encabezadoPrestadoresServicioRow.createCell(0);
        noCellEquipamiento.setCellStyle(style);
        Cell entidadCellEquipamiento = encabezadoPrestadoresServicioRow.createCell(1);
        entidadCellEquipamiento.setCellStyle(style);
        Cell empresaCellEquipamiento = encabezadoPrestadoresServicioRow.createCell(2);
        empresaCellEquipamiento.setCellStyle(style);
        Cell permisoEstatalCellEquipamiento = encabezadoPrestadoresServicioRow.createCell(3);
        permisoEstatalCellEquipamiento.setCellStyle(style);
        Cell permisoFederalCellEquipamiento = encabezadoPrestadoresServicioRow.createCell(4);
        permisoFederalCellEquipamiento.setCellStyle(style);
        Cell tieneEquipoRegistradoCellEquipamiento = encabezadoPrestadoresServicioRow.createCell(5);
        tieneEquipoRegistradoCellEquipamiento.setCellStyle(style);

        noCellEquipamiento.setCellValue("NO.");
        entidadCellEquipamiento.setCellValue("ENTIDAD");
        empresaCellEquipamiento.setCellValue("EMPRESA");
        permisoEstatalCellEquipamiento.setCellValue("PERMISO ESTATAL");
        permisoFederalCellEquipamiento.setCellValue("PERMISO FEDERAL");
        tieneEquipoRegistradoCellEquipamiento.setCellValue("TIENE EQUIPAMIENTO REGISTRADO");

        consecutivo.set(1);

        empresas.forEach(e -> {
            List<EmpresaEquipo> equipos = empresaEquipoRepository.findAllByEmpresaAndEliminadoFalse(e.getId());

            Row eRow = equipamientoSheet.createRow(consecutivo.get());
            Cell empresaConsecutivoCell = eRow.createCell(0);
            empresaConsecutivoCell.setCellStyle(style);
            Cell empresaEntidadCell = eRow.createCell(1);
            empresaEntidadCell.setCellStyle(style);
            Cell empresaRsCell = eRow.createCell(2);
            empresaRsCell.setCellStyle(style);
            Cell empresaPermisoEstatalCell = eRow.createCell(3);
            empresaPermisoEstatalCell.setCellStyle(style);
            Cell empresaPermisoFederalCell = eRow.createCell(4);
            empresaPermisoFederalCell.setCellStyle(style);
            Cell empresaEquipoRegistradoCell = eRow.createCell(5);
            empresaEquipoRegistradoCell.setCellStyle(style);

            empresaConsecutivoCell.setCellValue(consecutivo.get());
            empresaEntidadCell.setCellValue("JALISCO");
            empresaRsCell.setCellValue(e.getRazonSocial().toUpperCase());
            empresaPermisoEstatalCell.setCellValue(e.getRegistro());
            empresaPermisoFederalCell.setCellValue(e.getRegistroFederal());
            empresaEquipoRegistradoCell.setCellValue(equipos.size() > 0 ? "SI" : "NO");

            consecutivo.incrementAndGet();
        });

        // Creando Personal
        Row encabezadoPersonalRow = personalSheet.createRow(0);
        Cell noCellPersonal = encabezadoPersonalRow.createCell(0);
        noCellPersonal.setCellStyle(style);
        Cell entidadAdsCellPersonal = encabezadoPersonalRow.createCell(1);
        entidadAdsCellPersonal.setCellStyle(style);
        Cell cuipCellPersonal = encabezadoPersonalRow.createCell(2);
        cuipCellPersonal.setCellStyle(style);
        Cell aPaternoCellPersonal = encabezadoPersonalRow.createCell(3);
        aPaternoCellPersonal.setCellStyle(style);
        Cell aMaternoCellPersonal = encabezadoPersonalRow.createCell(4);
        aMaternoCellPersonal.setCellStyle(style);
        Cell nombresCellPersonal = encabezadoPersonalRow.createCell(5);
        nombresCellPersonal.setCellStyle(style);
        Cell rfcCellPersonal = encabezadoPersonalRow.createCell(6);
        rfcCellPersonal.setCellStyle(style);
        Cell curpCellPersonal = encabezadoPersonalRow.createCell(7);
        curpCellPersonal.setCellStyle(style);
        Cell puestoCellPersonal = encabezadoPersonalRow.createCell(8);
        puestoCellPersonal.setCellStyle(style);
        Cell corporacionCellPersonal = encabezadoPersonalRow.createCell(9);
        corporacionCellPersonal.setCellStyle(style);
        Cell fechaIngresoCellPersonal = encabezadoPersonalRow.createCell(10);
        fechaIngresoCellPersonal.setCellStyle(style);
        Cell permisoEstatalCellPersonal = encabezadoPersonalRow.createCell(11);
        permisoEstatalCellPersonal.setCellStyle(style);
        Cell permisoFederalCellPersonal = encabezadoPersonalRow.createCell(12);
        permisoFederalCellPersonal.setCellStyle(style);

        noCellPersonal.setCellValue("NO.");
        entidadAdsCellPersonal.setCellValue("ENTIDAD_ADS");
        cuipCellPersonal.setCellValue("CUIP");
        aPaternoCellPersonal.setCellValue("A_PATERNO");
        aMaternoCellPersonal.setCellValue("A_MATERNO");
        nombresCellPersonal.setCellValue("NOMBRE (S)");
        rfcCellPersonal.setCellValue("RFC");
        curpCellPersonal.setCellValue("CURP");
        puestoCellPersonal.setCellValue("PUESTO");
        corporacionCellPersonal.setCellValue("CORPORACION");
        fechaIngresoCellPersonal.setCellValue("FECHA DE INGRESO");
        permisoEstatalCellPersonal.setCellValue("PERMISO ESTATAL");
        permisoFederalCellPersonal.setCellValue("PERMISO FEDERAL");

        consecutivo.set(1);

        personal.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            PersonalSubpuesto puesto = null;
            if(p.getSubpuesto() > 0) {
                puesto = personalSubpuestoRepository.getOne(p.getSubpuesto());
            }

            Row eRow = personalSheet.createRow(consecutivo.get());
            Cell personalConsecutivoCell = eRow.createCell(0);
            personalConsecutivoCell.setCellStyle(style);
            Cell entidadAdscripcionCell = eRow.createCell(1);
            entidadAdscripcionCell.setCellStyle(style);
            Cell cuipCell = eRow.createCell(2);
            cuipCell.setCellStyle(style);
            Cell aPaternoCell = eRow.createCell(3);
            aPaternoCell.setCellStyle(style);
            Cell aMaternoCell = eRow.createCell(4);
            aMaternoCell.setCellStyle(style);
            Cell nombresCell = eRow.createCell(5);
            nombresCell.setCellStyle(style);
            Cell rfcPersonaCell = eRow.createCell(6);
            rfcPersonaCell.setCellStyle(style);
            Cell curpCell = eRow.createCell(7);
            curpCell.setCellStyle(style);
            Cell puestoCell = eRow.createCell(8);
            puestoCell.setCellStyle(style);
            Cell corporacionCell = eRow.createCell(9);
            corporacionCell.setCellStyle(style);
            Cell fechaDeIngresoCell = eRow.createCell(10);
            fechaDeIngresoCell.setCellStyle(style);
            Cell permisoEstatalPCell = eRow.createCell(11);
            permisoEstatalPCell.setCellStyle(style);
            Cell permisoFederalPCell = eRow.createCell(12);
            permisoFederalPCell.setCellStyle(style);

            personalConsecutivoCell.setCellValue(consecutivo.get());
            entidadAdscripcionCell.setCellValue("JALISCO");
            cuipCell.setCellValue(p.getCuip() != null ? p.getCuip() : "NA");
            aPaternoCell.setCellValue(p.getApellidoPaterno());
            aMaternoCell.setCellValue(p.getApellidoMaterno());
            nombresCell.setCellValue(p.getNombres());
            try {
                rfcPersonaCell.setCellValue(p.getRfc().substring(0, 10));
            } catch(StringIndexOutOfBoundsException s) {
                rfcPersonaCell.setCellValue("");
            }

            curpCell.setCellValue(p.getCurp());
            puestoCell.setCellValue((puesto != null) ? puesto.getNombre().toUpperCase() : "");
            corporacionCell.setCellValue(empresa.getRazonSocial().toUpperCase());
            fechaDeIngresoCell.setCellValue(p.getFechaIngreso().toString());
            permisoEstatalPCell.setCellValue(empresa.getRegistro());
            permisoFederalPCell.setCellValue(empresa.getRegistroFederal());

            consecutivo.incrementAndGet();
        });

        /*for(int i = 0; i < PERSONAL_TOTAL_COLUMNAS; i++) {
            personalSheet.autoSizeColumn(i);
            if(i < PRESTADORES_SERVICIOS_TOTAL_COLUMNAS) prestadoresDeServiciosSheet.autoSizeColumn(i);
            if(i < EQUIPAMIENTO_TOTAL_COLUMNAS) equipamientoSheet.autoSizeColumn(i);
        }*/

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteAcuerdos(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<Acuerdo> acuerdos = acuerdoRepository.getAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet acuerdosSheet = workbook.createSheet("ACUERDOS");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = acuerdosSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell fechaEncabezadoCell = encabezadoReporteRow.createCell(1);
        fechaEncabezadoCell.setCellStyle(style);
        Cell tipoEncabezadoCell = encabezadoReporteRow.createCell(2);
        tipoEncabezadoCell.setCellStyle(style);
        Cell fechaInicioEncabezadoCell = encabezadoReporteRow.createCell(3);
        fechaInicioEncabezadoCell.setCellStyle(style);
        Cell fechaFinEncabezadoCell = encabezadoReporteRow.createCell(4);
        fechaFinEncabezadoCell.setCellStyle(style);
        Cell multaUmasEncabezadoCell = encabezadoReporteRow.createCell(5);
        multaUmasEncabezadoCell.setCellStyle(style);
        Cell multaPesosEncabezadoCell = encabezadoReporteRow.createCell(6);
        multaPesosEncabezadoCell.setCellStyle(style);
        Cell registroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(7);
        registroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(8);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(9);
        fechaCreacionEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        fechaEncabezadoCell.setCellValue("FECHA");
        tipoEncabezadoCell.setCellValue("TIPO DE ACUERDO");
        fechaInicioEncabezadoCell.setCellValue("FECHA DE INICIO");
        fechaFinEncabezadoCell.setCellValue("FECHA DE FIN");
        multaUmasEncabezadoCell.setCellValue("MULTA EN UMAS");
        multaPesosEncabezadoCell.setCellValue("MULTA EN PESOS");
        registroEmpresaEncabezadoCell.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        acuerdos.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());

            Row eRow = acuerdosSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell fechaCell = eRow.createCell(1);
            fechaCell.setCellStyle(style);
            Cell tipoCell = eRow.createCell(2);
            tipoCell.setCellStyle(style);
            Cell fechaInicioCell = eRow.createCell(3);
            fechaInicioCell.setCellStyle(style);
            Cell fechaFinCell = eRow.createCell(4);
            fechaFinCell.setCellStyle(style);
            Cell multaUmasCell = eRow.createCell(5);
            multaUmasCell.setCellStyle(style);
            Cell multaPesosCell = eRow.createCell(6);
            multaPesosCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(7);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(8);
            razonSocialCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(9);
            fechaCreacionCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            fechaCell.setCellValue(p.getFecha().toString());
            tipoCell.setCellValue(p.getTipo().toString());
            fechaInicioCell.setCellValue((p.getFechaInicio() != null) ? p.getFechaInicio().toString() : "");
            fechaFinCell.setCellValue((p.getFechaFin() != null) ? p.getFechaFin().toString() : "");
            multaUmasCell.setCellValue(p.getMultaUmas() != null ? p.getMultaUmas().toString() : "");
            multaPesosCell.setCellValue(p.getMultaPesos() != null ? p.getMultaPesos().toString() : "");
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialCell.setCellValue(empresa.getRazonSocial());
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReportePersonal(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<Personal> personal = personaRepository.getAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet personalSheet = workbook.createSheet("PERSONAL");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = personalSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell nacionalidadEncabezadoCell = encabezadoReporteRow.createCell(1);
        nacionalidadEncabezadoCell.setCellStyle(style);
        Cell curpEncabezadoCell = encabezadoReporteRow.createCell(2);
        curpEncabezadoCell.setCellStyle(style);
        Cell apellidoPaternoEncabezadoCell = encabezadoReporteRow.createCell(3);
        apellidoPaternoEncabezadoCell.setCellStyle(style);
        Cell apellidoMaternoEncabezadoCell = encabezadoReporteRow.createCell(4);
        apellidoMaternoEncabezadoCell.setCellStyle(style);
        Cell nombresEncabezadoCell = encabezadoReporteRow.createCell(5);
        nombresEncabezadoCell.setCellStyle(style);
        Cell fechaNacimientoEncabezadoCell = encabezadoReporteRow.createCell(6);
        fechaNacimientoEncabezadoCell.setCellStyle(style);
        Cell sexoEncabezadoCell = encabezadoReporteRow.createCell(7);
        sexoEncabezadoCell.setCellStyle(style);
        Cell tipoSangreEmpresaEncabezadoCell = encabezadoReporteRow.createCell(8);
        tipoSangreEmpresaEncabezadoCell.setCellStyle(style);
        Cell fechaIngresoEncabezadoCell = encabezadoReporteRow.createCell(9);
        fechaIngresoEncabezadoCell.setCellStyle(style);
        Cell estadoCivilEncabezadoCell = encabezadoReporteRow.createCell(10);
        estadoCivilEncabezadoCell.setCellStyle(style);
        Cell calleEncabezadoCell = encabezadoReporteRow.createCell(11);
        calleEncabezadoCell.setCellStyle(style);
        Cell numeroExteriorEncabezadoCell = encabezadoReporteRow.createCell(12);
        numeroExteriorEncabezadoCell.setCellStyle(style);
        Cell numeroInteriorEncabezadoCell = encabezadoReporteRow.createCell(13);
        numeroInteriorEncabezadoCell.setCellStyle(style);
        Cell coloniaEncabezadoCell = encabezadoReporteRow.createCell(14);
        coloniaEncabezadoCell.setCellStyle(style);
        Cell municipioEncabezadoCell = encabezadoReporteRow.createCell(15);
        municipioEncabezadoCell.setCellStyle(style);
        Cell estadoEncabezadoCell = encabezadoReporteRow.createCell(16);
        estadoEncabezadoCell.setCellStyle(style);
        Cell codigoPostalEncabezadoCell = encabezadoReporteRow.createCell(17);
        codigoPostalEncabezadoCell.setCellStyle(style);
        Cell telefonoEncabezadoCell = encabezadoReporteRow.createCell(18);
        telefonoEncabezadoCell.setCellStyle(style);
        Cell correoElectronicoEncabezadoCell = encabezadoReporteRow.createCell(19);
        correoElectronicoEncabezadoCell.setCellStyle(style);
        Cell puestoEncabezadoCell = encabezadoReporteRow.createCell(20);
        puestoEncabezadoCell.setCellStyle(style);
        Cell subpuestoEncabezadoCell = encabezadoReporteRow.createCell(21);
        subpuestoEncabezadoCell.setCellStyle(style);
        Cell detallesEncabezadoCell = encabezadoReporteRow.createCell(22);
        detallesEncabezadoCell.setCellStyle(style);
        Cell estatusCuipEncabezadoCell = encabezadoReporteRow.createCell(23);
        estatusCuipEncabezadoCell.setCellStyle(style);
        Cell numeroVolanteCuipEncabezadoCell = encabezadoReporteRow.createCell(24);
        numeroVolanteCuipEncabezadoCell.setCellStyle(style);
        Cell fechaVolanteCuipEncabezadoCell = encabezadoReporteRow.createCell(25);
        fechaVolanteCuipEncabezadoCell.setCellStyle(style);
        Cell modalidadEncabezadoCell = encabezadoReporteRow.createCell(26);
        modalidadEncabezadoCell.setCellStyle(style);
        Cell rfcEncabezadoCell = encabezadoReporteRow.createCell(27);
        rfcEncabezadoCell.setCellStyle(style);
        Cell formaEjecucionEncabezadoCell = encabezadoReporteRow.createCell(28);
        formaEjecucionEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(29);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell numeroRegistroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(30);
        numeroRegistroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(31);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);
        Cell cuipEncabezadoCell = encabezadoReporteRow.createCell(32);
        cuipEncabezadoCell.setCellStyle(style);


        noCell.setCellValue("NO. CONSECUTIVO");
        nacionalidadEncabezadoCell.setCellValue("NACIONALIDAD");
        curpEncabezadoCell.setCellValue("CURP");
        apellidoPaternoEncabezadoCell.setCellValue("APELLIDO PATERNO");
        apellidoMaternoEncabezadoCell.setCellValue("APELLIDO MATERNO");
        nombresEncabezadoCell.setCellValue("NOMBRES");
        fechaNacimientoEncabezadoCell.setCellValue("FECHA NACIMIENTO");
        sexoEncabezadoCell.setCellValue("SEXO");
        tipoSangreEmpresaEncabezadoCell.setCellValue("TIPO SANGRE");
        fechaIngresoEncabezadoCell.setCellValue("FECHA INGRESO");
        estadoCivilEncabezadoCell.setCellValue("ESTADO CIVIL");
        calleEncabezadoCell.setCellValue("CALLE");
        numeroExteriorEncabezadoCell.setCellValue("NUMERO EXTERIOR");
        numeroInteriorEncabezadoCell.setCellValue("NUMERO INTERIOR");
        coloniaEncabezadoCell.setCellValue("COLONIA");
        municipioEncabezadoCell.setCellValue("MUNICIPIO");
        estadoEncabezadoCell.setCellValue("ESTADO");
        codigoPostalEncabezadoCell.setCellValue("CODIGO POSTAL");
        telefonoEncabezadoCell.setCellValue("TELEFONO");
        correoElectronicoEncabezadoCell.setCellValue("CORREO ELECTRONICO");
        puestoEncabezadoCell.setCellValue("PUESTO");
        subpuestoEncabezadoCell.setCellValue("SUBPUESTO");
        detallesEncabezadoCell.setCellValue("DETALLES");
        estatusCuipEncabezadoCell.setCellValue("ESTATUS CUIP");
        numeroVolanteCuipEncabezadoCell.setCellValue("NUMERO VOLANTE");
        fechaVolanteCuipEncabezadoCell.setCellValue("FECHA VOLANTE");
        modalidadEncabezadoCell.setCellValue("MODALIDAD");
        rfcEncabezadoCell.setCellValue("RFC");
        formaEjecucionEncabezadoCell.setCellValue("FORMA EJECUCION");
        fechaCreacionEncabezadoCell.setCellValue("FECHA CREACION");
        numeroRegistroEmpresaEncabezadoCell.setCellValue("NUMERO REGISTRO");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");
        cuipEncabezadoCell.setCellValue("CUIP");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        personal.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            PersonalPuesto personalPuesto = null;
            PersonalSubpuesto personalSubpuesto = null;
            if(p.getPuesto() > 0) {
                personalPuesto = personalPuestoRepository.getOne(p.getPuesto());
            }

            if(p.getSubpuesto() > 0) {
                personalSubpuesto = personalSubpuestoRepository.getOne(p.getSubpuesto());
            }
            PersonalNacionalidad nacionalidad = personalNacionalidadRepository.getOne(p.getNacionalidad());
            Modalidad modalidad = null;
            if(p.getModalidad() != null) {
                EmpresaModalidad empresaModalidad = empresaModalidadRepository.getOne(p.getModalidad());
                modalidad = modalidadRepository.getOne(empresaModalidad.getModalidad());
            }

            Row eRow = personalSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell nacionalidadCell = eRow.createCell(1);
            nacionalidadCell.setCellStyle(style);
            Cell curpCell = eRow.createCell(2);
            curpCell.setCellStyle(style);
            Cell apellidoPaternoCell = eRow.createCell(3);
            apellidoPaternoCell.setCellStyle(style);
            Cell apellidoMaternoCell = eRow.createCell(4);
            apellidoMaternoCell.setCellStyle(style);
            Cell nombresCell = eRow.createCell(5);
            nombresCell.setCellStyle(style);
            Cell fechaNacimientoCell = eRow.createCell(6);
            fechaNacimientoCell.setCellStyle(style);
            Cell sexoCell = eRow.createCell(7);
            sexoCell.setCellStyle(style);
            Cell tipoSangreCell = eRow.createCell(8);
            tipoSangreCell.setCellStyle(style);
            Cell fechaIngresoCell = eRow.createCell(9);
            fechaIngresoCell.setCellStyle(style);
            Cell estadoCivilCell = eRow.createCell(10);
            estadoCivilCell.setCellStyle(style);
            Cell calleCell = eRow.createCell(11);
            calleCell.setCellStyle(style);
            Cell numeroExteriorCell = eRow.createCell(12);
            numeroExteriorCell.setCellStyle(style);
            Cell numeroInteriorCell = eRow.createCell(13);
            numeroInteriorCell.setCellStyle(style);
            Cell coloniaCell = eRow.createCell(14);
            coloniaCell.setCellStyle(style);
            Cell municipioCell = eRow.createCell(15);
            municipioCell.setCellStyle(style);
            Cell estadoCell = eRow.createCell(16);
            estadoCell.setCellStyle(style);
            Cell codigoPostalCell = eRow.createCell(17);
            codigoPostalCell.setCellStyle(style);
            Cell telefonoCell = eRow.createCell(18);
            telefonoCell.setCellStyle(style);
            Cell correoElectronicoCell = eRow.createCell(19);
            correoElectronicoCell.setCellStyle(style);
            Cell puestoCell = eRow.createCell(20);
            puestoCell.setCellStyle(style);
            Cell subpuestoCell = eRow.createCell(21);
            subpuestoCell.setCellStyle(style);
            Cell detallesCell = eRow.createCell(22);
            detallesCell.setCellStyle(style);
            Cell estatusCuipCell = eRow.createCell(23);
            estatusCuipCell.setCellStyle(style);
            Cell numeroVolanteCell = eRow.createCell(24);
            numeroVolanteCell.setCellStyle(style);
            Cell fechaVolanteCell = eRow.createCell(25);
            fechaVolanteCell.setCellStyle(style);
            Cell modalidadCell = eRow.createCell(26);
            modalidadCell.setCellStyle(style);
            Cell rfcCell = eRow.createCell(27);
            rfcCell.setCellStyle(style);
            Cell formaEjecucionCell = eRow.createCell(28);
            formaEjecucionCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(29);
            fechaCreacionCell.setCellStyle(style);
            Cell numeroRegistroCell = eRow.createCell(30);
            numeroRegistroCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(31);
            razonSocialCell.setCellStyle(style);
            Cell cuipCell = eRow.createCell(32);
            cuipCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            nacionalidadCell.setCellValue(nacionalidad.getNombre());
            curpCell.setCellValue(p.getCurp());
            apellidoPaternoCell.setCellValue(p.getApellidoPaterno());
            apellidoMaternoCell.setCellValue(p.getApellidoMaterno());
            nombresCell.setCellValue(p.getNombres());
            fechaNacimientoCell.setCellValue(p.getFechaNacimiento().toString());
            sexoCell.setCellValue(p.getSexo().toString());
            tipoSangreCell.setCellValue(p.getTipoSangre().getNombre());
            fechaIngresoCell.setCellValue(p.getFechaIngreso().toString());
            estadoCivilCell.setCellValue(p.getEstadoCivil().getNombre());
            calleCell.setCellValue(p.getDomicilio1());
            numeroExteriorCell.setCellValue(p.getNumeroExterior());
            numeroInteriorCell.setCellValue(p.getNumeroInterior());
            coloniaCell.setCellValue(p.getDomicilio2());
            municipioCell.setCellValue(p.getDomicilio3());
            estadoCell.setCellValue(p.getEstado());
            codigoPostalCell.setCellValue(p.getCodigoPostal());
            telefonoCell.setCellValue(p.getTelefono());
            correoElectronicoCell.setCellValue(p.getCorreoElectronico());
            puestoCell.setCellValue(personalPuesto != null ? personalPuesto.getNombre() : "");
            subpuestoCell.setCellValue(personalSubpuesto != null ? personalSubpuesto.getNombre() : "");
            detallesCell.setCellValue(p.getDetallesPuesto());
            estatusCuipCell.setCellValue(p.getEstatusCuip() != null ? p.getEstatusCuip().getNombre() : "");
            numeroVolanteCell.setCellValue(p.getNumeroVolanteCuip() != null ? p.getNumeroVolanteCuip() : "NA");
            fechaVolanteCell.setCellValue(p.getFechaVolanteCuip() != null ? p.getFechaVolanteCuip().toString() : "NA");
            modalidadCell.setCellValue(modalidad != null ? modalidad.getNombre() : "NA");
            rfcCell.setCellValue(p.getRfc());
            formaEjecucionCell.setCellValue((p.getFormaEjecucion() != null) ? p.getFormaEjecucion().getNombre() : "");
            cuipCell.setCellValue(p.getCuip() != null ? p.getCuip() : "NA");
            numeroRegistroCell.setCellValue(empresa.getRegistro());
            razonSocialCell.setCellValue(empresa.getRazonSocial());
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteEscrituras(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<EmpresaEscritura> escrituras = empresaEscrituraRepository.findAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet escriturasSheet = workbook.createSheet("ESCRITURAS");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = escriturasSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell numeroEscrituraEncabezadoCell = encabezadoReporteRow.createCell(1);
        numeroEscrituraEncabezadoCell.setCellStyle(style);
        Cell fechaEscrituraEncabezadoCell = encabezadoReporteRow.createCell(2);
        fechaEscrituraEncabezadoCell.setCellStyle(style);
        Cell ciudadEncabezadoCell = encabezadoReporteRow.createCell(3);
        ciudadEncabezadoCell.setCellStyle(style);
        Cell tipoFedatarioEncabezadoCell = encabezadoReporteRow.createCell(4);
        tipoFedatarioEncabezadoCell.setCellStyle(style);
        Cell numeroEncabezadoCell = encabezadoReporteRow.createCell(5);
        numeroEncabezadoCell.setCellStyle(style);
        Cell nombreFedatarioEncabezadoCell = encabezadoReporteRow.createCell(6);
        nombreFedatarioEncabezadoCell.setCellStyle(style);
        Cell descripcionEncabezadoCell = encabezadoReporteRow.createCell(7);
        descripcionEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(8);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell curpNotarioEncabezadoCell = encabezadoReporteRow.createCell(9);
        curpNotarioEncabezadoCell.setCellStyle(style);
        Cell registroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(10);
        registroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(11);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        numeroEscrituraEncabezadoCell.setCellValue("NUMERO DE ESCRITURA");
        fechaEscrituraEncabezadoCell.setCellValue("FECHA DE ESCRITURA");
        ciudadEncabezadoCell.setCellValue("CIUDAD");
        tipoFedatarioEncabezadoCell.setCellValue("TIPO FEDATARIO");
        numeroEncabezadoCell.setCellValue("NUMERO DE FEDATARIO");
        nombreFedatarioEncabezadoCell.setCellValue("NOMBRE FEDATARIO");
        descripcionEncabezadoCell.setCellValue("DESCRIPCION");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");
        curpNotarioEncabezadoCell.setCellValue("CURP FEDATARIO");
        registroEmpresaEncabezadoCell.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        escrituras.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());

            Row eRow = escriturasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell numeroEscrituraCell = eRow.createCell(1);
            numeroEscrituraCell.setCellStyle(style);
            Cell fechaEscrituraCell = eRow.createCell(2);
            fechaEscrituraCell.setCellStyle(style);
            Cell ciudadCell = eRow.createCell(3);
            ciudadCell.setCellStyle(style);
            Cell tipoFedatarioCell = eRow.createCell(4);
            tipoFedatarioCell.setCellStyle(style);
            Cell numeroCell = eRow.createCell(5);
            numeroCell.setCellStyle(style);
            Cell nombreFedatarioCell = eRow.createCell(6);
            nombreFedatarioCell.setCellStyle(style);
            Cell descripcionCell = eRow.createCell(7);
            descripcionCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(8);
            fechaCreacionCell.setCellStyle(style);
            Cell curpNotarioCell = eRow.createCell(9);
            curpNotarioCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(10);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(11);
            razonSocialCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            numeroEscrituraCell.setCellValue(p.getNumeroEscritura());
            fechaEscrituraCell.setCellValue(p.getFechaEscritura().toString());
            ciudadCell.setCellValue(p.getCiudad());
            tipoFedatarioCell.setCellValue(p.getTipoFedatario().getNombre());
            numeroCell.setCellValue(p.getNumero());
            nombreFedatarioCell.setCellValue(p.getNombreFedatario() + " " + p.getApellidoPaterno() + " " + p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "");
            descripcionCell.setCellValue(p.getDescripcion() != null ? p.getDescripcion() : "NA");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            curpNotarioCell.setCellValue(p.getCurp() != null ? p.getCurp() : "NA");
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialCell.setCellValue(empresa.getRazonSocial());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteCanes(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<Can> canes = canRepository.findAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet canesSheet = workbook.createSheet("CANES");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = canesSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell nombreEncabezadoCell = encabezadoReporteRow.createCell(1);
        nombreEncabezadoCell.setCellStyle(style);
        Cell generoEncabezadoCell = encabezadoReporteRow.createCell(2);
        generoEncabezadoCell.setCellStyle(style);
        Cell razaEncabezadoCell = encabezadoReporteRow.createCell(3);
        razaEncabezadoCell.setCellStyle(style);
        Cell domicilioAsignadoEncabezadoCell = encabezadoReporteRow.createCell(4);
        domicilioAsignadoEncabezadoCell.setCellStyle(style);
        Cell fechaIngresoEncabezadoCell = encabezadoReporteRow.createCell(5);
        fechaIngresoEncabezadoCell.setCellStyle(style);
        Cell edadEncabezadoCell = encabezadoReporteRow.createCell(6);
        edadEncabezadoCell.setCellStyle(style);
        Cell pesoEncabezadoCell = encabezadoReporteRow.createCell(7);
        pesoEncabezadoCell.setCellStyle(style);
        Cell descripcionEncabezadoCell = encabezadoReporteRow.createCell(8);
        descripcionEncabezadoCell.setCellStyle(style);
        Cell origenEncabezadoCell = encabezadoReporteRow.createCell(9);
        origenEncabezadoCell.setCellStyle(style);
        Cell statusEncabezadoCell = encabezadoReporteRow.createCell(10);
        statusEncabezadoCell.setCellStyle(style);
        Cell chipEncabezadoCell = encabezadoReporteRow.createCell(11);
        chipEncabezadoCell.setCellStyle(style);
        Cell tatuajeEncabezadoCell = encabezadoReporteRow.createCell(12);
        tatuajeEncabezadoCell.setCellStyle(style);
        Cell razonSocialContratoEncabezadoCell = encabezadoReporteRow.createCell(13);
        razonSocialContratoEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(14);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell registroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(15);
        registroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(16);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);
        Cell asignacionEncabezadoCell = encabezadoReporteRow.createCell(17);
        asignacionEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        nombreEncabezadoCell.setCellValue("NOMBRE");
        generoEncabezadoCell.setCellValue("GENERO");
        razaEncabezadoCell.setCellValue("RAZA");
        domicilioAsignadoEncabezadoCell.setCellValue("DOMICILIO ASIGNADO");
        fechaIngresoEncabezadoCell.setCellValue("FECHA INGRESO");
        edadEncabezadoCell.setCellValue("EDAD");
        pesoEncabezadoCell.setCellValue("PESO");
        descripcionEncabezadoCell.setCellValue("DESCRIPCION");
        origenEncabezadoCell.setCellValue("ORIGEN");
        statusEncabezadoCell.setCellValue("STATUS");
        chipEncabezadoCell.setCellValue("CHIP");
        tatuajeEncabezadoCell.setCellValue("TATUAJE");
        razonSocialContratoEncabezadoCell.setCellValue("RAZON SOCIAL ORIGEN");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");
        registroEmpresaEncabezadoCell.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");
        asignacionEncabezadoCell.setCellValue("ASIGNADO A");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        canes.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            CanRaza canRaza = canRazaRepository.getOne(p.getRaza());
            Personal personaAsignada = personaRepository.getByCanAndEliminadoFalse(p.getId());
            EmpresaDomicilio domicilio = null;
            if(p.getDomicilioAsignado() != null) {
                domicilio = empresaDomicilioRepository.getOne(p.getDomicilioAsignado());
            }

            Row eRow = canesSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell nombreCell = eRow.createCell(1);
            nombreCell.setCellStyle(style);
            Cell generoCell = eRow.createCell(2);
            generoCell.setCellStyle(style);
            Cell razaCell = eRow.createCell(3);
            razaCell.setCellStyle(style);
            Cell domicilioAsignadoCell = eRow.createCell(4);
            domicilioAsignadoCell.setCellStyle(style);
            Cell fechaIngresoCell = eRow.createCell(5);
            fechaIngresoCell.setCellStyle(style);
            Cell edadCell = eRow.createCell(6);
            edadCell.setCellStyle(style);
            Cell pesoCell = eRow.createCell(7);
            pesoCell.setCellStyle(style);
            Cell descripcionCell = eRow.createCell(8);
            descripcionCell.setCellStyle(style);
            Cell origenCell = eRow.createCell(9);
            origenCell.setCellStyle(style);
            Cell statusCell = eRow.createCell(10);
            statusCell.setCellStyle(style);
            Cell chipCell = eRow.createCell(11);
            chipCell.setCellStyle(style);
            Cell tatuajeCell = eRow.createCell(12);
            tatuajeCell.setCellStyle(style);
            Cell razonSocialContratoCell = eRow.createCell(13);
            razonSocialContratoCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(14);
            fechaCreacionCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(15);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialEmpresaCell = eRow.createCell(16);
            razonSocialEmpresaCell.setCellStyle(style);
            Cell asignacionCell = eRow.createCell(17);
            asignacionCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            nombreCell.setCellValue(p.getNombre());
            generoCell.setCellValue(p.getGenero().getCodigo());
            razaCell.setCellValue(canRaza.getNombre());
            domicilioAsignadoCell.setCellValue(p.getDomicilioAsignado() != null ? domicilio.getDomicilio1() + " " + domicilio.getNumeroExterior() + " " + (domicilio.getNumeroInterior() != null ? domicilio.getNumeroInterior() : "") + " " + domicilio.getDomicilio2() : "NA");
            fechaIngresoCell.setCellValue(p.getFechaIngreso().toString());
            edadCell.setCellValue(p.getEdad());
            pesoCell.setCellValue(p.getPeso().toString());
            descripcionCell.setCellValue(p.getDescripcion() != null ? p.getDescripcion() : "NA");
            origenCell.setCellValue(p.getOrigen().getNombre());
            statusCell.setCellValue(p.getStatus().getNombre());
            chipCell.setCellValue(p.getChip() ? "SI" : "NO");
            tatuajeCell.setCellValue(p.getTatuaje() ? "SI" : "NO");
            razonSocialContratoCell.setCellValue(p.getRazonSocial() != null ? p.getRazonSocial() : "NA");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialEmpresaCell.setCellValue(empresa.getRazonSocial());
            asignacionCell.setCellValue((personaAsignada != null) ? personaAsignada.getNombres() + " " + personaAsignada.getApellidoPaterno() + " " + personaAsignada.getApellidoMaterno() : "");

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteVehiculos(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<Vehiculo> vehiculos = vehiculoRepository.findAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet vehiculoSheet = workbook.createSheet("VEHICULOS");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = vehiculoSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell tipoVehiculoEncabezadoCell = encabezadoReporteRow.createCell(1);
        tipoVehiculoEncabezadoCell.setCellStyle(style);
        Cell marcaEncabezadoCell = encabezadoReporteRow.createCell(2);
        marcaEncabezadoCell.setCellStyle(style);
        Cell submarcaEncabezadoCell = encabezadoReporteRow.createCell(3);
        submarcaEncabezadoCell.setCellStyle(style);
        Cell anoEncabezadoCell = encabezadoReporteRow.createCell(4);
        anoEncabezadoCell.setCellStyle(style);
        Cell rotuladoEncabezadoCell = encabezadoReporteRow.createCell(5);
        rotuladoEncabezadoCell.setCellStyle(style);
        Cell placasEncabezadoCell = encabezadoReporteRow.createCell(6);
        placasEncabezadoCell.setCellStyle(style);
        Cell serieEncabezadoCell = encabezadoReporteRow.createCell(7);
        serieEncabezadoCell.setCellStyle(style);
        Cell origenEncabezadoCell = encabezadoReporteRow.createCell(8);
        origenEncabezadoCell.setCellStyle(style);
        Cell blindadoEncabezadoCell = encabezadoReporteRow.createCell(9);
        blindadoEncabezadoCell.setCellStyle(style);
        Cell numeroHologramaEncabezadoCell = encabezadoReporteRow.createCell(10);
        numeroHologramaEncabezadoCell.setCellStyle(style);
        Cell placaMetalicaEncabezadoCell = encabezadoReporteRow.createCell(11);
        placaMetalicaEncabezadoCell.setCellStyle(style);
        Cell usoEncabezadoCell = encabezadoReporteRow.createCell(12);
        usoEncabezadoCell.setCellStyle(style);
        Cell razonSocialEncabezadoCell = encabezadoReporteRow.createCell(13);
        razonSocialEncabezadoCell.setCellStyle(style);
        Cell fechaInicioEncabezadoCell = encabezadoReporteRow.createCell(14);
        fechaInicioEncabezadoCell.setCellStyle(style);
        Cell fechaFinEncabezadoCell = encabezadoReporteRow.createCell(15);
        fechaFinEncabezadoCell.setCellStyle(style);
        Cell statusEncabezadoCell = encabezadoReporteRow.createCell(16);
        statusEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(17);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell registroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(18);
        registroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(19);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        tipoVehiculoEncabezadoCell.setCellValue("TIPO VEHICULO");
        marcaEncabezadoCell.setCellValue("MARCA");
        submarcaEncabezadoCell.setCellValue("SUBMARCA");
        anoEncabezadoCell.setCellValue("AÑO");
        rotuladoEncabezadoCell.setCellValue("ROTULADO");
        placasEncabezadoCell.setCellValue("PLACAS");
        serieEncabezadoCell.setCellValue("SERIE");
        origenEncabezadoCell.setCellValue("ORIGEN");
        blindadoEncabezadoCell.setCellValue("BLINDADO");
        numeroHologramaEncabezadoCell.setCellValue("NUMERO HOLOGRAMA");
        placaMetalicaEncabezadoCell.setCellValue("PLACA METALICA");
        usoEncabezadoCell.setCellValue("USO");
        razonSocialEncabezadoCell.setCellValue("RAZON SOCIAL");
        fechaInicioEncabezadoCell.setCellValue("FECHA INICIO");
        fechaFinEncabezadoCell.setCellValue("FECHA FIN");
        statusEncabezadoCell.setCellValue("STATUS");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");
        registroEmpresaEncabezadoCell.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        vehiculos.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            VehiculoTipo vehiculoTipo = vehiculoTipoRepository.getOne(p.getTipo());
            VehiculoMarca vehiculoMarca = vehiculoMarcaRepository.getOne(p.getMarca());
            VehiculoUso vehiculoUso = vehiculoUsoRepository.getOne(p.getUso());
            VehiculoSubmarca vehiculoSubmarca = null;
            if(p.getSubmarca() > 0) {
                vehiculoSubmarca = vehiculoSubmarcaRepository.getOne(p.getSubmarca());
            }

            Row eRow = vehiculoSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell tipoVehiculoCell = eRow.createCell(1);
            tipoVehiculoCell.setCellStyle(style);
            Cell marcaCell = eRow.createCell(2);
            marcaCell.setCellStyle(style);
            Cell submarcaCell = eRow.createCell(3);
            submarcaCell.setCellStyle(style);
            Cell anoCell = eRow.createCell(4);
            anoCell.setCellStyle(style);
            Cell rotuladoCell = eRow.createCell(5);
            rotuladoCell.setCellStyle(style);
            Cell placasCell = eRow.createCell(6);
            placasCell.setCellStyle(style);
            Cell serieCell = eRow.createCell(7);
            serieCell.setCellStyle(style);
            Cell origenCell = eRow.createCell(8);
            origenCell.setCellStyle(style);
            Cell blindadoCell = eRow.createCell(9);
            blindadoCell.setCellStyle(style);
            Cell numeroHologramaCell = eRow.createCell(10);
            numeroHologramaCell.setCellStyle(style);
            Cell placaMetalicaCell = eRow.createCell(11);
            placaMetalicaCell.setCellStyle(style);
            Cell usoCell = eRow.createCell(12);
            usoCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(13);
            razonSocialCell.setCellStyle(style);
            Cell fechaInicioCell = eRow.createCell(14);
            fechaInicioCell.setCellStyle(style);
            Cell fechaFinCell = eRow.createCell(15);
            fechaFinCell.setCellStyle(style);
            Cell statusCell = eRow.createCell(16);
            statusCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(17);
            fechaCreacionCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(18);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialEmpresaCell = eRow.createCell(19);
            razonSocialEmpresaCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            tipoVehiculoCell.setCellValue(vehiculoTipo.getNombre());
            marcaCell.setCellValue(vehiculoMarca.getNombre());
            submarcaCell.setCellValue(vehiculoSubmarca != null ? vehiculoSubmarca.getNombre() : "NA");
            anoCell.setCellValue(p.getAnio());
            rotuladoCell.setCellValue(p.isRotulado() ? "SI" : "NO");
            placasCell.setCellValue(p.getPlacas());
            serieCell.setCellValue(p.getSerie());
            origenCell.setCellValue(p.getOrigen().getNombre());
            blindadoCell.setCellValue(p.isBlindado() ? "SI" : "NO");
            numeroHologramaCell.setCellValue(StringUtils.isNotBlank(p.getNumeroHolograma()) ? p.getNumeroHolograma() : "NA");
            placaMetalicaCell.setCellValue(StringUtils.isNotBlank(p.getPlacaMetalica()) ? p.getPlacaMetalica() : "NA");
            usoCell.setCellValue(vehiculoUso != null ? vehiculoUso.getNombre() : "NA");
            razonSocialCell.setCellValue(p.getRazonSocial() != null ? p.getRazonSocial() : "NA");
            fechaInicioCell.setCellValue(p.getFechaInicio() != null ? p.getFechaInicio().toString() : "NA");
            fechaFinCell.setCellValue(p.getFechaFin() != null ? p.getFechaFin().toString() : "NA");
            statusCell.setCellValue(p.isColoresCapturado() && p.isFotografiaCapturada() ? "COMPLETO" : "INCOMPLETO");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialEmpresaCell.setCellValue(empresa.getRazonSocial());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteClientes(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<Cliente> clientes = clienteRepository.findAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet clientesSheet = workbook.createSheet("CLIENTES");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = clientesSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell tipoPersonaEncabezadoCell = encabezadoReporteRow.createCell(1);
        tipoPersonaEncabezadoCell.setCellStyle(style);
        Cell rfcEncabezadoCell = encabezadoReporteRow.createCell(2);
        rfcEncabezadoCell.setCellStyle(style);
        Cell nombreComercialEncabezadoCell = encabezadoReporteRow.createCell(3);
        nombreComercialEncabezadoCell.setCellStyle(style);
        Cell razonSocialEncabezadoCell = encabezadoReporteRow.createCell(4);
        razonSocialEncabezadoCell.setCellStyle(style);
        Cell canesEncabezadoCell = encabezadoReporteRow.createCell(5);
        canesEncabezadoCell.setCellStyle(style);
        Cell armasEncabezadoCell = encabezadoReporteRow.createCell(6);
        armasEncabezadoCell.setCellStyle(style);
        Cell fechaInicioEncabezadoCell = encabezadoReporteRow.createCell(7);
        fechaInicioEncabezadoCell.setCellStyle(style);
        Cell fechaFinEncabezadoModal = encabezadoReporteRow.createCell(8);
        fechaFinEncabezadoModal.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(9);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell registroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(10);
        registroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(11);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);
        Cell modalidadesEncabezadoCell = encabezadoReporteRow.createCell(12);
        modalidadesEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        tipoPersonaEncabezadoCell.setCellValue("TIPO PERSONA");
        rfcEncabezadoCell.setCellValue("RFC");
        nombreComercialEncabezadoCell.setCellValue("NOMBRE COMERCIAL");
        razonSocialEncabezadoCell.setCellValue("RAZON SOCIAL");
        canesEncabezadoCell.setCellValue("CANES");
        armasEncabezadoCell.setCellValue("ARMAS");
        fechaInicioEncabezadoCell.setCellValue("FECHA INICIO");
        fechaFinEncabezadoModal.setCellValue("FECHA FIN");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");
        registroEmpresaEncabezadoCell.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");
        modalidadesEncabezadoCell.setCellValue("MODALIDADES");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        clientes.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            List<ClienteModalidad> modalidades = clienteModalidadRepository.getAllByClienteAndEliminadoFalse(p.getId());
            List<EmpresaModalidadDto> empresaModalidades = modalidades.stream()
                    .map(em -> {
                        EmpresaModalidadDto emd = empresaModalidadService.obtenerEmpresaModalidadPorId(em.getEmpresaModalidad());
                        return emd;
                    })
                    .collect(Collectors.toList());
            EmpresaLicenciaColectiva licenciaColectiva = empresaLicenciaColectivaRepository.getOne(p.getEmpresa());

            Row eRow = clientesSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell tipoPersonaCell = eRow.createCell(1);
            tipoPersonaCell.setCellStyle(style);
            Cell rfcCell = eRow.createCell(2);
            rfcCell.setCellStyle(style);
            Cell nombreComercialCell = eRow.createCell(3);
            nombreComercialCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(4);
            razonSocialCell.setCellStyle(style);
            Cell canesCell = eRow.createCell(5);
            canesCell.setCellStyle(style);
            Cell armasCell = eRow.createCell(6);
            armasCell.setCellStyle(style);
            Cell fechaInicioCell = eRow.createCell(7);
            fechaInicioCell.setCellStyle(style);
            Cell fechaFinCell = eRow.createCell(8);
            fechaFinCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(9);
            fechaCreacionCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(10);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialEmpresaCell = eRow.createCell(11);
            razonSocialEmpresaCell.setCellStyle(style);
            Cell modalidadesCell = eRow.createCell(12);
            modalidadesCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            tipoPersonaCell.setCellValue(p.getTipoPersona().getNombre());
            rfcCell.setCellValue(p.getRfc());
            nombreComercialCell.setCellValue(p.getNombreComercial());
            razonSocialCell.setCellValue(p.getRazonSocial());
            canesCell.setCellValue(p.isCanes() ? "SI" : "NO");
            armasCell.setCellValue(p.isCanes() ? "SI" : "NO");
            fechaInicioCell.setCellValue(p.getFechaInicio().toString());
            fechaFinCell.setCellValue((p.getFechaFin() != null) ? p.getFechaFin().toString() : "NA");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialEmpresaCell.setCellValue(empresa.getRazonSocial());
            modalidadesCell.setCellValue(empresaModalidades.stream().map(m -> m.getModalidad().getNombre()).collect(Collectors.joining(", ")).toUpperCase());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteArmas(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<Arma> armas = armaRepository.findAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet armasSheet = workbook.createSheet("ARMAS");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = armasSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell tipoArmaEncabezadoCell = encabezadoReporteRow.createCell(1);
        tipoArmaEncabezadoCell.setCellStyle(style);
        Cell claseEncabezadoCell = encabezadoReporteRow.createCell(2);
        claseEncabezadoCell.setCellStyle(style);
        Cell marcaEncabezadoCell = encabezadoReporteRow.createCell(3);
        marcaEncabezadoCell.setCellStyle(style);
        Cell calibreEncabezadoCell = encabezadoReporteRow.createCell(4);
        calibreEncabezadoCell.setCellStyle(style);
        Cell bunkerEncabezadoCell = encabezadoReporteRow.createCell(5);
        bunkerEncabezadoCell.setCellStyle(style);
        Cell statusEncabezadoCell = encabezadoReporteRow.createCell(6);
        statusEncabezadoCell.setCellStyle(style);
        Cell serieEncabezadoCell = encabezadoReporteRow.createCell(7);
        serieEncabezadoCell.setCellStyle(style);
        Cell matriculaEncabezadoCell = encabezadoReporteRow.createCell(8);
        matriculaEncabezadoCell.setCellStyle(style);
        Cell licenciaColectivaEncabezadoCell = encabezadoReporteRow.createCell(9);
        licenciaColectivaEncabezadoCell.setCellStyle(style);
        Cell modalidadEncabezadoCell = encabezadoReporteRow.createCell(10);
        modalidadEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(11);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell registroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(12);
        registroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(13);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);
        Cell personalAsignadoEncabezadoCell = encabezadoReporteRow.createCell(14);
        personalAsignadoEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        tipoArmaEncabezadoCell.setCellValue("TIPO ARMA");
        claseEncabezadoCell.setCellValue("CLASE");
        marcaEncabezadoCell.setCellValue("MARCA");
        calibreEncabezadoCell.setCellValue("CALIBRE");
        bunkerEncabezadoCell.setCellValue("DOMICILIO");
        statusEncabezadoCell.setCellValue("STATUS");
        serieEncabezadoCell.setCellValue("MODELO");
        matriculaEncabezadoCell.setCellValue("MATRICULA");
        licenciaColectivaEncabezadoCell.setCellValue("LICENCIA COLECTIVA");
        modalidadEncabezadoCell.setCellValue("MODALIDAD");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");
        registroEmpresaEncabezadoCell.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");
        personalAsignadoEncabezadoCell.setCellValue("PERSONAL ASIGNADO");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        armas.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            ArmaClase clase = armaClaseRepository.getOne(p.getClase());
            ArmaMarca marca = armaMarcaRepository.getOne(p.getMarca());
            EmpresaLicenciaColectiva licenciaColectiva = empresaLicenciaColectivaRepository.getOne(p.getLicenciaColectiva());
            EmpresaDomicilio empresaDomicilio = empresaDomicilioRepository.getOne(p.getBunker());
            EmpresaModalidad empresaModalidad = empresaModalidadRepository.getOne(licenciaColectiva.getModalidad());
            Modalidad modalidad = modalidadRepository.getOne(empresaModalidad.getModalidad());
            Personal personalAsignado = null;
            if(p.getStatus() == ArmaStatusEnum.ASIGNADA && p.getTipo() == ArmaTipoEnum.CORTA) {
                personalAsignado = personaRepository.getByArmaCortaAndEliminadoFalse(p.getId());
            } else if(p.getStatus() == ArmaStatusEnum.ASIGNADA && p.getTipo() == ArmaTipoEnum.LARGA) {
                personalAsignado = personaRepository.getByArmaLargaAndEliminadoFalse(p.getId());
            }

            Row eRow = armasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell tipoArmaCell = eRow.createCell(1);
            tipoArmaCell.setCellStyle(style);
            Cell claseArmaCell = eRow.createCell(2);
            claseArmaCell.setCellStyle(style);
            Cell marcaCell = eRow.createCell(3);
            marcaCell.setCellStyle(style);
            Cell calibreCell = eRow.createCell(4);
            calibreCell.setCellStyle(style);
            Cell bunkerCell = eRow.createCell(5);
            bunkerCell.setCellStyle(style);
            Cell statusCell = eRow.createCell(6);
            statusCell.setCellStyle(style);
            Cell serieCell = eRow.createCell(7);
            serieCell.setCellStyle(style);
            Cell matriculaCell = eRow.createCell(8);
            matriculaCell.setCellStyle(style);
            Cell licenciaColectivaCell = eRow.createCell(9);
            licenciaColectivaCell.setCellStyle(style);
            Cell modalidadCell = eRow.createCell(10);
            modalidadCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(11);
            fechaCreacionCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(12);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(13);
            razonSocialCell.setCellStyle(style);
            Cell personalAsignadoCell = eRow.createCell(14);
            personalAsignadoCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            tipoArmaCell.setCellValue(p.getTipo().getNombre());
            claseArmaCell.setCellValue(clase.getNombre());
            marcaCell.setCellValue(marca.getNombre());
            calibreCell.setCellValue(p.getCalibre());
            bunkerCell.setCellValue(empresaDomicilio.getDomicilio1() + " " + empresaDomicilio.getNumeroExterior() + " " + (empresaDomicilio.getNumeroInterior() != null ? empresaDomicilio.getNumeroInterior() : "") + " " + empresaDomicilio.getDomicilio2());
            statusCell.setCellValue(p.getStatus().getCodigo());
            serieCell.setCellValue(p.getSerie());
            matriculaCell.setCellValue(p.getMatricula());
            licenciaColectivaCell.setCellValue(licenciaColectiva.getNumeroOficio());
            modalidadCell.setCellValue(modalidad.getNombre());
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialCell.setCellValue(empresa.getRazonSocial());
            personalAsignadoCell.setCellValue((personalAsignado != null) ? personalAsignado.getNombres() + " " + personalAsignado.getApellidoPaterno() + " " + personalAsignado.getApellidoMaterno() : "");

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteLicenciasColectivas(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());
        List<EmpresaLicenciaColectiva> licenciasColectivasEliminadas = empresaLicenciaColectivaRepository.findAllByEliminadoTrue().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet licenciasColectivasSheet = workbook.createSheet("CREADAS (" + licenciasColectivas.size() + ")");
        Sheet licenciasColectivasEliminadasSheet = workbook.createSheet("ELIMINADAS (" + licenciasColectivasEliminadas.size() + ")");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = licenciasColectivasSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell numeroOficioEncabezadoCell = encabezadoReporteRow.createCell(1);
        numeroOficioEncabezadoCell.setCellStyle(style);
        Cell modalidadEncabezadoCell = encabezadoReporteRow.createCell(2);
        modalidadEncabezadoCell.setCellStyle(style);
        Cell submodalidadEncabezadoCell = encabezadoReporteRow.createCell(3);
        submodalidadEncabezadoCell.setCellStyle(style);
        Cell fechaInicioEncabezadoCell = encabezadoReporteRow.createCell(4);
        fechaInicioEncabezadoCell.setCellStyle(style);
        Cell fechaFinEncabezadoCell = encabezadoReporteRow.createCell(5);
        fechaFinEncabezadoCell.setCellStyle(style);
        Cell armasCortasEncabezadoCell = encabezadoReporteRow.createCell(6);
        armasCortasEncabezadoCell.setCellStyle(style);
        Cell armasLargasEncabezadoCell = encabezadoReporteRow.createCell(7);
        armasLargasEncabezadoCell.setCellStyle(style);
        Cell domiciliosEncabezadoCell = encabezadoReporteRow.createCell(8);
        domiciliosEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(9);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell registroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(10);
        registroEmpresaEncabezadoCell.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCell = encabezadoReporteRow.createCell(11);
        razonSocialEmpresaEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        numeroOficioEncabezadoCell.setCellValue("NUMERO DE OFICIO");
        modalidadEncabezadoCell.setCellValue("MODALIDAD");
        submodalidadEncabezadoCell.setCellValue("SUBMODALIDAD");
        fechaInicioEncabezadoCell.setCellValue("FECHA INICIO");
        fechaFinEncabezadoCell.setCellValue("FECHA FIN");
        armasCortasEncabezadoCell.setCellValue("ARMAS CORTAS");
        armasLargasEncabezadoCell.setCellValue("ARMAS LARGAS");
        domiciliosEncabezadoCell.setCellValue("DOMICILIOS");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");
        registroEmpresaEncabezadoCell.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCell.setCellValue("RAZON SOCIAL");

        Row encabezadoReporteEliminadoRow = licenciasColectivasEliminadasSheet.createRow(0);
        Cell noCellEliminado = encabezadoReporteEliminadoRow.createCell(0);
        noCellEliminado.setCellStyle(style);
        Cell numeroOficioEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(1);
        numeroOficioEncabezadoCellEliminado.setCellStyle(style);
        Cell modalidadEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(2);
        modalidadEncabezadoCellEliminado.setCellStyle(style);
        Cell submodalidadEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(3);
        submodalidadEncabezadoCellEliminado.setCellStyle(style);
        Cell fechaInicioEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(4);
        fechaInicioEncabezadoCellEliminado.setCellStyle(style);
        Cell fechaFinEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(5);
        fechaFinEncabezadoCellEliminado.setCellStyle(style);
        Cell fechaCreacionEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(6);
        fechaCreacionEncabezadoCellEliminado.setCellStyle(style);
        Cell registroEmpresaEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(7);
        registroEmpresaEncabezadoCellEliminado.setCellStyle(style);
        Cell razonSocialEmpresaEncabezadoCellEliminado = encabezadoReporteEliminadoRow.createCell(8);
        razonSocialEmpresaEncabezadoCellEliminado.setCellStyle(style);

        noCellEliminado.setCellValue("NO. CONSECUTIVO");
        numeroOficioEncabezadoCellEliminado.setCellValue("NUMERO DE OFICIO");
        modalidadEncabezadoCellEliminado.setCellValue("MODALIDAD");
        submodalidadEncabezadoCellEliminado.setCellValue("SUBMODALIDAD");
        fechaInicioEncabezadoCellEliminado.setCellValue("FECHA INICIO");
        fechaFinEncabezadoCellEliminado.setCellValue("FECHA FIN");
        fechaCreacionEncabezadoCellEliminado.setCellValue("FECHA DE CREACION");
        registroEmpresaEncabezadoCellEliminado.setCellValue("REGISTRO EMPRESA");
        razonSocialEmpresaEncabezadoCellEliminado.setCellValue("RAZON SOCIAL");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        licenciasColectivas.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            Modalidad modalidad = modalidadRepository.getOne(p.getModalidad());
            Submodalidad submodalidad = null;
            if(p.getSubmodalidad() > 0) {
                submodalidad = submodalidadRepository.getOne(p.getSubmodalidad());
            }


            List<Arma> armas = armaRepository.getAllByLicenciaColectivaAndEliminadoFalse(p.getId());
            long cantidadArmasCortas = armas.stream().filter(a -> a.getTipo() == ArmaTipoEnum.CORTA).count();
            long cantidadArmasLargas = armas.stream().filter(a -> a.getTipo() == ArmaTipoEnum.LARGA).count();

            List<EmpresaLicenciaColectivaDomicilio> domicilios = empresaLicenciaColectivaDomicilioRepository.findAllByLicenciaColectiva(p.getId());

            Row eRow = licenciasColectivasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell numeroOficioCell = eRow.createCell(1);
            numeroOficioCell.setCellStyle(style);
            Cell modalidadCell = eRow.createCell(2);
            modalidadCell.setCellStyle(style);
            Cell submodalidadCell = eRow.createCell(3);
            submodalidadCell.setCellStyle(style);
            Cell fechaInicioCell = eRow.createCell(4);
            fechaInicioCell.setCellStyle(style);
            Cell fechaFinCell = eRow.createCell(5);
            fechaFinCell.setCellStyle(style);
            Cell armasCortasCell = eRow.createCell(6);
            armasCortasCell.setCellStyle(style);
            Cell armasLargasCell = eRow.createCell(7);
            armasLargasCell.setCellStyle(style);
            Cell domiciliosCell = eRow.createCell(8);
            domiciliosCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(9);
            fechaCreacionCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(10);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(11);
            razonSocialCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            numeroOficioCell.setCellValue(p.getNumeroOficio());
            modalidadCell.setCellValue(modalidad.getNombre());
            submodalidadCell.setCellValue(submodalidad != null ? submodalidad.getNombre() : "NA");
            fechaInicioCell.setCellValue(p.getFechaInicio().toString());
            fechaFinCell.setCellValue(p.getFechaFin().toString());
            armasCortasCell.setCellValue(cantidadArmasCortas);
            armasLargasCell.setCellValue(cantidadArmasLargas);
            domiciliosCell.setCellValue(domicilios.size());
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialCell.setCellValue(empresa.getRazonSocial());

            consecutivo.incrementAndGet();
        });

        licenciasColectivasEliminadas.forEach(p -> {
            Empresa empresa = empresaRepository.getOne(p.getEmpresa());
            Modalidad modalidad = modalidadRepository.getOne(p.getModalidad());
            Submodalidad submodalidad = null;
            if(p.getSubmodalidad() > 0) {
                submodalidad = submodalidadRepository.getOne(p.getSubmodalidad());
            }

            Row eRow = licenciasColectivasEliminadasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell numeroOficioCell = eRow.createCell(1);
            numeroOficioCell.setCellStyle(style);
            Cell modalidadCell = eRow.createCell(2);
            modalidadCell.setCellStyle(style);
            Cell submodalidadCell = eRow.createCell(3);
            submodalidadCell.setCellStyle(style);
            Cell fechaInicioCell = eRow.createCell(4);
            fechaInicioCell.setCellStyle(style);
            Cell fechaFinCell = eRow.createCell(5);
            fechaFinCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(6);
            fechaCreacionCell.setCellStyle(style);
            Cell registroEmpresaCell = eRow.createCell(7);
            registroEmpresaCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(8);
            razonSocialCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            numeroOficioCell.setCellValue(p.getNumeroOficio());
            modalidadCell.setCellValue(modalidad.getNombre());
            submodalidadCell.setCellValue(submodalidad != null ? submodalidad.getNombre() : "NA");
            fechaInicioCell.setCellValue(p.getFechaInicio().toString());
            fechaFinCell.setCellValue(p.getFechaFin().toString());
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            registroEmpresaCell.setCellValue(empresa.getRegistro());
            razonSocialCell.setCellValue(empresa.getRazonSocial());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    @Override
    public File generarReporteVisitas(LocalDate fechaInicio, LocalDate fechafin) throws Exception {
        List<Visita> visitas = visitaRepository.getAllByEliminadoFalse().stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        String filepath = "/ceemsp/fs/files/reportes/reporte-" + RandomStringUtils.randomAlphanumeric(6) + ".xls";
        OutputStream outputStream = new FileOutputStream(filepath);

        Sheet visitasSheet = workbook.createSheet("VISITAS");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = visitasSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell tipoVisitaEncabezadoCell = encabezadoReporteRow.createCell(1);
        tipoVisitaEncabezadoCell.setCellStyle(style);
        Cell numeroRegistroEmpresaEncabezadoCell = encabezadoReporteRow.createCell(2);
        numeroRegistroEmpresaEncabezadoCell.setCellStyle(style);
        Cell numeroOrdenEncabezadoCell = encabezadoReporteRow.createCell(3);
        numeroOrdenEncabezadoCell.setCellStyle(style);
        Cell fechaVisitaEncabezadoCell = encabezadoReporteRow.createCell(4);
        fechaVisitaEncabezadoCell.setCellStyle(style);
        Cell requerimientoEncabezadoCell = encabezadoReporteRow.createCell(5);
        requerimientoEncabezadoCell.setCellStyle(style);
        Cell fechaTerminoEncabezadoCell = encabezadoReporteRow.createCell(6);
        fechaTerminoEncabezadoCell.setCellStyle(style);
        Cell responsableEncabezadoCell = encabezadoReporteRow.createCell(7);
        responsableEncabezadoCell.setCellStyle(style);
        Cell calleEncabezadoCell = encabezadoReporteRow.createCell(8);
        calleEncabezadoCell.setCellStyle(style);
        Cell numeroExteriorEncabezadoCell = encabezadoReporteRow.createCell(9);
        numeroExteriorEncabezadoCell.setCellStyle(style);
        Cell numeroInteriorEncabezadoCell = encabezadoReporteRow.createCell(10);
        numeroInteriorEncabezadoCell.setCellStyle(style);
        Cell coloniaEncabezadoCell = encabezadoReporteRow.createCell(11);
        coloniaEncabezadoCell.setCellStyle(style);
        Cell municipioEncabezadoCell = encabezadoReporteRow.createCell(12);
        municipioEncabezadoCell.setCellStyle(style);
        Cell referenciaEncabezadoCell = encabezadoReporteRow.createCell(13);
        referenciaEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(14);
        fechaCreacionEncabezadoCell.setCellStyle(style);
        Cell razonSocialEncabezadoCell = encabezadoReporteRow.createCell(15);
        razonSocialEncabezadoCell.setCellStyle(style);
        Cell nombreComercialEncabezadoCell = encabezadoReporteRow.createCell(16);
        nombreComercialEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        tipoVisitaEncabezadoCell.setCellValue("TIPO DE VISITA");
        numeroRegistroEmpresaEncabezadoCell.setCellValue("NUMERO REGISTRO");
        numeroOrdenEncabezadoCell.setCellValue("NUMERO ORDEN");
        fechaVisitaEncabezadoCell.setCellValue("FECHA VISITA");
        requerimientoEncabezadoCell.setCellValue("REQUERIMIENTO");
        fechaTerminoEncabezadoCell.setCellValue("FECHA DE TERMINO");
        responsableEncabezadoCell.setCellValue("RESPONSABLE");
        calleEncabezadoCell.setCellValue("CALLE");
        numeroExteriorEncabezadoCell.setCellValue("NO. EXTERIOR");
        numeroInteriorEncabezadoCell.setCellValue("NO. INTERIOR");
        coloniaEncabezadoCell.setCellValue("COLONIA");
        municipioEncabezadoCell.setCellValue("MUNICIPIO");
        referenciaEncabezadoCell.setCellValue("REFERENCIA");
        fechaCreacionEncabezadoCell.setCellValue("FECHA CREACION");
        razonSocialEncabezadoCell.setCellValue("RAZON SOCIAL");
        nombreComercialEncabezadoCell.setCellValue("NOMBRE COMERCIAL");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        visitas.forEach(p -> {
            Usuario responsable = usuarioRepository.getOne(p.getResponsable());

            Row eRow = visitasSheet.createRow(consecutivo.get());
            Cell numeroConsecutivoCell = eRow.createCell(0);
            numeroConsecutivoCell.setCellStyle(style);
            Cell tipoVisitaCell = eRow.createCell(1);
            tipoVisitaCell.setCellStyle(style);
            Cell numeroRegistroEmpresaCell = eRow.createCell(2);
            numeroRegistroEmpresaCell.setCellStyle(style);
            Cell numeroOrdenCell = eRow.createCell(3);
            numeroOrdenCell.setCellStyle(style);
            Cell fechaVisitaCell = eRow.createCell(4);
            fechaVisitaCell.setCellStyle(style);
            Cell requerimientoCell = eRow.createCell(5);
            requerimientoCell.setCellStyle(style);
            Cell fechaTerminoCell = eRow.createCell(6);
            fechaTerminoCell.setCellStyle(style);
            Cell responsableCell = eRow.createCell(7);
            responsableCell.setCellStyle(style);
            Cell calleCell = eRow.createCell(8);
            calleCell.setCellStyle(style);
            Cell numeroExteriorCell = eRow.createCell(9);
            numeroExteriorCell.setCellStyle(style);
            Cell numeroInteriorCell = eRow.createCell(10);
            numeroInteriorCell.setCellStyle(style);
            Cell coloniaCell = eRow.createCell(11);
            coloniaCell.setCellStyle(style);
            Cell municipioCell = eRow.createCell(12);
            municipioCell.setCellStyle(style);
            Cell referenciaCell = eRow.createCell(13);
            referenciaCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(14);
            fechaCreacionCell.setCellStyle(style);
            Cell razonSocialCell = eRow.createCell(15);
            razonSocialCell.setCellStyle(style);
            Cell nombreComercialCell = eRow.createCell(16);
            nombreComercialCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            tipoVisitaCell.setCellValue(p.getTipoVisita().getNombre());
            numeroRegistroEmpresaCell.setCellValue(p.getNumeroRegistro() != null ? p.getNumeroRegistro() : "NA");
            numeroOrdenCell.setCellValue(p.getNumeroOrden());
            fechaVisitaCell.setCellValue(p.getFechaVisita().toString());
            requerimientoCell.setCellValue(p.isRequerimiento() ? "SI" : "NO");
            fechaTerminoCell.setCellValue(p.getFechaTermino() != null ? p.getFechaTermino().toString() : "NA");
            responsableCell.setCellValue(responsable.getNombres() + " " + responsable.getApellidos() + " " + responsable.getApellidoMaterno() != null ? responsable.getApellidoMaterno() : "");
            calleCell.setCellValue(p.getDomicilio1());
            numeroExteriorCell.setCellValue(p.getNumeroExterior());
            numeroInteriorCell.setCellValue(p.getNumeroInterior());
            coloniaCell.setCellValue(p.getDomicilio2());
            municipioCell.setCellValue(p.getDomicilio3());
            referenciaCell.setCellValue(p.getDomicilio4());
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            razonSocialCell.setCellValue(p.getRazonSocial());
            nombreComercialCell.setCellValue(p.getNombreComercial());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File(filepath);
    }

    private String calcularTipoRazonSocial(String razonSocial) {
        if(StringUtils.containsIgnoreCase("SA DE CV", razonSocial) || StringUtils.containsIgnoreCase("S.A. DE C.V.", razonSocial)) {
            return "SOCIEDAD ANONIMA DE CAPITAL VARIABLE";
        } else if(StringUtils.containsIgnoreCase("S DE RL DE CV", razonSocial) || StringUtils.containsIgnoreCase("S. DE R.L. DE C.V.", razonSocial)) {
            return "SOCIEDAD DE RESPONSABILIDAD LIMITADA DE CAPITAL VARIABLE";
        } else if(StringUtils.endsWithIgnoreCase("AC", razonSocial) || StringUtils.endsWithIgnoreCase("A.C.", razonSocial)) {
            return "ASOCIACION CIVIL";
        } else if(StringUtils.endsWithIgnoreCase("SC", razonSocial) || StringUtils.endsWithIgnoreCase("S.C.", razonSocial)) {
            return "SOCIEDAD CIVIL";
        } else {
            return "NA";
        }
    }
}
