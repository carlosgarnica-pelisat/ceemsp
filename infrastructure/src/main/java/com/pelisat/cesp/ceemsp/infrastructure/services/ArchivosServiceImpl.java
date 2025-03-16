package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ArchivosServiceImpl implements ArchivosService {

    private final Logger logger = LoggerFactory.getLogger(ArchivosService.class);
    private static final String ROOT_FS = "/ceemsp/fs/files/";
    private static final String EMPRESAS_FOLDER = "empresas/";

    @Override
    public String guardarArchivoMultipart(MultipartFile multipartFile, TipoArchivoEnum tipoArchivo, String empresaUuid) throws IOException {
        return guardarArchivoMultipart(multipartFile, tipoArchivo, empresaUuid, true);
    }

    @Override
    public String guardarArchivoMultipart(MultipartFile multipartFile, TipoArchivoEnum tipoArchivo, String empresaUuid, boolean archivoEmpresa) throws IOException {
        if(multipartFile == null || tipoArchivo == null) {
            logger.warn("El archivo o el tipo de archivo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        String basePath = "";

        if(archivoEmpresa) {
            basePath = ROOT_FS + EMPRESAS_FOLDER + empresaUuid + "/";
        } else {
            basePath = ROOT_FS;
        }

        File file = new File(basePath + tipoArchivo.getRutaCarpeta() + tipoArchivo.getPrefijoArchivo() +
                "-" + RandomStringUtils.randomAlphanumeric(6) + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());

        return file.getAbsolutePath();
    }

    @Override
    public String guardarArchivo(File archivo, TipoArchivoEnum tipoArchivoEnum) throws Exception {
        if(archivo == null || tipoArchivoEnum == null) {
            logger.warn("El archivo o el tipo de archivo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        String basePath = ROOT_FS;

        File file = new File(basePath + tipoArchivoEnum.getRutaCarpeta() + tipoArchivoEnum.getPrefijoArchivo() +
                "-" + RandomStringUtils.randomAlphanumeric(6) + "." + FilenameUtils.getExtension(archivo.getName()));
        FileUtils.writeByteArrayToFile(file, Files.readAllBytes(file.toPath()));

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
