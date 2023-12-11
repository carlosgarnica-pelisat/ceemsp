package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.metadata.CanFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.dto.metadata.VehiculoFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.CanFotografiaRepository;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoFotografiaRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
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
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanFotografiaServiceImpl implements CanFotografiaService {

    private final Logger logger = LoggerFactory.getLogger(CanFotografiaService.class);
    private final CanFotografiaRepository canFotografiaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final ArchivosService archivosService;
    private final CanRepository canRepository;

    @Autowired
    public CanFotografiaServiceImpl(CanFotografiaRepository canFotografiaRepository, DaoToDtoConverter daoToDtoConverter,
                                    DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, EmpresaService empresaService,
                                    UsuarioService usuarioService, ArchivosService archivosService, CanRepository canRepository) {
        this.canFotografiaRepository = canFotografiaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.archivosService = archivosService;
        this.canRepository = canRepository;
    }

    @Override
    public List<CanFotografiaMetadata> mostrarCanFotografias(String uuid, String canUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o el can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el metadata de las fotos del can [{}]", canUuid);

        Can can = canRepository.getByUuid(canUuid);
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
    public File descargarFotografiaCan(String uuid, String canUuid, String fotografiaUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(fotografiaUuid)) {
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
    @Transactional
    public void guardarCanFotografia(String uuid, String personalUuid, String username, MultipartFile multipartFile, CanFotografiaMetadata metadata) {
        if (StringUtils.isBlank(uuid) || StringUtils.isBlank(personalUuid) || StringUtils.isBlank(username) || multipartFile == null) {
            logger.warn("El uuid de la empresa, el can o la foto vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando el nuevo can en la base de datos");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Can can = canRepository.getByUuidAndEliminadoFalse(personalUuid);

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
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.FOTOGRAFIA_CAN, uuid);
            canFotografia.setRuta(ruta);
            canFotografiaRepository.save(canFotografia);

            if(!can.isFotografiaCapturada()) {
                can.setFotografiaCapturada(true);
                daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());
                canRepository.save(can);
            }
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    @Transactional
    public void eliminarCanFotografia(String uuid, String canUuid, String fotografiaUuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(fotografiaUuid) || StringUtils.isBlank(username)) {
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
