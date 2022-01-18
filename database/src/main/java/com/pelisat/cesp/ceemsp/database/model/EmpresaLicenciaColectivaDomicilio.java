package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EMPRESAS_LICENCIAS_COLECTIVAS_DOMICILIOS")
@Getter
@Setter
public class EmpresaLicenciaColectivaDomicilio extends CommonModel {
    @Column(name = "LICENCIA_COLECTIVA", nullable = false)
    private int licenciaColectiva;

    @Column(name = "DOMICILIO", nullable = false)
    private int domicilio;
}
