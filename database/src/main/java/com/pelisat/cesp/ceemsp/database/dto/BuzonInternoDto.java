package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuzonInternoDto {
    private int id;
    private String uuid;
    private String motivo;
    private String mensaje;
    private List<BuzonInternoDestinatarioDto> destinatarios;
}
