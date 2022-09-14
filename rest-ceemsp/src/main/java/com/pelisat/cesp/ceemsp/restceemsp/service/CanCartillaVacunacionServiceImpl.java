package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.CanCartillaVacunacionRepository;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanCartillaVacunacionServiceImpl implements CanCartillaVacunacionService{

    private final Logger logger = LoggerFactory.getLogger(CanCartillaVacunacionService.class);
    private final UsuarioService usuarioService;
    private final CanRepository canRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;
    private final CanCartillaVacunacionRepository canCartillaVacunacionRepository;

    @Autowired
    public CanCartillaVacunacionServiceImpl(UsuarioService usuarioService, CanRepository canRepository, DaoToDtoConverter daoToDtoConverter,
                                            DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, CanCartillaVacunacionRepository canCartillaVacunacionRepository,
                                            ArchivosService archivosService) {
        this.usuarioService = usuarioService;
        this.canRepository = canRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.canCartillaVacunacionRepository = canCartillaVacunacionRepository;
        this.archivosService = archivosService;
    }

    @Override
    public List<CanCartillaVacunacionDto> obtenerCartillasVacunacionPorCanUuid(String empresaUuid, String canUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las cartillas de vacunacion para el can con uuid [{}]", canUuid);

        Can can = canRepository.getByUuid(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanCartillaVacunacion> canCartillaVacunaciones = canCartillaVacunacionRepository.findAllByCanAndEliminadoFalse(can.getId());
        return canCartillaVacunaciones.stream().map(daoToDtoConverter::convertDaoToDtoCanCartillaVacunacion).collect(Collectors.toList());
    }

    @Override
    public List<CanCartillaVacunacionDto> obtenerTodasCartillasVacunacionPorCanUuid(String empresaUuid, String canUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("Alguno de los parametros viene invalido");
            throw new InvalidDataException();
        }
        logger.info("Obteniendo todas las cartillas de vacunacion por el can con uuid [{}]", canUuid);
        Can can = canRepository.getByUuid(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanCartillaVacunacion> canCartillaVacunaciones = canCartillaVacunacionRepository.findAllByCan(can.getId());
        return canCartillaVacunaciones.stream().map(daoToDtoConverter::convertDaoToDtoCanCartillaVacunacion).collect(Collectors.toList());
    }

    @Override
    public CanCartillaVacunacionDto guardarCartillaVacunacion(String empresaUuid, String canUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canCartillaVacunacionDto == null) {
            logger.warn("El uuid de la empresa, el can, el usuario o la cartilla de vacunacion a guardar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nueva cartilla de vacunacion al can [{}]", username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        CanCartillaVacunacion canCartillaVacunacion = dtoToDaoConverter.convertDtoToDaoCanCartillaVacunacion(canCartillaVacunacionDto);
        canCartillaVacunacion.setCan(can.getId());
        canCartillaVacunacion.setFechaExpedicion(LocalDate.parse(canCartillaVacunacionDto.getFechaExpedicion()));
        daoHelper.fulfillAuditorFields(true, canCartillaVacunacion, usuarioDto.getId());

        String ruta = "";

        try {
            ruta = archivosService.guardarArchivoMultipart(archivo, TipoArchivoEnum.CARTILLA_VACUNACION_CAN, empresaUuid);
            canCartillaVacunacion.setRutaDocumento(ruta);
            CanCartillaVacunacion canCartillaVacunacionCreada = canCartillaVacunacionRepository.save(canCartillaVacunacion);

            return daoToDtoConverter.convertDaoToDtoCanCartillaVacunacion(canCartillaVacunacionCreada);
        } catch(Exception ex) {
            logger.warn(ex.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    public File obtenerPdfCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(cartillaUuid)) {
            logger.warn("El uuid de la empresa, el can, el usuario o la cartilla de vacunacion a guardar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando la cartilla de vacunacion en PDF con el uuid [{}]", cartillaUuid);

        CanCartillaVacunacion canCartillaVacunacion = canCartillaVacunacionRepository.findByUuidAndEliminadoFalse(cartillaUuid);

        if(canCartillaVacunacion == null) {
            logger.warn("La cartilla no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        if(StringUtils.isBlank(canCartillaVacunacion.getRutaDocumento())) {
            logger.warn("No hay archivo definido para esta cartilla de vacunacion");
            throw new NotFoundResourceException();
        }

        File cartillaVacunacionPdf = new File(canCartillaVacunacion.getRutaDocumento());

        if(!cartillaVacunacionPdf.exists() &&
                cartillaVacunacionPdf.isDirectory()) {
            logger.warn("El archvo no existe en el sistema de archivos");
            throw new NotFoundResourceException();
        }

        return cartillaVacunacionPdf;
    }

    @Override
    public CanCartillaVacunacionDto modificarCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(cartillaUuid) || StringUtils.isBlank(username) || canCartillaVacunacionDto == null) {
            logger.warn("Alguno de los parametros enviados no es valido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la cartilla de vacunacion con el uuid [{}]", cartillaUuid);

        CanCartillaVacunacion canCartillaVacunacion = canCartillaVacunacionRepository.findByUuidAndEliminadoFalse(cartillaUuid);
        if(canCartillaVacunacion == null) {
            logger.warn("La cartilla de vacunacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Eliminando y modificando");
            archivosService.eliminarArchivo(canCartillaVacunacion.getRutaDocumento());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.CARTILLA_VACUNACION_CAN, empresaUuid);
                canCartillaVacunacion.setRutaDocumento(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        canCartillaVacunacion.setCedula(canCartillaVacunacionDto.getCedula());
        canCartillaVacunacion.setFechaExpedicion(LocalDate.parse(canCartillaVacunacionDto.getFechaExpedicion()));
        canCartillaVacunacion.setExpedidoPor(canCartillaVacunacionDto.getExpedidoPor());
        daoHelper.fulfillAuditorFields(false, canCartillaVacunacion, usuario.getId());
        canCartillaVacunacionRepository.save(canCartillaVacunacion);

        return daoToDtoConverter.convertDaoToDtoCanCartillaVacunacion(canCartillaVacunacion);
    }

    @Override
    public CanCartillaVacunacionDto borrarCartillaVacunacion(String empresaUuid, String canUuid, String cartillaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(cartillaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros enviados no es valido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la cartilla de vacunacion con el uuid [{}]", cartillaUuid);

        CanCartillaVacunacion canCartillaVacunacion = canCartillaVacunacionRepository.findByUuidAndEliminadoFalse(cartillaUuid);
        if(canCartillaVacunacion == null) {
            logger.warn("La cartilla de vacunacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        canCartillaVacunacion.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, canCartillaVacunacion, usuario.getId());
        canCartillaVacunacionRepository.save(canCartillaVacunacion);

        return daoToDtoConverter.convertDaoToDtoCanCartillaVacunacion(canCartillaVacunacion);
    }
}
