package com.pelisat.cesp.ceemsp.database.dto;

import com.pelisat.cesp.ceemsp.database.dto.metadata.PersonalFotografiaMetadata;
import com.pelisat.cesp.ceemsp.database.type.CuipStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.EstadoCivilEnum;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoSangreEnum;
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
    private String rutaVolanteCuip;
    private ModalidadDto modalidad;
    private String rfc;
    private boolean eliminado;
    private String fechaCreacion;

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
}
