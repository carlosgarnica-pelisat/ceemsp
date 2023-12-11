package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanCartillaVacunacionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CanCartillaVacunacion;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.CanCartillaVacunacionRepository;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaCanCartillaVacunacionServiceImpl implements EmpresaCanCartillaVacunacionService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaCanCartillaVacunacionService.class);
    private final UsuarioService usuarioService;
    private final CanRepository canRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;
    private final CanCartillaVacunacionRepository canCartillaVacunacionRepository;

    @Autowired
    public EmpresaCanCartillaVacunacionServiceImpl(
            UsuarioService usuarioService, CanRepository canRepository, DaoToDtoConverter daoToDtoConverter,
            DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, ArchivosService archivosService,
            CanCartillaVacunacionRepository canCartillaVacunacionRepository
    ) {
        this.usuarioService = usuarioService;
        this.canRepository = canRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.archivosService = archivosService;
        this.canCartillaVacunacionRepository = canCartillaVacunacionRepository;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<CanCartillaVacunacionDto> obtenerCartillasVacunacionPorCanUuid(String canUuid) {
        if(StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las cartillas de vacunacion para el can con uuid [{}]", canUuid);

        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanCartillaVacunacion> canCartillaVacunaciones = canCartillaVacunacionRepository.findAllByCanAndEliminadoFalse(can.getId());
        return canCartillaVacunaciones.stream().map(daoToDtoConverter::convertDaoToDtoCanCartillaVacunacion).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CanCartillaVacunacionDto guardarCartillaVacunacion(String canUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canCartillaVacunacionDto == null) {
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
            ruta = archivosService.guardarArchivoMultipart(archivo, TipoArchivoEnum.CARTILLA_VACUNACION_CAN, usuarioDto.getEmpresa().getUuid());
            canCartillaVacunacion.setRutaDocumento(ruta);
            CanCartillaVacunacion canCartillaVacunacionCreada = canCartillaVacunacionRepository.save(canCartillaVacunacion);

            if(!can.isVacunacionCapturada()) {
                can.setVacunacionCapturada(true);
                daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());
                canRepository.save(can);
            }

            return daoToDtoConverter.convertDaoToDtoCanCartillaVacunacion(canCartillaVacunacionCreada);
        } catch(Exception ex) {
            logger.warn(ex.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    public File obtenerPdfCartillaVacunacion(String canUuid, String cartillaUuid) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(cartillaUuid)) {
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
    @Transactional
    public CanCartillaVacunacionDto modificarCartillaVacunacion(String canUuid, String cartillaUuid, String username, CanCartillaVacunacionDto canCartillaVacunacionDto, MultipartFile archivo) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(cartillaUuid) || StringUtils.isBlank(username) || canCartillaVacunacionDto == null) {
            logger.warn("Alguno de los parametros enviados no es valido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la cartilla de vacunacion con el uuid [{}]", cartillaUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        CanCartillaVacunacion canCartillaVacunacion = canCartillaVacunacionRepository.findByUuidAndEliminadoFalse(cartillaUuid);
        if(canCartillaVacunacion == null) {
            logger.warn("La cartilla de vacunacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(archivo != null) {
            logger.info("Se subio con un archivo. Eliminando y modificando");
            archivosService.eliminarArchivo(canCartillaVacunacion.getRutaDocumento());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(archivo, TipoArchivoEnum.CARTILLA_VACUNACION_CAN, usuario.getEmpresa().getUuid());
                canCartillaVacunacion.setRutaDocumento(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        canCartillaVacunacion.setCedula(canCartillaVacunacionDto.getCedula());
        canCartillaVacunacion.setFechaExpedicion(LocalDate.parse(canCartillaVacunacionDto.getFechaExpedicion()));
        canCartillaVacunacion.setExpedidoPor(canCartillaVacunacionDto.getExpedidoPor());
        daoHelper.fulfillAuditorFields(false, canCartillaVacunacion, usuario.getId());
        canCartillaVacunacionRepository.save(canCartillaVacunacion);

        return daoToDtoConverter.convertDaoToDtoCanCartillaVacunacion(canCartillaVacunacion);
    }

    @Override
    @Transactional
    public CanCartillaVacunacionDto borrarCartillaVacunacion(String canUuid, String cartillaUuid, String username) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(cartillaUuid) || StringUtils.isBlank(username)) {
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
