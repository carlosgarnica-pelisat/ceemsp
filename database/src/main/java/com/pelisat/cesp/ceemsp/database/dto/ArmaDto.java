package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArmaDto {
    private int id;
    private String uuid;
    private EmpresaLicenciaColectivaDto licenciaColectiva;
    private ArmaTipoEnum tipo;
    private ArmaClaseDto clase;
    private ArmaMarcaDto marca;
    private String calibre;
    private EmpresaDomicilioDto bunker;
    private ArmaStatusEnum status;
}
