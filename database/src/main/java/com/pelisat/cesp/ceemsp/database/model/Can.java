package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.CanGeneroEnum;
import com.pelisat.cesp.ceemsp.database.type.CanOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "CANES")
@Getter
@Setter
public class Can extends CommonModel {
    @Column(name = "EMPRESA", nullable = false)
    private Integer empresa;

    @Column(name = "GENERO", nullable = false)
    @Enumerated(EnumType.STRING)
    private CanGeneroEnum genero;

    // TODO: Cambiar a relationships de JPA
    @Column(name = "RAZA", nullable = false)
    private Integer raza;

    @Column(name = "DOMICILIO_ASIGNADO", nullable = false)
    private Integer domicilioAsignado;

    @Column(name = "FECHA_INGRESO", nullable = false)
    private Date fechaIngreso;

    @Column(name = "EDAD", nullable = false)
    private Integer edad;

    @Column(name = "PESO", nullable = false)
    private BigDecimal peso;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "ORIGEN", nullable = false)
    @Enumerated(EnumType.STRING)
    private CanOrigenEnum origen;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private CanStatusEnum status;

    @Column(name = "CHIP", nullable = false)
    private Boolean chip;

    @Column(name = "TATUAJE", nullable = false)
    private Boolean tatuaje;


}
