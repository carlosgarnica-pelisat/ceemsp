package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.type.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonaDto {
    private int id;
    private String uuid;
    private PersonalNacionalidadDto nacionalidad;
    private String curp;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private SexoEnum sexo;
    private String fechaNacimiento;
    private String fechaIngreso;
    private TipoSangreEnum tipoSangre;
    private EstadoCivilEnum estadoCivil;
    private String domicilio1;
    private String numeroExterior;
    private String numeroInterior;
    private String domicilio2;
    private String domicilio3;
    private String domicilio4;
    private String localidad;
    private String estado;
    private String pais;
    private String codigoPostal;
    private String telefono;
    private String correoElectronico;
    private PersonalPuestoDeTrabajoDto puestoDeTrabajo;
    private PersonalSubpuestoDeTrabajoDto subpuestoDeTrabajo;
    private String detallesPuesto;
    private EmpresaDomicilioDto domicilioAsignado;
    private CuipStatusEnum estatusCuip;
    private String cuip;
    private String numeroVolanteCuip;
    private String fechaVolanteCuip;
    private boolean archivoVolanteCuipCargado;
    private EmpresaModalidadDto modalidad;
    private String rfc;
    private boolean eliminado;
    private String fechaCreacion;
    private String fechaActualizacion;
    private FormaEjecucionEnum formaEjecucion;

    private CanDto can;
    private VehiculoDto vehiculo;
    private ArmaDto armaCorta;
    private ArmaDto armaLarga;
    private ClienteDto cliente;
    private ClienteDomicilioDto clienteDomicilio;

    private String motivoBaja;
    private String observacionesBaja;
    private String documentoFundatorioBaja;
    private String fechaBaja;

    private EstadoDto estadoCatalogo;
    private MunicipioDto municipioCatalogo;
    private LocalidadDto localidadCatalogo;
    private ColoniaDto coloniaCatalogo;
    private CalleDto calleCatalogo;

    private List<PersonalCertificacionDto> certificaciones;
    private List<PersonalFotografiaMetadata> fotografias;

    private boolean puestoTrabajoCapturado;
    private boolean cursosCapturados;
    private boolean fotografiaCapturada;
    private Boolean eliminadoIncidencia;
    private String fechaCreacionIncidencia;
    private String fechaEliminacionIncidencia;

    private EmpresaDto empresa;
}
