package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.dto.metadata.VisitaArchivoMetadata;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.PersonalFotografia;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.model.VisitaArchivo;
import com.pelisat.cesp.ceemsp.database.repository.VisitaArchivoRepository;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
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
public class VisitaArchivoServiceImpl implements VisitaArchivoService {

    private final Logger logger = LoggerFactory.getLogger(VisitaArchivoService.class);
    private final VisitaArchivoRepository visitaArchivoRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final VisitaRepository visitaRepository;
    private final DaoHelper daoHelper;
    private final ArchivosService archivosService;

    @Autowired
    public VisitaArchivoServiceImpl(VisitaArchivoRepository visitaArchivoRepository, EmpresaService empresaService,
                                    VisitaRepository visitaRepository, UsuarioService usuarioService, DaoHelper daoHelper,
                                    ArchivosService archivosService) {
        this.visitaArchivoRepository = visitaArchivoRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.visitaRepository = visitaRepository;
        this.daoHelper = daoHelper;
        this.archivosService = archivosService;
    }

    @Override
    public List<VisitaArchivoMetadata> obtenerArchivosPorVisita(String visitaUuid) {
        if(StringUtils.isBlank(visitaUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Mostrando los metadatos de los archivos de la visita con uuid [{}]", visitaUuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(visitaUuid);
        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<VisitaArchivo> metadata = visitaArchivoRepository.getAllByVisitaAndEliminadoFalse(visita.getId());

        return metadata.stream().map(m -> {
            VisitaArchivoMetadata vam = new VisitaArchivoMetadata();
            String[] tokens = m.getUbicacionArchivo().split("[\\\\|/]");
            vam.setId(m.getId());
            vam.setDescripcion(m.getDescripcion());
            vam.setUuid(m.getUuid());
            vam.setNombreArchivo(tokens[tokens.length - 1]);
            return vam;
        }).collect(Collectors.toList());
    }

    @Override
    public File descargarArchivoVisita(String visitaUuid, String archivoUuid) {
        if(StringUtils.isBlank(visitaUuid) || StringUtils.isBlank(archivoUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando el archivo de la visita con uuid [{}]", archivoUuid);
        VisitaArchivo visitaArchivo = visitaArchivoRepository.getByUuidAndEliminadoFalse(archivoUuid);

        if(visitaArchivo == null) {
            logger.warn("El archivo esta eliminado o no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(visitaArchivo.getUbicacionArchivo());
    }

    @Override
    @Transactional
    public VisitaArchivoMetadata guardarArchivo(String visitaUuid, String username, MultipartFile archivo, VisitaArchivoMetadata visitaArchivoMetadata) {
        if (StringUtils.isBlank(visitaUuid) || StringUtils.isBlank(username) || archivo == null || visitaArchivoMetadata == null) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando el archivo de la visita en la base de datos");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(visitaUuid);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        VisitaArchivo visitaArchivo = new VisitaArchivo();
        visitaArchivo.setVisita(visita.getId());
        visitaArchivo.setDescripcion(visitaArchivoMetadata.getDescripcion());
        daoHelper.fulfillAuditorFields(true, visitaArchivo, usuarioDto.getId());

        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(archivo, TipoArchivoEnum.VISITA_ARCHIVO, "", false);
            visitaArchivo.setUbicacionArchivo(ruta);
            visitaArchivoRepository.save(visitaArchivo);
            return null;
        } catch (IOException ioException) {
            logger.warn(ioException.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Override
    @Transactional
    public VisitaArchivoMetadata eliminarArchivo(String visitaUuid, String archivoUuid, String username) {
        if(StringUtils.isBlank(visitaUuid) || StringUtils.isBlank(archivoUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la fotografia con uuid [{}]", visitaUuid);
        VisitaArchivo visitaArchivo = visitaArchivoRepository.getByUuidAndEliminadoFalse(archivoUuid);

        if(visitaArchivo == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        archivosService.eliminarArchivo(visitaArchivo.getUbicacionArchivo());
        visitaArchivo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, visitaArchivo, usuarioDto.getId());
        visitaArchivoRepository.save(visitaArchivo);
        VisitaArchivoMetadata visitaArchivoMetadata = new VisitaArchivoMetadata();
        String[] tokens = visitaArchivo.getUbicacionArchivo().split("[\\\\|/]");
        visitaArchivoMetadata.setId(visitaArchivo.getId());
        visitaArchivoMetadata.setDescripcion(visitaArchivo.getDescripcion());
        visitaArchivoMetadata.setUuid(visitaArchivo.getUuid());
        visitaArchivoMetadata.setNombreArchivo(tokens[tokens.length - 1]);

        return visitaArchivoMetadata;
    }
}
