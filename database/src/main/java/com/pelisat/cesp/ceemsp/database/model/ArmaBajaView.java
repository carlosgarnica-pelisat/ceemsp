package com.pelisat.cesp.ceemsp.database.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "armas_bajas_view")
@Immutable
@Getter
@Setter
public class ArmaBajaView {
    @Column(name = "id")
    @Id
    private int id;

    @Column(name = "licencia_colectiva")
    private int licenciaColectiva;

    @Column(name = "empresa")
    private int empresa;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @Column(name = "modalidad")
    private LocalDateTime modalidad;
}
