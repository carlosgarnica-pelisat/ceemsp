package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.dto.metadata.CanFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.type.CanGeneroEnum;
import com.pelisat.cesp.ceemsp.database.type.CanOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CanDto {
    private int id;
    private String uuid;
    private String nombre;
    private CanGeneroEnum genero;
    private CanRazaDto raza;
    private String razaOtro;
    private EmpresaDomicilioDto domicilioAsignado;
    private String fechaIngreso;
    private int edad;
    private BigDecimal peso;
    private String descripcion;
    private CanOrigenEnum origen;
    private CanStatusEnum status;
    private boolean chip;
    private boolean tatuaje;
    private String razonSocial;
    private String fechaInicio;
    private String fechaFin;
    private ClienteDto clienteAsignado;
    private ClienteDomicilioDto clienteDomicilio;
    private String motivos;
    private boolean eliminado;
    private String fechaCreacion;

    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;

    private List<CanAdiestramientoDto> adiestramientos;
    private List<CanCartillaVacunacionDto> cartillasVacunacion;
    private List<CanConstanciaSaludDto> constanciasSalud;
    private List<CanFotografiaMetadata> fotografias;
}
