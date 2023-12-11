package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.NotificacionArgosTipoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacionArgosDto {
    private int id;
    private String uuid;
    private String motivo;
    private String descripcion;
    private NotificacionArgosTipoEnum tipo;
    private String fechaCreacion;
    private boolean leido;
    private String ubicacion;
}
