package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.dto.metadata.CanFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.type.CanGeneroEnum;
import com.pelisat.cesp.ceemsp.database.type.CanOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    private PersonaDto elementoAsignado;
    private ClienteDomicilioDto clienteDomicilio;
    private String motivos;
    private boolean eliminado;
    private String fechaCreacion;
    private String fechaActualizacion;

    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;
    private boolean fotografiaCapturada;
    private boolean adiestramientoCapturado;
    private boolean vacunacionCapturada;
    private boolean constanciaCapturada;

    private EmpresaDto empresa;

    private List<CanAdiestramientoDto> adiestramientos;
    private List<CanCartillaVacunacionDto> cartillasVacunacion;
    private List<CanConstanciaSaludDto> constanciasSalud;
    private List<CanFotografiaMetadata> fotografias;
    private Boolean eliminadoIncidencia;
    private String fechaCreacionIncidencia;
    private String fechaEliminacionIncidencia;
}
