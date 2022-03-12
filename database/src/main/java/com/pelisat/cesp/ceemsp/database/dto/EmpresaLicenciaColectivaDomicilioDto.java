package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaLicenciaColectivaDomicilioDto {
    private int id;
    private String uuid;
    private EmpresaDomicilioDto empresaDomicilio;
}
