package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModalidadDto {
    private int id;
    private String uuid;
    private String nombre;
    private String descripcion;
    private TipoTramiteEnum tipo;
    private List<SubmodalidadDto> submodalidades;
}
