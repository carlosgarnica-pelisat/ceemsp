package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteAsignacionPersonalDto {
    private int id;
    private String uuid;
    private ClienteDomicilioDto domicilio;
    private PersonaDto personal;
    private boolean eliminado;
}
