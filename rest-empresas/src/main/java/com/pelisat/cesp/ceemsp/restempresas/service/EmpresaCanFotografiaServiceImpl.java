package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.metadata.CanFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CanFotografia;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.CanFotografiaRepository;
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
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaCanFotografiaServiceImpl implements EmpresaCanFotografiaService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaCanFotografiaService.class);
    private final CanFotografiaRepository canFotografiaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final ArchivosService archivosService;
    private final CanRepository canRepository;

    @Autowired
    public EmpresaCanFotografiaServiceImpl(CanFotografiaRepository canFotografiaRepository, DaoToDtoConverter daoToDtoConverter,
                                           DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                           UsuarioService usuarioService, ArchivosService archivosService,
                                           CanRepository canRepository) {
        this.canFotografiaRepository = canFotografiaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.archivosService = archivosService;
        this.canRepository = canRepository;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<CanFotografiaMetadata> mostrarCanFotografias(String canUuid) {
        if(StringUtils.isBlank(canUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el metadata de las fotos del can [{}]", canUuid);

        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        if(can == null) {
            logger.warn("el can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<CanFotografia> metadata = canFotografiaRepository.getAllByCanAndEliminadoFalse(can.getId());

        return metadata.stream().map(m -> {
            CanFotografiaMetadata cfm = new CanFotografiaMetadata();
            String[] tokens = m.getRuta().split("[\\\\|/]");
            cfm.setId(m.getId());
            cfm.setDescripcion(m.getDescripcion());
            cfm.setUuid(m.getUuid());
            cfm.setNombreArchivo(tokens[tokens.length - 1]);
            return cfm;
        }).collect(Collectors.toList());
    }

    @Override
    public File descargarFotografiaCan(String canUuid, String fotografiaUuid) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(fotografiaUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando la fotografia del can con uuid [{}]", fotografiaUuid);
        CanFotografia canFotografia = canFotografiaRepository.getByUuidAndEliminadoFalse(fotografiaUuid);

        if(canFotografia == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(canFotografia.getRuta());
    }

    @Override
    public void guardarCanFotografia(String canUuid, String username, MultipartFile multipartFile, CanFotografiaMetadata metadata) {
        if (StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || multipartFile == null) {
            logger.warn("El uuid de la empresa, el can o la foto vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando el nuevo can en la base de datos");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);

        if(can == null) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        CanFotografia canFotografia = new CanFotografia();
        canFotografia.setCan(can.getId());
        canFotografia.setDescripcion(metadata.getDescripcion());
        daoHelper.fulfillAuditorFields(true, canFotografia, usuarioDto.getId());

        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.FOTOGRAFIA_CAN, usuarioDto.getEmpresa().getUuid());
            canFotografia.setRuta(ruta);
            canFotografiaRepository.save(canFotografia);
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    public void eliminarCanFotografia(String canUuid, String fotografiaUuid, String username) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(fotografiaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la fotografia del can con uuid [{}]", fotografiaUuid);
        CanFotografia canFotografia = canFotografiaRepository.getByUuidAndEliminadoFalse(fotografiaUuid);

        if(canFotografia == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        archivosService.eliminarArchivo(canFotografia.getRuta());
        canFotografia.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, canFotografia, usuarioDto.getId());
        canFotografiaRepository.save(canFotografia);
    }
}
