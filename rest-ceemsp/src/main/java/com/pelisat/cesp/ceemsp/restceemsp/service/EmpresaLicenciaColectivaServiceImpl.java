package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaLicenciaColectiva;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaLicenciaColectivaRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaLicenciaColectivaServiceImpl implements EmpresaLicenciaColectivaService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final ModalidadService modalidadService;
    private final SubmodalidadService submodalidadService;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaLicenciaColectivaService.class);

    @Autowired
    public EmpresaLicenciaColectivaServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                               EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository,
                                               UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper,
                                               EmpresaService empresaService, ModalidadService modalidadService,
                                               SubmodalidadService submodalidadService, ArchivosService archivosService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.modalidadService = modalidadService;
        this.submodalidadService = submodalidadService;
        this.archivosService = archivosService;
    }

    @Override
    public List<EmpresaLicenciaColectivaDto> obtenerLicenciasColectivasPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        List<EmpresaLicenciaColectiva> empresaLicenciasColectivas = empresaLicenciaColectivaRepository
                .findAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return empresaLicenciasColectivas.stream().map(elc -> {
            EmpresaLicenciaColectivaDto elcd = daoToDtoConverter.convertDaoToDtoEmpresaLicenciaColectiva(elc);
            elcd.setModalidad(modalidadService.obtenerModalidadPorId(elc.getModalidad()));
            return elcd;
        }).collect(Collectors.toList());
    }

    @Override
    public EmpresaLicenciaColectivaDto obtenerLicenciaColectivaPorUuid(String empresaUuid, String licenciaUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(licenciaUuid)) {
            logger.warn("Uno o los uuid de la empresa o la licencia vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la licencia colectiva con el uuid [{}]", licenciaUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaLicenciaColectiva licenciaColectiva = empresaLicenciaColectivaRepository.findByUuidAndEliminadoFalse(licenciaUuid);

        if(licenciaColectiva == null) {
            logger.warn("La licencia colectiva no existe");
            throw new NotFoundResourceException();
        }

        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = daoToDtoConverter.convertDaoToDtoEmpresaLicenciaColectiva(licenciaColectiva);

        if(!soloEntidad) {
            empresaLicenciaColectivaDto.setModalidad(modalidadService.obtenerModalidadPorId(licenciaColectiva.getModalidad()));
            if(licenciaColectiva.getSubmodalidad() > 0) {
                empresaLicenciaColectivaDto.setSubmodalidad(submodalidadService.obtenerSubmodalidadPorId(licenciaColectiva.getId()));
            }
        }

        return empresaLicenciaColectivaDto;
    }

    @Override
    public EmpresaLicenciaColectivaDto guardarLicenciaColectiva(String empresaUuid, String username, EmpresaLicenciaColectivaDto licenciaColectivaDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(username) || licenciaColectivaDto == null) {
            logger.warn("Alguno de los parametros ingresados es invalido");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaLicenciaColectiva empresaLicenciaColectiva = new EmpresaLicenciaColectiva();
        empresaLicenciaColectiva.setModalidad(licenciaColectivaDto.getModalidad().getId());
        if(licenciaColectivaDto.getSubmodalidad() != null) {
            empresaLicenciaColectiva.setSubmodalidad(licenciaColectivaDto.getSubmodalidad().getId());
        }
        empresaLicenciaColectiva.setNumeroOficio(licenciaColectivaDto.getNumeroOficio());
        empresaLicenciaColectiva.setFechaInicio(LocalDate.parse(licenciaColectivaDto.getFechaInicio()));
        empresaLicenciaColectiva.setFechaFin(LocalDate.parse(licenciaColectivaDto.getFechaFin()));
        empresaLicenciaColectiva.setEmpresa(empresaDto.getId());
        daoHelper.fulfillAuditorFields(true, empresaLicenciaColectiva, usuarioDto.getId());

        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.LICENCIA_COLECTIVA, empresaUuid);
            empresaLicenciaColectiva.setRutaDocumento(ruta);
            EmpresaLicenciaColectiva licenciaColectivaCreada = empresaLicenciaColectivaRepository.save(empresaLicenciaColectiva);
            return daoToDtoConverter.convertDaoToDtoEmpresaLicenciaColectiva(licenciaColectivaCreada);
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    public File descargarLicenciaPdf(String empresaUuid, String licenciaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(licenciaUuid)) {
            logger.warn("Los datos vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Descargando el archivo de la licencia con uuid [{}]", licenciaUuid);

        EmpresaLicenciaColectiva licenciaColectiva = empresaLicenciaColectivaRepository.findByUuidAndEliminadoFalse(licenciaUuid);

        if(licenciaColectiva == null) {
            logger.warn("La licencia colectiva no existe");
            throw new NotFoundResourceException();
        }

        return new File(licenciaColectiva.getRutaDocumento());
    }

    @Override
    public EmpresaLicenciaColectivaDto modificarLicenciaColectiva(String empresaUuid, String licenciaUuid, String username, EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(licenciaUuid) || StringUtils.isBlank(username) || empresaLicenciaColectivaDto == null) {
            logger.warn("Alguno de los parametros ingresados es invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la licencia colectiva con el uuid [{}]", licenciaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaLicenciaColectiva licenciaColectiva = empresaLicenciaColectivaRepository.findByUuidAndEliminadoFalse(licenciaUuid);

        if(licenciaColectiva == null) {
            logger.warn("La licencia colectiva no existe");
            throw new NotFoundResourceException();
        }

        licenciaColectiva.setModalidad(empresaLicenciaColectivaDto.getModalidad().getId());
        licenciaColectiva.setFechaInicio(LocalDate.parse(empresaLicenciaColectivaDto.getFechaInicio()));
        licenciaColectiva.setFechaFin(LocalDate.parse(empresaLicenciaColectivaDto.getFechaFin()));
        licenciaColectiva.setNumeroOficio(empresaLicenciaColectivaDto.getNumeroOficio());
        if(empresaLicenciaColectivaDto.getSubmodalidad() != null) {
            licenciaColectiva.setSubmodalidad(empresaLicenciaColectivaDto.getSubmodalidad().getId());
        }

        daoHelper.fulfillAuditorFields(false, licenciaColectiva, usuarioDto.getId());
        empresaLicenciaColectivaRepository.save(licenciaColectiva);

        return empresaLicenciaColectivaDto;
    }

    @Override
    public EmpresaLicenciaColectivaDto eliminarLicenciaColectiva(String empresaUuid, String licenciaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(licenciaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros ingresados es invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la licencia colectiva con el uuid [{}]", licenciaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaLicenciaColectiva licenciaColectiva = empresaLicenciaColectivaRepository.findByUuidAndEliminadoFalse(licenciaUuid);

        if(licenciaColectiva == null) {
            logger.warn("La licencia colectiva no existe");
            throw new NotFoundResourceException();
        }

        licenciaColectiva.setEliminado(true);

        daoHelper.fulfillAuditorFields(false, licenciaColectiva, usuarioDto.getId());
        empresaLicenciaColectivaRepository.save(licenciaColectiva);

        return daoToDtoConverter.convertDaoToDtoEmpresaLicenciaColectiva(licenciaColectiva);
    }
}
