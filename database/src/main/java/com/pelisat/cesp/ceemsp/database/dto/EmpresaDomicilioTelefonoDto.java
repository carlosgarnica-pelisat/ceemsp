package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.TipoTelefonoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaDomicilioTelefonoDto {
    private int id;
    private String uuid;
    private TipoTelefonoEnum tipoTelefono;
    private String telefono;
}
