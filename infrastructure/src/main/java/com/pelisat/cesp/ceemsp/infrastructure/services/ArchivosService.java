package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ArchivosService {
    String guardarArchivoMultipart(MultipartFile multipartFile, TipoArchivoEnum tipoArchivo, String empresaUuid) throws IOException;
    String guardarArchivoMultipart(MultipartFile multipartFile, TipoArchivoEnum tipoArchivoEnum, String empresaUuid, boolean archivoEmpresa) throws IOException;
    String eliminarArchivo(String directorio);
}
