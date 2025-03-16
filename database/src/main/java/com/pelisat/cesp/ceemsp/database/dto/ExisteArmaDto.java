package com.pelisat.cesp.ceemsp.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExisteArmaDto {
    private Boolean existe;
    private String matricula;
    private String serie;
    private ArmaDto arma;
}
