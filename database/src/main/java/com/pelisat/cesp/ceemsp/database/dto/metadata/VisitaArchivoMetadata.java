package com.pelisat.cesp.ceemsp.database.dto.metadata;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitaArchivoMetadata {
    private int id;
    private String uuid;
    private String nombreArchivo;
    private String descripcion;
}