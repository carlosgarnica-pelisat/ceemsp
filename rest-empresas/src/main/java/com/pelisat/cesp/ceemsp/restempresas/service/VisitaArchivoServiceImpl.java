package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.metadata.VisitaArchivoMetadata;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.model.VisitaArchivo;
import com.pelisat.cesp.ceemsp.database.repository.VisitaArchivoRepository;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitaArchivoServiceImpl implements VisitaArchivoService {

    private final Logger logger = LoggerFactory.getLogger(VisitaArchivoServiceImpl.class);
    private final VisitaRepository visitaRepository;
    private final VisitaArchivoRepository visitaArchivoRepository;

    @Autowired
    public VisitaArchivoServiceImpl(VisitaArchivoRepository visitaArchivoRepository, VisitaRepository visitaRepository) {
        this.visitaRepository = visitaRepository;
        this.visitaArchivoRepository = visitaArchivoRepository;
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
}
