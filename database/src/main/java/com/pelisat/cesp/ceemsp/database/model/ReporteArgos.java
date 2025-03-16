package com.pelisat.cesp.ceemsp.database.model;

import com.pelisat.cesp.ceemsp.database.type.ReporteArgosStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoReporteArgosEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "REPORTES_ARGOS")
public class ReporteArgos extends CommonModel {
    @Column(name = "TIPO")
    @Enumerated(EnumType.STRING)
    private TipoReporteArgosEnum tipo;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ReporteArgosStatusEnum status;
    @Column(name = "RUTA_ARCHIVO")
    private String rutaArchivo;
    @Column(name = "RAZON")
    private String razon;
    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;
}
