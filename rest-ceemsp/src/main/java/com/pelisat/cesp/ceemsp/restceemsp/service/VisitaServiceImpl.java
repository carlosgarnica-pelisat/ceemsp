package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class VisitaServiceImpl implements VisitaService {

    private final Logger logger = LoggerFactory.getLogger(VisitaService.class);
    private final VisitaRepository visitaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final VisitaArchivoService visitaArchivoService;
    private final CalleService calleService;
    private final ColoniaService coloniaService;
    private final LocalidadService localidadService;
    private final MunicipioService municipioService;
    private final EstadoService estadoService;

    @Autowired
    public VisitaServiceImpl(VisitaRepository visitaRepository, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                             EmpresaService empresaService, UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper,
                             VisitaArchivoService visitaArchivoService, CalleService calleService, ColoniaService coloniaService,
                             LocalidadService localidadService, MunicipioService municipioService, EstadoService estadoService) {
        this.visitaRepository = visitaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.visitaArchivoService = visitaArchivoService;
        this.calleService = calleService;
        this.coloniaService = coloniaService;
        this.localidadService = localidadService;
        this.municipioService = municipioService;
        this.estadoService = estadoService;
    }

    @Override
    public List<VisitaDto> obtenerTodas() {
        logger.info("Consultando todas las visitas en la base de datos");
        List<Visita> visitas = visitaRepository.getAllByEliminadoFalse();
        return visitas.stream()
                .map(v -> {
                    VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(v);
                    if(v.getEmpresa() != null && v.getEmpresa() > 0) {
                        visitaDto.setEmpresa(empresaService.obtenerPorId(v.getEmpresa()));
                    }
                    visitaDto.setResponsable(usuarioService.getUserById(v.getResponsable()));
                    return visitaDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitaDto> obtenerProximasVisitas() {
        logger.info("Consultando las proximas visitas en la base de datos");
        List<Visita> visitas = visitaRepository.getAllByFechaVisitaGreaterThanEqualAndEliminadoFalse(LocalDate.now());
        return visitas.stream()
                .map(v -> {
                    VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(v);
                    if(v.getEmpresa() != null && v.getEmpresa() > 0) {
                        visitaDto.setEmpresa(empresaService.obtenerPorId(v.getEmpresa()));
                    }
                    visitaDto.setResponsable(usuarioService.getUserById(v.getResponsable()));
                    return visitaDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public VisitaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la visita a consultar viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(visita);

        if(visita.getEmpresa() != null && visita.getEmpresa() > 0) {
            visitaDto.setEmpresa(empresaService.obtenerPorId(visita.getEmpresa()));
        }

        visitaDto.setResponsable(usuarioService.getUserById(visita.getResponsable()));
        visitaDto.setArchivos(visitaArchivoService.obtenerArchivosPorVisita(uuid));

        if(visita.getCalleCatalogo() > 0) {
            visitaDto.setCalleCatalogo(calleService.obtenerCallePorId(visita.getCalleCatalogo()));
        }
        if(visita.getColoniaCatalogo() > 0) {
            visitaDto.setColoniaCatalogo(coloniaService.obtenerColoniaPorId(visita.getColoniaCatalogo()));
        }
        if(visita.getLocalidadCatalogo() > 0) {
            visitaDto.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(visita.getLocalidadCatalogo()));
        }
        if(visita.getMunicipioCatalogo() > 0) {
            visitaDto.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(visita.getMunicipioCatalogo()));
        }
        if(visita.getEstadoCatalogo() > 0) {
            visitaDto.setEstadoCatalogo(estadoService.obtenerPorId(visita.getEstadoCatalogo()));
        }

        return visitaDto;
    }

    @Override
    public VisitaDto obtenerPorId(Integer id) {
        return null;
    }

    @Transactional
    @Override
    public VisitaDto crearNuevo(VisitaDto visitaDto, String username) {
        if(StringUtils.isBlank(username) || visitaDto == null) {
            logger.warn("El usuario o la visita a registrar viene como nula o vacia");
            throw new InvalidDataException();
        }

        logger.info("Registrando una nueva visita");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Visita visita = dtoToDaoConverter.convertDtoToDaoVisita(visitaDto);
        if(visitaDto.getTipoVisita() == TipoVisitaEnum.ORDINARIA || (visitaDto.getTipoVisita() == TipoVisitaEnum.INICIAL && visitaDto.isExisteEmpresa()) ||
                (visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && visitaDto.isExisteEmpresa()) || (visitaDto.getTipoVisita() == TipoVisitaEnum.IMPACTO && visitaDto.isExisteEmpresa())) {
            visita.setEmpresa(visitaDto.getEmpresa().getId());
            visita.setEmpresaDomicilio(visitaDto.getEmpresaDomicilio().getId());

            visita.setCalleCatalogo(visitaDto.getEmpresaDomicilio().getCalleCatalogo().getId());
            visita.setNumeroExterior(visitaDto.getEmpresaDomicilio().getNumeroExterior());
            visita.setNumeroInterior(visitaDto.getEmpresaDomicilio().getNumeroInterior());
            visita.setColoniaCatalogo(visitaDto.getEmpresaDomicilio().getColoniaCatalogo().getId());
            visita.setLocalidadCatalogo(visitaDto.getEmpresaDomicilio().getLocalidadCatalogo().getId());
            visita.setMunicipioCatalogo(visitaDto.getEmpresaDomicilio().getMunicipioCatalogo().getId());
            visita.setEstadoCatalogo(visitaDto.getEmpresaDomicilio().getEstadoCatalogo().getId());

            visita.setDomicilio1(visitaDto.getEmpresaDomicilio().getCalleCatalogo().getNombre());
            visita.setDomicilio2(visitaDto.getEmpresaDomicilio().getColoniaCatalogo().getNombre());
            visita.setLocalidad(visitaDto.getEmpresaDomicilio().getLocalidadCatalogo().getNombre());
            visita.setDomicilio3(visitaDto.getEmpresaDomicilio().getMunicipioCatalogo().getNombre());
            visita.setEstado(visitaDto.getEmpresaDomicilio().getEstadoCatalogo().getNombre());
            visita.setCodigoPostal(visitaDto.getEmpresaDomicilio().getCodigoPostal());
        } else if((visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && !visitaDto.isExisteEmpresa()) || (visitaDto.getTipoVisita() == TipoVisitaEnum.IMPACTO && !visitaDto.isExisteEmpresa()
                || (visitaDto.getTipoVisita() == TipoVisitaEnum.INICIAL && !visitaDto.isExisteEmpresa()))) {
            visita.setCalleCatalogo(visitaDto.getCalleCatalogo().getId());
            visita.setNumeroExterior(visitaDto.getNumeroExterior());
            visita.setNumeroInterior(visitaDto.getNumeroInterior());
            visita.setColoniaCatalogo(visitaDto.getColoniaCatalogo().getId());
            visita.setLocalidadCatalogo(visitaDto.getLocalidadCatalogo().getId());
            visita.setMunicipioCatalogo(visitaDto.getMunicipioCatalogo().getId());
            visita.setEstadoCatalogo(visitaDto.getEstadoCatalogo().getId());

            visita.setDomicilio1(visitaDto.getCalleCatalogo().getNombre());
            visita.setDomicilio2(visitaDto.getColoniaCatalogo().getNombre());
            visita.setLocalidad(visitaDto.getLocalidadCatalogo().getNombre());
            visita.setDomicilio3(visitaDto.getMunicipioCatalogo().getNombre());
            visita.setEstado(visitaDto.getEstadoCatalogo().getNombre());
            visita.setCodigoPostal(visitaDto.getCodigoPostal());
        }

        visita.setResponsable(visitaDto.getResponsable().getId());
        visita.setFechaVisita(LocalDate.parse(visitaDto.getFechaVisita()));
        daoHelper.fulfillAuditorFields(true, visita, usuarioDto.getId());

        Visita visitaCreada = visitaRepository.save(visita);

        return daoToDtoConverter.convertDaoToDtoVisita(visitaCreada);
    }

    @Transactional
    @Override
    public VisitaDto modificarVisita(String uuid, String username, VisitaDto visitaDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || visitaDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la informacion del requerimiento en la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(visitaDto.getTipoVisita() == TipoVisitaEnum.ORDINARIA || (visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && visitaDto.isExisteEmpresa())) {
            visita.setEmpresa(visitaDto.getEmpresa().getId());
            visita.setEmpresaDomicilio(visitaDto.getEmpresaDomicilio().getId());

            visita.setCalleCatalogo(visitaDto.getEmpresaDomicilio().getCalleCatalogo().getId());
            visita.setNumeroExterior(visitaDto.getEmpresaDomicilio().getNumeroExterior());
            visita.setNumeroInterior(visitaDto.getEmpresaDomicilio().getNumeroInterior());
            visita.setColoniaCatalogo(visitaDto.getEmpresaDomicilio().getColoniaCatalogo().getId());
            visita.setLocalidadCatalogo(visitaDto.getEmpresaDomicilio().getLocalidadCatalogo().getId());
            visita.setMunicipioCatalogo(visitaDto.getEmpresaDomicilio().getMunicipioCatalogo().getId());
            visita.setEstadoCatalogo(visitaDto.getEmpresaDomicilio().getEstadoCatalogo().getId());

            visita.setDomicilio1(visitaDto.getEmpresaDomicilio().getCalleCatalogo().getNombre());
            visita.setDomicilio2(visitaDto.getEmpresaDomicilio().getColoniaCatalogo().getNombre());
            visita.setLocalidad(visitaDto.getEmpresaDomicilio().getLocalidadCatalogo().getNombre());
            visita.setDomicilio3(visitaDto.getEmpresaDomicilio().getMunicipioCatalogo().getNombre());
            visita.setEstado(visitaDto.getEmpresaDomicilio().getEstadoCatalogo().getNombre());
            visita.setCodigoPostal(visitaDto.getEmpresaDomicilio().getCodigoPostal());
        } else if(visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && !visitaDto.isExisteEmpresa()) {
            visita.setCalleCatalogo(visitaDto.getCalleCatalogo().getId());
            visita.setNumeroExterior(visitaDto.getNumeroExterior());
            visita.setNumeroInterior(visitaDto.getNumeroInterior());
            visita.setColoniaCatalogo(visitaDto.getColoniaCatalogo().getId());
            visita.setLocalidadCatalogo(visitaDto.getLocalidadCatalogo().getId());
            visita.setMunicipioCatalogo(visitaDto.getMunicipioCatalogo().getId());
            visita.setEstadoCatalogo(visitaDto.getEstadoCatalogo().getId());

            visita.setDomicilio1(visitaDto.getCalleCatalogo().getNombre());
            visita.setDomicilio2(visitaDto.getColoniaCatalogo().getNombre());
            visita.setLocalidad(visitaDto.getLocalidadCatalogo().getNombre());
            visita.setDomicilio3(visitaDto.getMunicipioCatalogo().getNombre());
            visita.setEstado(visitaDto.getEstadoCatalogo().getNombre());
        }
        visita.setNombreComercial(visitaDto.getNombreComercial());
        visita.setRazonSocial(visitaDto.getRazonSocial());
        visita.setObservaciones(visitaDto.getObservaciones());

        visita.setFechaVisita(LocalDate.parse(visitaDto.getFechaVisita()));
        visita.setTipoVisita(visitaDto.getTipoVisita());
        visita.setNumeroRegistro(visitaDto.getNumeroRegistro());
        visita.setNumeroOrden(visitaDto.getNumeroOrden());
        visita.setResponsable(visitaDto.getResponsable().getId());

        daoHelper.fulfillAuditorFields(false, visita, usuarioDto.getId());
        visitaRepository.save(visita);
        return daoToDtoConverter.convertDaoToDtoVisita(visita);
    }

    @Transactional
    @Override
    public VisitaDto eliminarVisita(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        visita.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, visita, usuarioDto.getId());
        visitaRepository.save(visita);
        return daoToDtoConverter.convertDaoToDtoVisita(visita);
    }

    @Transactional
    @Override
    public VisitaDto modificarRequerimiento(String uuid, String username, VisitaDto visitaDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || visitaDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la informacion del requerimiento en la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(visitaDto.isRequerimiento()) {
            visita.setRequerimiento(true);
            visita.setFechaTermino(LocalDate.parse(visitaDto.getFechaTermino()));
            visita.setDetallesRequerimiento(visitaDto.getDetallesRequerimiento());
        } else {
            visita.setRequerimiento(false);
            visita.setFechaTermino(null);
            visita.setDetallesRequerimiento(null);
        }
        daoHelper.fulfillAuditorFields(false, visita, usuarioDto.getId());
        visitaRepository.save(visita);
        return daoToDtoConverter.convertDaoToDtoVisita(visita);
    }

    @Override
    public File obtenerReporteExcelVisitas() throws Exception {
        List<Visita> visitas = visitaRepository.getAllByEliminadoFalse()
                .stream()
                .sorted((o1, o2) -> Integer.valueOf(o1.getEmpresa()).compareTo(o2.getEmpresa()))
                .collect(Collectors.toList());

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
            UsuarioDto responsable = usuarioService.getUserById(p.getResponsable());

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
