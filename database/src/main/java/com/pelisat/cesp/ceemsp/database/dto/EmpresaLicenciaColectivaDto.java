package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaLicenciaColectivaDto {
    private String numeroOficio;
    private ModalidadDto modalidad;
    private SubmodalidadDto submodalidad;
    private String fechaInicio;
    private String fechaFin;
    private String rutaDocumento;
}
