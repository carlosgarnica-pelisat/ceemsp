package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Service
public class ArchivosServiceImpl implements ArchivosService {

    private final Logger logger = LoggerFactory.getLogger(ArchivosService.class);
    private static final String ROOT_FS = "/ceemsp/fs/files/";
    private static final String EMPRESAS_FOLDER = "empresas/";

    @Override
    public String guardarArchivoMultipart(MultipartFile multipartFile, TipoArchivoEnum tipoArchivo) throws IOException {
        if(multipartFile == null || tipoArchivo == null) {
            logger.warn("El archivo o el tipo de archivo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        File file = new File(ROOT_FS + EMPRESAS_FOLDER + tipoArchivo.getRutaCarpeta() + tipoArchivo.getPrefijoArchivo() + "test");
        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());

        return file.getAbsolutePath();
    }

    @Override
    public String eliminarArchivo(String directorio) {
        if(StringUtils.isBlank(directorio)) {
            logger.warn("La ruta del archivo a liminar viene como nula o vacia");
            throw new InvalidDataException();
        }

        File file = new File(directorio);
        file.delete();
        return directorio;
    }
}
