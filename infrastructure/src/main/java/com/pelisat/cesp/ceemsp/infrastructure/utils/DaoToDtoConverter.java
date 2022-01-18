package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

@Component
public class DaoToDtoConverter {
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(DaoToDtoConverter.class);

    @Autowired
    public DaoToDtoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void setDaoToDtoConverterSetup() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper.getConfiguration().setAmbiguityIgnored(true);
    }

    public UsuarioDto convertDaoToDtoUser(Usuario usuario) {
        if(usuario == null) {
            logger.warn("The user dao is coming as null");
            throw new InvalidDataException();
        }

        return modelMapper.map(usuario, UsuarioDto.class);
    }

    public CanRazaDto convertDaoToDtoCanRaza(CanRaza canRaza) {
        if(canRaza == null) {
            logger.warn("La raza a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(canRaza, CanRazaDto.class);
    }

    public CanTipoAdiestramientoDto convertDaoToDtoCanTipoAdiestramiento(CanTipoAdiestramiento canTipoAdiestramiento) {
        if(canTipoAdiestramiento == null) {
            logger.warn("El tipo de adiestramiento a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(canTipoAdiestramiento, CanTipoAdiestramientoDto.class);
    }

    public ModalidadDto convertDaoToDtoModalidad(Modalidad modalidad) {
        if(modalidad == null) {
            logger.warn("La modalidad a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(modalidad, ModalidadDto.class);
    }

    public SubmodalidadDto convertDaoToDtoSubmodalidad(Submodalidad submodalidad) {
        if(submodalidad == null) {
            logger.warn("La submodalidad a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(submodalidad, SubmodalidadDto.class);
    }

    public ArmaClaseDto convertDaoToDtoArmaClase(ArmaClase armaClase) {
        if(armaClase == null) {
            logger.warn("La clase de arma a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(armaClase, ArmaClaseDto.class);
    }

    public ArmaMarcaDto convertDaoToDtoArmaMarca(ArmaMarca armaMarca) {
        if(armaMarca == null) {
            logger.warn("La marca del arma a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(armaMarca, ArmaMarcaDto.class);
    }

    public ArmaTipoDto convertDaoToDtoArmaTipo(ArmaTipo armaTipo) {
        if(armaTipo == null) {
            logger.warn("El tipo del arma a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(armaTipo, ArmaTipoDto.class);
    }

    public VehiculoMarcaDto convertDaoToDtoVehiculoMarca(VehiculoMarca vehiculoMarca) {
        if(vehiculoMarca == null) {
            logger.warn("La marca del vehiculo a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(vehiculoMarca, VehiculoMarcaDto.class);
    }

    public VehiculoSubmarcaDto convertDaoToDtoVehiculoSubmarca(VehiculoSubmarca vehiculoSubmarca) {
        if(vehiculoSubmarca == null) {
            logger.warn("La submarca del vehiculo a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(vehiculoSubmarca, VehiculoSubmarcaDto.class);
    }

    public VehiculoTipoDto convertDaoToDtoVehiculoTipo(VehiculoTipo vehiculoTipo) {
        if(vehiculoTipo == null) {
            logger.warn("El tipo del vehiculo a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(vehiculoTipo, VehiculoTipoDto.class);
    }

    public EmpresaDto convertDaoToDtoEmpresa(Empresa empresa) {
        if(empresa == null) {
            logger.warn("La empresa a convertir viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresa, EmpresaDto.class);
    }

    public EmpresaModalidadDto convertDaoToDtoEmpresaModalidad(EmpresaModalidad empresaModalidad) {
        if(empresaModalidad == null) {
            logger.warn("La modalidad de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        /*EmpresaModalidadDto empresaModalidadDto = new EmpresaModalidadDto();
        empresaModalidadDto.setFechaInicio(empresaModalidad.getFechaInicio().format(DateTimeFormatter.ofPattern("dd LLLL yyyy")));*/

        return modelMapper.map(empresaModalidad, EmpresaModalidadDto.class);
    }

    public EmpresaDomicilioDto convertDaoToDtoEmpresaDomicilio(EmpresaDomicilio empresaDomicilio) {
        if(empresaDomicilio == null) {
            logger.warn("El domicilio de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaDomicilio, EmpresaDomicilioDto.class);
    }

    public EmpresaEscrituraDto convertDaoToDtoEmpresaEscritura(EmpresaEscritura empresaEscritura) {
        if(empresaEscritura == null) {
            logger.warn("La escritura de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaEscritura, EmpresaEscrituraDto.class);
    }

    public EmpresaEscrituraApoderadoDto convertDaoToDtoEmpresaEscrituraApoderado(EmpresaEscrituraApoderado empresaEscrituraApoderado) {
        if(empresaEscrituraApoderado == null) {
            logger.warn("El apoderado de la escritura de la empresa viene nula o vacía");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaEscrituraApoderado, EmpresaEscrituraApoderadoDto.class);
    }

    public EmpresaEscrituraSocioDto convertDaoToDtoEmpresaEscrituraSocio(EmpresaEscrituraSocio empresaEscrituraSocio) {
        if(empresaEscrituraSocio == null) {
            logger.warn("El socio de la escritura de la empresa viene nula o vacía");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaEscrituraSocio, EmpresaEscrituraSocioDto.class);
    }

    public EmpresaEscrituraRepresentanteDto convertDaoToDtoEmpresaEscrituraRepresentante(EmpresaEscrituraRepresentante empresaEscrituraRepresentante) {
        if(empresaEscrituraRepresentante == null) {
            logger.warn("El representante de la escritura de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaEscrituraRepresentante, EmpresaEscrituraRepresentanteDto.class);
    }

    public EmpresaEscrituraConsejoDto convertDaoToDtoEmpresaEscrituraConsejo(EmpresaEscrituraConsejo empresaEscrituraConsejo) {
        if(empresaEscrituraConsejo == null) {
            logger.warn("El consejo de la escritura de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaEscrituraConsejo, EmpresaEscrituraConsejoDto.class);
    }

    public EmpresaLicenciaColectivaDto convertDaoToDtoEmpresaLicenciaColectiva(EmpresaLicenciaColectiva empresaLicenciaColectiva) {
        if(empresaLicenciaColectiva == null) {
            logger.warn("La licencia colectiva de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaLicenciaColectiva, EmpresaLicenciaColectivaDto.class);
    }

    public ClienteDto convertDaoToDtoCliente(Cliente cliente) {
        if(cliente == null) {
            logger.warn("El cliente de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(cliente, ClienteDto.class);
    }

    public TipoInfraestructuraDto convertDaoToDtoTipoInfraestructura(TipoInfraestructura tipoInfraestructura) {
        if(tipoInfraestructura == null) {
            logger.warn("El tipo de infraestructura viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(tipoInfraestructura, TipoInfraestructuraDto.class);
    }

    public VehiculoDto convertDaoToDtoVehiculo(Vehiculo vehiculo) {
        if(vehiculo == null) {
            logger.warn("El vehiculo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(vehiculo, VehiculoDto.class);
    }

    public VehiculoUsoDto convertDaoToDtoVehiculoUso(VehiculoUso vehiculoUso) {
        if(vehiculoUso == null) {
            logger.warn("El uso del vehiculo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(vehiculoUso, VehiculoUsoDto.class);
    }

    public PersonalPuestoDeTrabajoDto convertDaoToDtoPersonalPuestoDeTrabajo(PersonalPuesto personalPuesto) {
        if(personalPuesto == null) {
            logger.warn("El puesto de trabajo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(personalPuesto, PersonalPuestoDeTrabajoDto.class);
    }

    public PersonalSubpuestoDeTrabajoDto convertDaoToDtoPersonalSubpuestoDeTrabajo(PersonalSubpuesto personalSubpuesto) {
        if(personalSubpuesto == null) {
            logger.warn("El subpuesto de trabajo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(personalSubpuesto, PersonalSubpuestoDeTrabajoDto.class);
    }

    public PersonalNacionalidadDto convertDaoToDtoPersonalNacionalidad(PersonalNacionalidad personalNacionalidad) {
        if(personalNacionalidad == null) {
            logger.warn("La nacionalidad viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(personalNacionalidad, PersonalNacionalidadDto.class);
    }

    public ClienteDomicilioDto convertDaoToDtoClienteDomicilio(ClienteDomicilio clienteDomicilio) {
        if(clienteDomicilio == null) {
            logger.warn("El domicilio del cliente viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(clienteDomicilio, ClienteDomicilioDto.class);
    }

    public PersonaDto convertDaoToDtoPersona(Personal personal) {
        if(personal == null) {
            logger.warn("El personal viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(personal, PersonaDto.class);
    }

    public CanDto convertDaoToDtoCan(Can can) {
        if(can == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(can, CanDto.class);
    }

    public ArmaDto convertDaoToDtoArma(Arma arma) {
        if(arma == null) {
            logger.warn("El arma viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(arma, ArmaDto.class);
    }

    public ComunicadoGeneralDto convertDaoToDtoComunicadoGeneral(ComunicadoGeneral comunicadoGeneral) {
        if (comunicadoGeneral == null) {
            logger.warn("El comunicado general viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(comunicadoGeneral, ComunicadoGeneralDto.class);
    }

    public VehiculoColorDto convertDaoToDtoVehiculoColor(VehiculoColor vehiculoColor) {
        if (vehiculoColor == null) {
            logger.warn("El color del vehiculo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(vehiculoColor, VehiculoColorDto.class);
    }

    public PersonalCertificacionDto convertDaoToDtoPersonalCertificacion(PersonalCertificacion personalCertificacion) {
        if(personalCertificacion == null) {
            logger.warn("La certificacion del personal viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(personalCertificacion, PersonalCertificacionDto.class);
    }

    public IncidenciaDto convertDaoToDtoIncidencia(Incidencia incidencia) {
        if(incidencia == null) {
            logger.warn("La incidencia viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(incidencia, IncidenciaDto.class);
    }

    public CanAdiestramientoDto convertDaoToDtoCanAdiestramiento(CanAdiestramiento canAdiestramiento) {
        if(canAdiestramiento == null) {
            logger.warn("El can adiestramiento viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(canAdiestramiento, CanAdiestramientoDto.class);
    }

    public CanCartillaVacunacionDto convertDaoToDtoCanCartillaVacunacion(CanCartillaVacunacion canCartillaVacunacion) {
        if(canCartillaVacunacion == null) {
            logger.warn("El can adiestramiento viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(canCartillaVacunacion, CanCartillaVacunacionDto.class);
    }

    public CanConstanciaSaludDto convertDaoToDtoCanConstanciaSalud(CanConstanciaSalud canConstanciaSalud) {
        if(canConstanciaSalud == null) {
            logger.warn("La constancia de salud del can viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(canConstanciaSalud, CanConstanciaSaludDto.class);
    }

    public EquipoDto convertDaoToDtoEquipo(Equipo equipo) {
        if(equipo == null) {
            logger.warn("El equipo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        return modelMapper.map(equipo, EquipoDto.class);
    }

    public EmpresaFormaEjecucionDto convertDaoToDtoEmpresaFormaEjecucion(EmpresaFormaEjecucion empresaFormaEjecucion) {
        if(empresaFormaEjecucion == null) {
            logger.warn("La forma de ejecucion viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaFormaEjecucion, EmpresaFormaEjecucionDto.class);
    }

    public UniformeDto convertDaoToDtoUniforme(Uniforme uniforme) {
        if(uniforme == null) {
            logger.warn("El uniforme viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(uniforme, UniformeDto.class);
    }
}
