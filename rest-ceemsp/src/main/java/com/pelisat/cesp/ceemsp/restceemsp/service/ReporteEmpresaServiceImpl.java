package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ReporteEmpresaServiceImpl implements ReporteEmpresaService {

    private final AcuerdoRepository acuerdoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final VisitaRepository visitaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;
    private final ModalidadRepository modalidadRepository;
    private final SubmodalidadRepository submodalidadRepository;
    private final PersonalPuestoRepository personalPuestoRepository;
    private final PersonalSubpuestoRepository personalSubpuestoRepository;
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
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
    private final EmpresaModalidadRepository empresaModalidadRepository;
    private final Logger logger = LoggerFactory.getLogger(ReporteEmpresaService.class);
    private final EmpresaRepository empresaRepository;
    private final ClienteDomicilioRepository clienteDomicilioRepository;
    private final EmpresaLicenciaColectivaDomicilioRepository empresaLicenciaColectivaDomicilioRepository;
    private final ClienteModalidadRepository clienteModalidadRepository;
    private final EmpresaModalidadService empresaModalidadService;

    @Autowired
    public ReporteEmpresaServiceImpl(AcuerdoRepository acuerdoRepository, EmpresaEscrituraRepository empresaEscrituraRepository, VisitaRepository visitaRepository, UsuarioRepository usuarioRepository, EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository, ModalidadRepository modalidadRepository, SubmodalidadRepository submodalidadRepository, PersonalPuestoRepository personalPuestoRepository, PersonalNacionalidadRepository personalNacionalidadRepository, CanRepository canRepository, CanRazaRepository canRazaRepository, EmpresaDomicilioRepository empresaDomicilioRepository, ArmaRepository armaRepository, ArmaClaseRepository armaClaseRepository, ArmaMarcaRepository armaMarcaRepository, ClienteRepository clienteRepository, VehiculoRepository vehiculoRepository, VehiculoTipoRepository vehiculoTipoRepository, VehiculoMarcaRepository vehiculoMarcaRepository, VehiculoSubmarcaRepository vehiculoSubmarcaRepository, VehiculoUsoRepository vehiculoUsoRepository,
                               UsuarioService usuarioService, PersonaRepository personaRepository, PersonalSubpuestoRepository personalSubpuestoRepository,
                               EmpresaModalidadRepository empresaModalidadRepository,
                                     EmpresaRepository empresaRepository,
                                     ClienteDomicilioRepository clienteDomicilioRepository,
                                     EmpresaLicenciaColectivaDomicilioRepository empresaLicenciaColectivaDomicilioRepository,
                                     ClienteModalidadRepository clienteModalidadRepository, EmpresaModalidadService empresaModalidadService) {
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
        this.usuarioService = usuarioService;
        this.personaRepository = personaRepository;
        this.personalSubpuestoRepository = personalSubpuestoRepository;
        this.empresaModalidadRepository = empresaModalidadRepository;
        this.empresaRepository = empresaRepository;
        this.clienteDomicilioRepository = clienteDomicilioRepository;
        this.empresaLicenciaColectivaDomicilioRepository = empresaLicenciaColectivaDomicilioRepository;
        this.clienteModalidadRepository = clienteModalidadRepository;
        this.empresaModalidadService = empresaModalidadService;
    }

    @Override
    public File generarReporteAcuerdos(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Acuerdo> acuerdos = acuerdoRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

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

        noCell.setCellValue("NO. CONSECUTIVO");
        fechaEncabezadoCell.setCellValue("FECHA");
        tipoEncabezadoCell.setCellValue("TIPO DE ACUERDO");
        fechaInicioEncabezadoCell.setCellValue("FECHA DE INICIO");
        fechaFinEncabezadoCell.setCellValue("FECHA DE FIN");
        multaUmasEncabezadoCell.setCellValue("MULTA EN UMAS");
        multaPesosEncabezadoCell.setCellValue("MULTA EN PESOS");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        acuerdos.forEach(p -> {
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

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            fechaCell.setCellValue(p.getFecha().toString());
            tipoCell.setCellValue(p.getTipo().toString());
            fechaInicioCell.setCellValue(p.getFechaInicio().toString());
            fechaFinCell.setCellValue(p.getFechaFin().toString());
            multaUmasCell.setCellValue(p.getMultaUmas() != null ? p.getMultaUmas().toString() : "");
            multaPesosCell.setCellValue(p.getMultaPesos() != null ? p.getMultaPesos().toString() : "");

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReportePersonal(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Personal> personal = personaRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

        Sheet personalSheet = workbook.createSheet("PERSONAL");

        // Creando prestadores de servicios
        Row encabezadoReporteRow = personalSheet.createRow(0);
        Cell noCell = encabezadoReporteRow.createCell(0);
        noCell.setCellStyle(style);
        Cell nacionalidadEncabezadoCell = encabezadoReporteRow.createCell(1);
        nacionalidadEncabezadoCell.setCellStyle(style);
        Cell curpEncabezadoCell = encabezadoReporteRow.createCell(2);
        curpEncabezadoCell.setCellStyle(style);
        Cell rfcEncabezadoCell = encabezadoReporteRow.createCell(3);
        rfcEncabezadoCell.setCellStyle(style);
        Cell apellidoPaternoEncabezadoCell = encabezadoReporteRow.createCell(4);
        apellidoPaternoEncabezadoCell.setCellStyle(style);
        Cell apellidoMaternoEncabezadoCell = encabezadoReporteRow.createCell(5);
        apellidoMaternoEncabezadoCell.setCellStyle(style);
        Cell nombresEncabezadoCell = encabezadoReporteRow.createCell(6);
        nombresEncabezadoCell.setCellStyle(style);
        Cell fechaNacimientoEncabezadoCell = encabezadoReporteRow.createCell(7);
        fechaNacimientoEncabezadoCell.setCellStyle(style);
        Cell sexoEncabezadoCell = encabezadoReporteRow.createCell(8);
        sexoEncabezadoCell.setCellStyle(style);
        Cell tipoSangreEmpresaEncabezadoCell = encabezadoReporteRow.createCell(9);
        tipoSangreEmpresaEncabezadoCell.setCellStyle(style);
        Cell fechaIngresoEncabezadoCell = encabezadoReporteRow.createCell(10);
        fechaIngresoEncabezadoCell.setCellStyle(style);
        Cell estadoCivilEncabezadoCell = encabezadoReporteRow.createCell(11);
        estadoCivilEncabezadoCell.setCellStyle(style);
        Cell calleEncabezadoCell = encabezadoReporteRow.createCell(12);
        calleEncabezadoCell.setCellStyle(style);
        Cell numeroExteriorEncabezadoCell = encabezadoReporteRow.createCell(13);
        numeroExteriorEncabezadoCell.setCellStyle(style);
        Cell numeroInteriorEncabezadoCell = encabezadoReporteRow.createCell(14);
        numeroInteriorEncabezadoCell.setCellStyle(style);
        Cell coloniaEncabezadoCell = encabezadoReporteRow.createCell(15);
        coloniaEncabezadoCell.setCellStyle(style);
        Cell municipioEncabezadoCell = encabezadoReporteRow.createCell(16);
        municipioEncabezadoCell.setCellStyle(style);
        Cell estadoEncabezadoCell = encabezadoReporteRow.createCell(17);
        estadoEncabezadoCell.setCellStyle(style);
        Cell codigoPostalEncabezadoCell = encabezadoReporteRow.createCell(18);
        codigoPostalEncabezadoCell.setCellStyle(style);
        Cell telefonoEncabezadoCell = encabezadoReporteRow.createCell(19);
        telefonoEncabezadoCell.setCellStyle(style);
        Cell correoElectronicoEncabezadoCell = encabezadoReporteRow.createCell(20);
        correoElectronicoEncabezadoCell.setCellStyle(style);
        Cell puestoEncabezadoCell = encabezadoReporteRow.createCell(21);
        puestoEncabezadoCell.setCellStyle(style);
        Cell subpuestoEncabezadoCell = encabezadoReporteRow.createCell(22);
        subpuestoEncabezadoCell.setCellStyle(style);
        Cell detallesEncabezadoCell = encabezadoReporteRow.createCell(23);
        detallesEncabezadoCell.setCellStyle(style);
        Cell estatusCuipEncabezadoCell = encabezadoReporteRow.createCell(24);
        estatusCuipEncabezadoCell.setCellStyle(style);
        Cell numeroVolanteCuipEncabezadoCell = encabezadoReporteRow.createCell(25);
        numeroVolanteCuipEncabezadoCell.setCellStyle(style);
        Cell fechaVolanteCuipEncabezadoCell = encabezadoReporteRow.createCell(26);
        fechaVolanteCuipEncabezadoCell.setCellStyle(style);
        Cell modalidadEncabezadoCell = encabezadoReporteRow.createCell(27);
        modalidadEncabezadoCell.setCellStyle(style);
        Cell formaEjecucionEncabezadoCell = encabezadoReporteRow.createCell(28);
        formaEjecucionEncabezadoCell.setCellStyle(style);
        Cell cuipEncabezadoCell = encabezadoReporteRow.createCell(29);
        cuipEncabezadoCell.setCellStyle(style);
        Cell portaArmasEncabezadoCell = encabezadoReporteRow.createCell(30);
        portaArmasEncabezadoCell.setCellStyle(style);
        Cell portaCanesEncabezadoCell = encabezadoReporteRow.createCell(31);
        portaCanesEncabezadoCell.setCellStyle(style);
        Cell estatusEncabezadoCell = encabezadoReporteRow.createCell(32);
        estatusEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(33);
        fechaCreacionEncabezadoCell.setCellStyle(style);


        noCell.setCellValue("NO. CONSECUTIVO");
        nacionalidadEncabezadoCell.setCellValue("NACIONALIDAD");
        curpEncabezadoCell.setCellValue("CURP");
        rfcEncabezadoCell.setCellValue("RFC");
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
        formaEjecucionEncabezadoCell.setCellValue("FORMA EJECUCION");
        cuipEncabezadoCell.setCellValue("CUIP");
        portaArmasEncabezadoCell.setCellValue("PORTACION DE ARMAS");
        portaCanesEncabezadoCell.setCellValue("PORTACION DE CANES");
        estatusEncabezadoCell.setCellValue("ESTATUS");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        personal.forEach(p -> {
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
            Cell rfcCell = eRow.createCell(3);
            rfcCell.setCellStyle(style);
            Cell apellidoPaternoCell = eRow.createCell(4);
            apellidoPaternoCell.setCellStyle(style);
            Cell apellidoMaternoCell = eRow.createCell(5);
            apellidoMaternoCell.setCellStyle(style);
            Cell nombresCell = eRow.createCell(6);
            nombresCell.setCellStyle(style);
            Cell fechaNacimientoCell = eRow.createCell(7);
            fechaNacimientoCell.setCellStyle(style);
            Cell sexoCell = eRow.createCell(8);
            sexoCell.setCellStyle(style);
            Cell tipoSangreCell = eRow.createCell(9);
            tipoSangreCell.setCellStyle(style);
            Cell fechaIngresoCell = eRow.createCell(10);
            fechaIngresoCell.setCellStyle(style);
            Cell estadoCivilCell = eRow.createCell(11);
            estadoCivilCell.setCellStyle(style);
            Cell calleCell = eRow.createCell(12);
            calleCell.setCellStyle(style);
            Cell numeroExteriorCell = eRow.createCell(13);
            numeroExteriorCell.setCellStyle(style);
            Cell numeroInteriorCell = eRow.createCell(14);
            numeroInteriorCell.setCellStyle(style);
            Cell coloniaCell = eRow.createCell(15);
            coloniaCell.setCellStyle(style);
            Cell municipioCell = eRow.createCell(16);
            municipioCell.setCellStyle(style);
            Cell estadoCell = eRow.createCell(17);
            estadoCell.setCellStyle(style);
            Cell codigoPostalCell = eRow.createCell(18);
            codigoPostalCell.setCellStyle(style);
            Cell telefonoCell = eRow.createCell(19);
            telefonoCell.setCellStyle(style);
            Cell correoElectronicoCell = eRow.createCell(20);
            correoElectronicoCell.setCellStyle(style);
            Cell puestoCell = eRow.createCell(21);
            puestoCell.setCellStyle(style);
            Cell subpuestoCell = eRow.createCell(22);
            subpuestoCell.setCellStyle(style);
            Cell detallesCell = eRow.createCell(23);
            detallesCell.setCellStyle(style);
            Cell estatusCuipCell = eRow.createCell(24);
            estatusCuipCell.setCellStyle(style);
            Cell numeroVolanteCell = eRow.createCell(25);
            numeroVolanteCell.setCellStyle(style);
            Cell fechaVolanteCell = eRow.createCell(26);
            fechaVolanteCell.setCellStyle(style);
            Cell modalidadCell = eRow.createCell(27);
            modalidadCell.setCellStyle(style);
            Cell formaEjecucionCell = eRow.createCell(28);
            formaEjecucionCell.setCellStyle(style);
            Cell cuipCell = eRow.createCell(29);
            cuipCell.setCellStyle(style);
            Cell portaArmasCell = eRow.createCell(30);
            portaArmasCell.setCellStyle(style);
            Cell portaCanesCell = eRow.createCell(31);
            portaCanesCell.setCellStyle(style);
            Cell estatusCell = eRow.createCell(32);
            estatusCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(33);
            fechaCreacionCell.setCellStyle(style);

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
            portaArmasCell.setCellValue(p.getFormaEjecucion() == FormaEjecucionEnum.ARMAS ? "SI" : "NO");
            portaCanesCell.setCellValue(p.getFormaEjecucion() == FormaEjecucionEnum.CANES ? "SI" : "NO");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());

            if(!p.isPuestoTrabajoCapturado() || (!p.isCursosCapturados() && StringUtils.containsIgnoreCase(personalPuesto.getNombre(), "Operativo")) || !p.isFotografiaCapturada()) {
                estatusCell.setCellValue("INCOMPLETA");
            } else {
                estatusCell.setCellValue("COMPLETA");
            }

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReporteEscrituras(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaEscritura> escrituras = empresaEscrituraRepository.findAllByEmpresaAndEliminadoFalse(empresa.getId());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

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
        Cell curpNotarioEncabezadoCell = encabezadoReporteRow.createCell(8);
        curpNotarioEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        numeroEscrituraEncabezadoCell.setCellValue("NUMERO DE ESCRITURA");
        fechaEscrituraEncabezadoCell.setCellValue("FECHA DE ESCRITURA");
        ciudadEncabezadoCell.setCellValue("CIUDAD");
        tipoFedatarioEncabezadoCell.setCellValue("TIPO FEDATARIO");
        numeroEncabezadoCell.setCellValue("NUMERO DE FEDATARIO");
        nombreFedatarioEncabezadoCell.setCellValue("NOMBRE FEDATARIO");
        descripcionEncabezadoCell.setCellValue("DESCRIPCION");
        curpNotarioEncabezadoCell.setCellValue("CURP FEDATARIO");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        escrituras.forEach(p -> {
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
            Cell curpNotarioCell = eRow.createCell(8);
            curpNotarioCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            numeroEscrituraCell.setCellValue(p.getNumeroEscritura());
            fechaEscrituraCell.setCellValue(p.getFechaEscritura().toString());
            ciudadCell.setCellValue(p.getCiudad());
            tipoFedatarioCell.setCellValue(p.getTipoFedatario().getNombre());
            numeroCell.setCellValue(p.getNumero());
            nombreFedatarioCell.setCellValue(p.getNombreFedatario() + " " + p.getApellidoPaterno() + " " + p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "");
            descripcionCell.setCellValue(p.getDescripcion() != null ? p.getDescripcion() : "NA");
            curpNotarioCell.setCellValue(p.getCurp() != null ? p.getCurp() : "NA");

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReporteCanes(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        List<Can> canes = canRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

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
        Cell personalAsignadoEncabezadoCell = encabezadoReporteRow.createCell(14);
        personalAsignadoEncabezadoCell.setCellStyle(style);
        Cell completitudEncabezadoCell = encabezadoReporteRow.createCell(15);
        completitudEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(16);
        fechaCreacionEncabezadoCell.setCellStyle(style);

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
        personalAsignadoEncabezadoCell.setCellValue("PERSONAL ASIGNADO");
        completitudEncabezadoCell.setCellValue("ESTATUS");
        fechaCreacionEncabezadoCell.setCellValue("FECHA CREACION");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        canes.forEach(p -> {
            CanRaza canRaza = canRazaRepository.getOne(p.getRaza());
            EmpresaDomicilio domicilio = null;
            if(p.getDomicilioAsignado() != null) {
                domicilio = empresaDomicilioRepository.getOne(p.getDomicilioAsignado());
            }

            Personal personaAsignada = null;
            if(p.getStatus() == CanStatusEnum.ACTIVO) {
                personaAsignada = personaRepository.getByCanAndEliminadoFalse(p.getId());
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
            Cell personalAsignadoCell = eRow.createCell(14);
            personalAsignadoCell.setCellStyle(style);
            Cell completitudCell = eRow.createCell(15);
            completitudCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(16);
            fechaCreacionCell.setCellStyle(style);

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
            personalAsignadoCell.setCellValue(personaAsignada != null ? personaAsignada.getNombres() + " " + personaAsignada.getApellidoPaterno() + " " + personaAsignada.getApellidoMaterno() : "");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());

            if(p.isFotografiaCapturada() && p.isAdiestramientoCapturado() && (p.isVacunacionCapturada() || p.isConstanciaCapturada())) {
                completitudCell.setCellValue("COMPLETO");
            } else {
                completitudCell.setCellValue("INCOMPLETO");
            }

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReporteVehiculos(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        List<Vehiculo> vehiculos = vehiculoRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId()).stream().sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa())).collect(Collectors.toList());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

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
        Cell completitudEncabezadoCell = encabezadoReporteRow.createCell(17);
        completitudEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(18);
        fechaCreacionEncabezadoCell.setCellStyle(style);

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
        completitudEncabezadoCell.setCellValue("ESTATUS");
        fechaCreacionEncabezadoCell.setCellValue("FECHA DE CREACION");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        vehiculos.forEach(p -> {
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
            Cell completitudCell = eRow.createCell(17);
            completitudCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(18);
            fechaCreacionCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            tipoVehiculoCell.setCellValue(vehiculoTipo.getNombre());
            marcaCell.setCellValue(vehiculoMarca.getNombre());
            submarcaCell.setCellValue(vehiculoSubmarca != null ? vehiculoSubmarca.getNombre() : "NA");
            anoCell.setCellValue(p.getAnio());
            rotuladoCell.setCellValue(p.isRotulado() ? "SI" : "NO");
            placasCell.setCellValue(p.getPlacas());
            serieCell.setCellValue(p.getSerie());
            origenCell.setCellValue(p.getOrigen().getNombre());
            blindadoCell.setCellValue(p.isBlindado() ? "SI" : "N/A");
            numeroHologramaCell.setCellValue(StringUtils.isNotBlank(p.getNumeroHolograma()) ? p.getNumeroHolograma() : "N/A");
            placaMetalicaCell.setCellValue(StringUtils.isNotBlank(p.getPlacaMetalica()) ? p.getPlacaMetalica() : "N/A");
            usoCell.setCellValue(vehiculoUso != null ? vehiculoUso.getNombre() : "NA");
            razonSocialCell.setCellValue(p.getRazonSocial() != null ? p.getRazonSocial() : "NA");
            fechaInicioCell.setCellValue(p.getFechaInicio() != null ? p.getFechaInicio().toString() : "NA");
            fechaFinCell.setCellValue(p.getFechaFin() != null ? p.getFechaFin().toString() : "NA");
            statusCell.setCellValue(p.getStatus().toString());
            completitudCell.setCellValue(p.isColoresCapturado() && p.isFotografiaCapturada() ? "COMPLETO" : "INCOMPLETO");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());

            consecutivo.incrementAndGet();
        });



        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReporteClientes(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        List<Cliente> clientes = clienteRepository.findAllByEmpresaAndEliminadoFalse(empresa.getId());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

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
        Cell fechaFinEncabezadoCell = encabezadoReporteRow.createCell(8);
        fechaFinEncabezadoCell.setCellStyle(style);
        Cell numeroSucursalesEncabezadoCell = encabezadoReporteRow.createCell(9);
        numeroSucursalesEncabezadoCell.setCellStyle(style);
        Cell modalidadesEncabezadoCell = encabezadoReporteRow.createCell(10);
        modalidadesEncabezadoCell.setCellStyle(style);
        Cell completitudEncabezadoCell = encabezadoReporteRow.createCell(11);
        completitudEncabezadoCell.setCellStyle(style);
        Cell fechaCreacionEncabezadoCell = encabezadoReporteRow.createCell(12);
        fechaCreacionEncabezadoCell.setCellStyle(style);

        noCell.setCellValue("NO. CONSECUTIVO");
        tipoPersonaEncabezadoCell.setCellValue("TIPO PERSONA");
        rfcEncabezadoCell.setCellValue("RFC");
        nombreComercialEncabezadoCell.setCellValue("NOMBRE COMERCIAL");
        razonSocialEncabezadoCell.setCellValue("RAZON SOCIAL");
        canesEncabezadoCell.setCellValue("CANES");
        armasEncabezadoCell.setCellValue("ARMAS");
        fechaInicioEncabezadoCell.setCellValue("FECHA INICIO");
        fechaFinEncabezadoCell.setCellValue("FECHA FIN");
        numeroSucursalesEncabezadoCell.setCellValue("NUMERO SUCURSALES");
        modalidadesEncabezadoCell.setCellValue("MODALIDADES");
        completitudEncabezadoCell.setCellValue("ESTATUS");
        fechaCreacionEncabezadoCell.setCellValue("FECHA CREACION");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        clientes.forEach(p -> {
            List<ClienteDomicilio> domicilios = clienteDomicilioRepository.getAllByClienteAndEliminadoFalse(p.getId());
            List<ClienteModalidad> modalidades = clienteModalidadRepository.getAllByClienteAndEliminadoFalse(p.getId());
            List<EmpresaModalidadDto> empresaModalidades = modalidades.stream()
                    .map(em -> {
                        EmpresaModalidadDto emd = empresaModalidadService.obtenerEmpresaModalidadPorId(em.getEmpresaModalidad());
                        return emd;
                    })
                    .collect(Collectors.toList());

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
            Cell numeroSucursalesCell = eRow.createCell(9);
            numeroSucursalesCell.setCellStyle(style);
            Cell modalidadesCell = eRow.createCell(10);
            modalidadesCell.setCellStyle(style);
            Cell completitudCell = eRow.createCell(11);
            completitudCell.setCellStyle(style);
            Cell fechaCreacionCell = eRow.createCell(12);
            fechaCreacionCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            tipoPersonaCell.setCellValue(p.getTipoPersona().getNombre());
            rfcCell.setCellValue(p.getRfc());
            nombreComercialCell.setCellValue(p.getNombreComercial());
            razonSocialCell.setCellValue(p.getRazonSocial());
            canesCell.setCellValue(p.isCanes() ? "SI" : "NO");
            armasCell.setCellValue(p.isCanes() ? "SI" : "NO");
            fechaInicioCell.setCellValue(p.getFechaInicio().toString());
            fechaFinCell.setCellValue((p.getFechaFin() != null) ? p.getFechaFin().toString() : "NA");
            numeroSucursalesCell.setCellValue(domicilios.size());
            modalidadesCell.setCellValue(empresaModalidades.stream().map(m -> m.getModalidad().getNombre()).collect(Collectors.joining(", ")).toUpperCase());
            completitudCell.setCellValue((p.isDomicilioCapturado() && p.isModalidadCapturada() && p.isFormaEjecucionCapturada()) ? "COMPLETO" : "INCOMPLETO");
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReporteArmas(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        List<Arma> armas = armaRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

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
        Cell personalAsignadoEncabezadoCell = encabezadoReporteRow.createCell(12);
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
        personalAsignadoEncabezadoCell.setCellValue("PERSONAL ASIGNADO");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        armas.forEach(p -> {
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
            Cell personalAsignadoCell = eRow.createCell(12);
            personalAsignadoCell.setCellStyle(style);

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            tipoArmaCell.setCellValue(p.getTipo().getNombre());
            claseArmaCell.setCellValue(clase.getNombre());
            marcaCell.setCellValue(marca.getNombre());
            calibreCell.setCellValue(p.getCalibre());
            bunkerCell.setCellValue(empresaDomicilio.getDomicilio1() + " " + empresaDomicilio.getNumeroExterior() + " " + (empresaDomicilio.getNumeroInterior() != null ? empresaDomicilio.getNumeroInterior() : "") + " " + empresaDomicilio.getDomicilio2());
            statusCell.setCellValue(p.getStatus().getNombre());
            serieCell.setCellValue(p.getSerie());
            matriculaCell.setCellValue(p.getMatricula());
            licenciaColectivaCell.setCellValue(licenciaColectiva.getNumeroOficio());
            modalidadCell.setCellValue(modalidad.getNombre());
            fechaCreacionCell.setCellValue(p.getFechaCreacion().toString());
            personalAsignadoCell.setCellValue((personalAsignado != null) ? personalAsignado.getNombres() + " " + personalAsignado.getApellidoPaterno() + " " + personalAsignado.getApellidoMaterno() : "");

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReporteLicenciasColectivas(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaLicenciaColectiva> licenciasColectivas = empresaLicenciaColectivaRepository.findAllByEmpresaAndEliminadoFalse(empresa.getId());

        Workbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        OutputStream outputStream = new FileOutputStream("test.xls");

        Sheet licenciasColectivasSheet = workbook.createSheet("LICENCIAS COLECTIVAS");

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

        noCell.setCellValue("NO. CONSECUTIVO");
        numeroOficioEncabezadoCell.setCellValue("NUMERO DE OFICIO");
        modalidadEncabezadoCell.setCellValue("MODALIDAD");
        submodalidadEncabezadoCell.setCellValue("SUBMODALIDAD");
        fechaInicioEncabezadoCell.setCellValue("FECHA INICIO");
        fechaFinEncabezadoCell.setCellValue("FECHA FIN");
        armasCortasEncabezadoCell.setCellValue("ARMAS CORTAS");
        armasLargasEncabezadoCell.setCellValue("ARMAS LARGAS");
        domiciliosEncabezadoCell.setCellValue("DOMICILIOS");

        AtomicInteger consecutivo = new AtomicInteger(1);
        consecutivo.set(1);

        licenciasColectivas.forEach(p -> {
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

            numeroConsecutivoCell.setCellValue(consecutivo.get());
            numeroOficioCell.setCellValue(p.getNumeroOficio());
            modalidadCell.setCellValue(modalidad.getNombre());
            submodalidadCell.setCellValue(submodalidad != null ? submodalidad.getNombre() : "NA");
            fechaInicioCell.setCellValue(p.getFechaInicio().toString());
            fechaFinCell.setCellValue(p.getFechaFin().toString());
            armasCortasCell.setCellValue(cantidadArmasCortas);
            armasLargasCell.setCellValue(cantidadArmasLargas);
            domiciliosCell.setCellValue(domicilios.size());

            consecutivo.incrementAndGet();
        });

        workbook.write(outputStream);
        return new File("test.xls");
    }

    @Override
    public File generarReporteVisitas(String uuid) throws Exception {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("Alguno de los datos viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(uuid);

        if(empresa == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<Visita> visitas = visitaRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());

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
}
