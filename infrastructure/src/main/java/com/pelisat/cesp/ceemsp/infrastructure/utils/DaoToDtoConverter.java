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

    public EmpresaEscrituraRepresentanteDto convertDaoToDtoEmpresaEscrituraRepresentante(EmpresaEscrituraRepresentante empresaEscrituraRepresentante) {
        if(empresaEscrituraRepresentante == null) {
            logger.warn("El representante de la escritura de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaEscrituraRepresentante, EmpresaEscrituraRepresentanteDto.class);
    }

    public EmpresaLicenciaColectivaDto convertDaoToDtoEmpresaLicenciaColectiva(EmpresaLicenciaColectiva empresaLicenciaColectiva) {
        if(empresaLicenciaColectiva == null) {
            logger.warn("La licencia colectiva de la empresa viene como nula o vacia");
            throw new InvalidDataException();
        }

        return modelMapper.map(empresaLicenciaColectiva, EmpresaLicenciaColectivaDto.class);
    }
}
