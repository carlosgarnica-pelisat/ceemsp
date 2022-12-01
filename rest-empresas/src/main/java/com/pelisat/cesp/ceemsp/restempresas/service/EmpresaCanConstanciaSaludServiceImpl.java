package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanConstanciaSaludDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CanConstanciaSalud;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.CanConstanciaSaludRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaCanConstanciaSaludServiceImpl implements EmpresaCanConstanciaSaludService {
    private final Logger logger = LoggerFactory.getLogger(EmpresaCanConstanciaSaludService.class);
    private final CanRepository canRepository;
    private final UsuarioService usuarioService;
    private final CanConstanciaSaludRepository canConstanciaSaludRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;

    @Autowired
    public EmpresaCanConstanciaSaludServiceImpl(
        CanRepository canRepository, UsuarioService usuarioService, CanConstanciaSaludRepository canConstanciaSaludRepository,
        DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
        ArchivosService archivosService
    ) {
        this.canRepository = canRepository;
        this.canConstanciaSaludRepository = canConstanciaSaludRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.archivosService = archivosService;
    }

    @Override
    public List<CanConstanciaSaludDto> obtenerConstanciasSaludPorCanUuid(String canUuid) {
        if(StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo constancias de salud del can con uuid [{}]", canUuid);

        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        List<CanConstanciaSalud> canConstanciasdeSalud = canConstanciaSaludRepository.findAllByCanAndEliminadoFalse(can.getId());
        return canConstanciasdeSalud.stream().map(daoToDtoConverter::convertDaoToDtoCanConstanciaSalud).collect(Collectors.toList());
    }

    @Override
    public File obtenerPdfConstanciaSalud(String canUuid, String constanciaSaludUuid) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(constanciaSaludUuid)) {
            logger.warn("El uuid de la empresa, el can, el usuario o la constancia de salud de vacunacion a guardar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando la cartilla de vacunacion en PDF con el uuid [{}]", constanciaSaludUuid);

        CanConstanciaSalud canConstanciaSalud = canConstanciaSaludRepository.findByUuidAndEliminadoFalse(constanciaSaludUuid);

        if(canConstanciaSalud == null) {
            logger.warn("La constancia no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        if(StringUtils.isBlank(canConstanciaSalud.getRutaDocumento())) {
            logger.warn("No hay archivo definido para esta constancia de salud");
            throw new NotFoundResourceException();
        }

        File constanciaSaludPdf = new File(canConstanciaSalud.getRutaDocumento());

        if(!constanciaSaludPdf.exists() &&
                constanciaSaludPdf.isDirectory()) {
            logger.warn("El archvo no existe en el sistema de archivos");
            throw new NotFoundResourceException();
        }

        return constanciaSaludPdf;
    }

    @Override
    public CanConstanciaSaludDto guardarConstanciaSalud(String canUuid, String username, CanConstanciaSaludDto canConstanciaSaludDto, MultipartFile archivo) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canConstanciaSaludDto == null) {
            logger.warn("El uuid de la empresa, el can, el usuario o la cartilla de vacunacion vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando constancia de salud al can [{}]", username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new NotFoundResourceException();
        }

        CanConstanciaSalud canConstanciaSalud = dtoToDaoConverter.convertDtoToDaoCanConstanciaSalud(canConstanciaSaludDto);
        canConstanciaSalud.setCan(can.getId());
        canConstanciaSalud.setFechaExpedicion(LocalDate.parse(canConstanciaSaludDto.getFechaExpedicion()));
        daoHelper.fulfillAuditorFields(true, canConstanciaSalud, usuarioDto.getId());
        String ruta = "";

        try {
            ruta = archivosService.guardarArchivoMultipart(archivo, TipoArchivoEnum.CONSTANCIA_SALUD_CAN, usuarioDto.getEmpresa().getUuid());
            canConstanciaSalud.setRutaDocumento(ruta);
            CanConstanciaSalud canConstanciaSaludCreada = canConstanciaSaludRepository.save(canConstanciaSalud);

            return daoToDtoConverter.convertDaoToDtoCanConstanciaSalud(canConstanciaSaludCreada);
        } catch(Exception ex) {
            logger.warn(ex.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    public CanConstanciaSaludDto modificarConstanciaSalud(String canUuid, String constanciaUuid, String username, CanConstanciaSaludDto canConstanciaSaludDto, MultipartFile archivo) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(constanciaUuid) || StringUtils.isBlank(username) || canConstanciaSaludDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la constancia de salud con el uuid [{}]", canUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        CanConstanciaSalud canConstanciaSalud = canConstanciaSaludRepository.findByUuidAndEliminadoFalse(constanciaUuid);
        if(canConstanciaSalud == null) {
            logger.warn("La constancia de salud del can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(archivo != null) {
            logger.info("Se subio con un archivo. Eliminando y modificando");
            archivosService.eliminarArchivo(canConstanciaSalud.getRutaDocumento());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(archivo, TipoArchivoEnum.CONSTANCIA_SALUD_CAN, usuarioDto.getEmpresa().getUuid());
                canConstanciaSalud.setRutaDocumento(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        canConstanciaSalud.setFechaExpedicion(LocalDate.parse(canConstanciaSaludDto.getFechaExpedicion()));
        canConstanciaSalud.setCedula(canConstanciaSaludDto.getCedula());
        canConstanciaSalud.setExpedidoPor(canConstanciaSaludDto.getExpedidoPor());
        daoHelper.fulfillAuditorFields(false, canConstanciaSalud, usuarioDto.getId());

        canConstanciaSaludRepository.save(canConstanciaSalud);

        return daoToDtoConverter.convertDaoToDtoCanConstanciaSalud(canConstanciaSalud);
    }

    @Override
    public CanConstanciaSaludDto eliminarConstanciaSalud(String canUuid, String constanciaUuid, String username) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(constanciaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la constancia de salud con el uuid [{}]", canUuid);

        CanConstanciaSalud canConstanciaSalud = canConstanciaSaludRepository.findByUuidAndEliminadoFalse(constanciaUuid);
        if(canConstanciaSalud == null) {
            logger.warn("La constancia de salud del can no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        canConstanciaSalud.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, canConstanciaSalud, usuarioDto.getId());

        canConstanciaSaludRepository.save(canConstanciaSalud);

        return daoToDtoConverter.convertDaoToDtoCanConstanciaSalud(canConstanciaSalud);
    }
}