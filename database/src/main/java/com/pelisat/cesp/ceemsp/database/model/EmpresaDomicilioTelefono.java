package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.TipoTelefonoEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "EMPRESAS_DOMICILIOS_TELEFONOS")
public class EmpresaDomicilioTelefono extends CommonModel {
    @Column(name = "DOMICILIO", nullable = false)
    private int domicilio;

    @Column(name = "TIPO_TELEFONO", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTelefonoEnum tipoTelefono;

    @Column(name = "TELEFONO", nullable = false)
    private String telefono;
}
