package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.CanGeneroEnum;
import com.pelisat.cesp.ceemsp.database.type.CanOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "CANES")
@Getter
@Setter
public class Can extends CommonModel {
    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "EMPRESA", nullable = false)
    private Integer empresa;

    @Column(name = "GENERO", nullable = false)
    @Enumerated(EnumType.STRING)
    private CanGeneroEnum genero;

    @Column(name = "RAZA", nullable = false)
    private Integer raza;

    @Column(name = "RAZA_OTRO")
    private String razaOtro;

    @Column(name = "DOMICILIO_ASIGNADO", nullable = false)
    private Integer domicilioAsignado;

    @Column(name = "FECHA_INGRESO", nullable = false)
    private LocalDate fechaIngreso;

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

    @Column(name = "RAZON_SOCIAL")
    private String razonSocial;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "ELEMENTO_ASIGNADO")
    private Integer elementoAsignado;

    @Column(name = "CLIENTE_ASIGNADO")
    private Integer clienteAsignado;

    @Column(name = "DOMICILIO_CLIENTE_ASIGNADO")
    private Integer domicilioClienteAsignado;

    @Column(name = "MOTIVOS")
    private String motivos;

    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;

    @Column(name = "OBSERVACIONES_BAJA")
    private String observacionesBaja;

    @Column(name = "DOCUMENTO_FUNDATORIO_BAJA")
    private String documentoFundatorioBaja;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @Column(name = "FOTOGRAFIA_CAPTURADA")
    private boolean fotografiaCapturada;

    @Column(name = "ADIESTRAMIENTO_CAPTURADO")
    private boolean adiestramientoCapturado;

    @Column(name = "VACUNACION_CAPTURADA")
    private boolean vacunacionCapturada;

    @Column(name = "CONSTANCIA_CAPTURADA")
    private boolean constanciaCapturada;
}
