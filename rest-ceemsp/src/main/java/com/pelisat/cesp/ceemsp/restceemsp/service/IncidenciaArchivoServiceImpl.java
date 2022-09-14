package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.metadata.IncidenciaArchivoMetadata;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.Incidencia;
import com.pelisat.cesp.ceemsp.database.model.IncidenciaArchivo;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaArchivoRepository;
import com.pelisat.cesp.ceemsp.database.repository.IncidenciaRepository;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;

@Service
public class IncidenciaArchivoServiceImpl implements IncidenciaArchivoService {

    private final IncidenciaArchivoRepository incidenciaArchivoRepository;
    private final UsuarioService usuarioService;
    private final IncidenciaRepository incidenciaRepository;
    private final ArchivosService archivosService;
    private final DaoHelper<IncidenciaArchivo> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(IncidenciaArchivoService.class);

    @Autowired
    public IncidenciaArchivoServiceImpl(IncidenciaArchivoRepository incidenciaArchivoRepository, UsuarioService usuarioService,
                                        IncidenciaRepository incidenciaRepository, ArchivosService archivosService,
                                        DaoHelper<IncidenciaArchivo> daoHelper) {
        this.incidenciaArchivoRepository = incidenciaArchivoRepository;
        this.usuarioService = usuarioService;
        this.incidenciaRepository = incidenciaRepository;
        this.archivosService = archivosService;
        this.daoHelper = daoHelper;
    }

    @Override
    public File descargarArchivoIncidencia(String empresaUuid, String incidenciaUUid, String archivoUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUUid) || StringUtils.isBlank(archivoUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando el archivo de la incidencia com el uuid [{}]", archivoUuid);

        IncidenciaArchivo incidenciaArchivo = incidenciaArchivoRepository.getByUuidAndEliminadoFalse(archivoUuid);

        if(incidenciaArchivo == null) {
            logger.warn("La escritura no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(incidenciaArchivo.getRutaArchivo());
    }

    @Override
    public IncidenciaArchivoMetadata agregarArchivoIncidencia(String empresaUuid, String incidenciaUuid, String username, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(username) || multipartFile == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Guardando un archivo en la incidencia [{}]", incidenciaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);
        if(incidencia == null) {
            logger.warn("La incidencia no se ha encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        try {
            String ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_INCIDENCIA, empresaUuid);

            IncidenciaArchivo incidenciaArchivo = new IncidenciaArchivo();
            incidenciaArchivo.setIncidencia(incidencia.getId());
            incidenciaArchivo.setRutaArchivo(ruta);
            daoHelper.fulfillAuditorFields(true, incidenciaArchivo, usuarioDto.getId());

            IncidenciaArchivo archivoCreado = incidenciaArchivoRepository.save(incidenciaArchivo);

            IncidenciaArchivoMetadata incidenciaArchivoMetadata = new IncidenciaArchivoMetadata();
            incidenciaArchivoMetadata.setId(archivoCreado.getId());
            incidenciaArchivoMetadata.setUuid(archivoCreado.getUuid());
            incidenciaArchivoMetadata.setNombreArchivo(archivoCreado.getRutaArchivo());

            return incidenciaArchivoMetadata;
        } catch(Exception ex) {
            logger.warn("No se ha podido guardar el archivo. ", ex);
            throw new InvalidDataException();
        }
    }

    @Transactional
    @Override
    public IncidenciaArchivoMetadata eliminarArchivoIncidencia(String empresaUuid, String incidenciaUuid, String archivoIncidenciaUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(incidenciaUuid) || StringUtils.isBlank(archivoIncidenciaUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Guardando un archivo en la incidencia [{}]", incidenciaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Incidencia incidencia = incidenciaRepository.getByUuidAndEliminadoFalse(incidenciaUuid);
        if(incidencia == null) {
            logger.warn("La incidencia no se ha encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        IncidenciaArchivo incidenciaArchivo = incidenciaArchivoRepository.getByUuidAndEliminadoFalse(archivoIncidenciaUuid);

        if(incidenciaArchivo == null) {
            logger.warn("El archivo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        incidenciaArchivo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, incidenciaArchivo, usuarioDto.getId());
        incidenciaArchivoRepository.save(incidenciaArchivo);

        return null;
    }
}
