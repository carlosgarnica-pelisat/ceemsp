package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DtoToDaoConverter {
    private final ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(DtoToDaoConverter.class);

    private static final int MAXIMUM_UUID_CHARS = 12;

    @Autowired
    public DtoToDaoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Usuario convertDtoToDaoUser(Usuario userDto) {
        if(userDto == null) {
            logger.warn("The userDto to be converted is coming as null");
            throw new InvalidDataException();
        }

        Usuario usuario = modelMapper.map(userDto, Usuario.class);
        if(StringUtils.isBlank(usuario.getUuid())) {
            logger.info("Uuid is coming as null. Generating a new one");
            usuario.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return usuario;
    }

    public CanRaza convertDtoToDaoCanRaza(CanRazaDto canRazaDto) {
        if(canRazaDto == null) {
            logger.warn("La raza viene como vacia o invalida");
            throw new InvalidDataException();
        }

        CanRaza canRaza = modelMapper.map(canRazaDto, CanRaza.class);
        if(StringUtils.isBlank(canRaza.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            canRaza.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return canRaza;
    }

    public CanTipoAdiestramiento convertDtoToDaoCanTipoAdiestramiento(CanTipoAdiestramientoDto canTipoAdiestramientoDto) {
        if(canTipoAdiestramientoDto == null) {
            logger.warn("El tipo de adiestramiento viene como vacio o nulo");
            throw new InvalidDataException();
        }

        CanTipoAdiestramiento canTipoAdiestramiento = modelMapper.map(canTipoAdiestramientoDto, CanTipoAdiestramiento.class);
        if(StringUtils.isBlank(canTipoAdiestramiento.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            canTipoAdiestramiento.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return canTipoAdiestramiento;
    }

    public Modalidad convertDtoToDaoModalidad(ModalidadDto modalidadDto) {
        if(modalidadDto == null) {
            logger.warn("La modalidad viene como vacia o nula");
            throw new InvalidDataException();
        }

        Modalidad modalidad = modelMapper.map(modalidadDto, Modalidad.class);
        if(StringUtils.isBlank(modalidad.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            modalidad.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return modalidad;
    }

    public ArmaClase convertDtoToDaoArmaClase(ArmaClaseDto armaClaseDto) {
        if(armaClaseDto == null) {
            logger.warn("La clase del arma viene como vacio o nulo");
            throw new InvalidDataException();
        }

        ArmaClase armaClase = modelMapper.map(armaClaseDto, ArmaClase.class);
        if(StringUtils.isBlank(armaClase.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            armaClase.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return armaClase;
    }

    public ArmaMarca convertDtoToDaoArmaMarca(ArmaMarcaDto armaMarcaDto) {
        if(armaMarcaDto == null) {
            logger.warn("La marca del arma viene como vacio o nulo");
            throw new InvalidDataException();
        }

        ArmaMarca armaMarca = modelMapper.map(armaMarcaDto, ArmaMarca.class);
        if(StringUtils.isBlank(armaMarca.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            armaMarca.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return armaMarca;
    }

    public ArmaTipo convertDtoToDaoArmaTipo(ArmaTipoDto armaTipoDto) {
        if(armaTipoDto == null) {
            logger.warn("El tipo del arma viene como vacio o nulo");
            throw new InvalidDataException();
        }

        ArmaTipo armaTipo = modelMapper.map(armaTipoDto, ArmaTipo.class);
        if(StringUtils.isBlank(armaTipo.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            armaTipo.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return armaTipo;
    }

    public VehiculoMarca convertDtoToDaoVehiculoMarca(VehiculoMarcaDto vehiculoMarcaDto) {
        if(vehiculoMarcaDto == null) {
            logger.warn("La marca del vehiculo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        VehiculoMarca vehiculoMarca = modelMapper.map(vehiculoMarcaDto, VehiculoMarca.class);
        if(StringUtils.isBlank(vehiculoMarca.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoMarca.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoMarca;
    }

    public VehiculoSubmarca convertDtoToDaoVehiculoSubmarca(VehiculoSubmarcaDto vehiculoSubmarcaDto) {
        if(vehiculoSubmarcaDto == null) {
            logger.warn("La submarca del vehiculo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        VehiculoSubmarca vehiculoSubmarca = modelMapper.map(vehiculoSubmarcaDto, VehiculoSubmarca.class);
        if(StringUtils.isBlank(vehiculoSubmarca.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoSubmarca.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoSubmarca;
    }

    public VehiculoTipo convertDtoToDaoVehiculoTipo(VehiculoTipoDto vehiculoTipoDto) {
        if(vehiculoTipoDto == null) {
            logger.warn("El tipo de vehiculo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        VehiculoTipo vehiculoTipo = modelMapper.map(vehiculoTipoDto, VehiculoTipo.class);
        if(StringUtils.isBlank(vehiculoTipo.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoTipo.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoTipo;
    }

    public Empresa convertDtoToDaoEmpresa(EmpresaDto empresaDto) {
        if(empresaDto == null) {
            logger.warn("La empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        Empresa empresa = modelMapper.map(empresaDto, Empresa.class);
        if(StringUtils.isBlank(empresa.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresa.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresa;
    }

    public EmpresaDomicilio convertDtoToDaoEmpresaDomicilio(EmpresaDomicilioDto empresaDomicilioDto) {
        if(empresaDomicilioDto == null) {
            logger.warn("El domicilio de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        EmpresaDomicilio empresaDomicilio = modelMapper.map(empresaDomicilioDto, EmpresaDomicilio.class);
        if(StringUtils.isBlank(empresaDomicilio.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresaDomicilio.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresaDomicilio;
    }

    public EmpresaEscritura convertDtoToDaoEmpresaEscritura(EmpresaEscrituraDto empresaEscrituraDto) {
        if(empresaEscrituraDto == null) {
            logger.warn("La escritura de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        EmpresaEscritura empresaEscritura = modelMapper.map(empresaEscrituraDto, EmpresaEscritura.class);
        if(StringUtils.isBlank(empresaEscritura.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresaEscritura.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresaEscritura;
    }

    public EmpresaEscrituraApoderado convertDtoToDaoEmpresaEscrituraApoderado(EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto) {
        if(empresaEscrituraApoderadoDto == null) {
            logger.warn("El apoderado de la escritura de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        EmpresaEscrituraApoderado empresaEscrituraApoderado = modelMapper.map(empresaEscrituraApoderadoDto, EmpresaEscrituraApoderado.class);
        if(StringUtils.isBlank(empresaEscrituraApoderado.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresaEscrituraApoderado.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresaEscrituraApoderado;
    }

    public EmpresaEscrituraSocio convertDtoToDaoEmpresaEscrituraSocio(EmpresaEscrituraSocioDto empresaEscrituraSocioDto) {
        if(empresaEscrituraSocioDto == null) {
            logger.warn("El socio de la escritura de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        EmpresaEscrituraSocio empresaEscrituraSocio = modelMapper.map(empresaEscrituraSocioDto, EmpresaEscrituraSocio.class);
        if(StringUtils.isBlank(empresaEscrituraSocio.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresaEscrituraSocio.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresaEscrituraSocio;
    }

    public EmpresaEscrituraRepresentante convertDtoToDaoEmpresaRepresentante(EmpresaEscrituraRepresentanteDto empresaEscrituraRepresentanteDto) {
        if(empresaEscrituraRepresentanteDto == null) {
            logger.warn("El representante de la escritura de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        EmpresaEscrituraRepresentante empresaEscrituraRepresentante = modelMapper.map(empresaEscrituraRepresentanteDto, EmpresaEscrituraRepresentante.class);
        if(StringUtils.isBlank(empresaEscrituraRepresentante.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresaEscrituraRepresentante.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresaEscrituraRepresentante;
    }

    public EmpresaLicenciaColectiva convertDtoToDaoEmpresaLicenciaColectiva(EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto) {
        if(empresaLicenciaColectivaDto == null) {
            logger.warn("La licencia coelctiva de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        EmpresaLicenciaColectiva empresaLicenciaColectiva = modelMapper.map(empresaLicenciaColectivaDto, EmpresaLicenciaColectiva.class);
        if(StringUtils.isBlank(empresaLicenciaColectiva.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresaLicenciaColectiva.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresaLicenciaColectiva;
    }

    public Cliente convertDtoToDaoCliente(ClienteDto clienteDto) {
        if(clienteDto == null) {
            logger.warn("El cliente de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        Cliente cliente = modelMapper.map(clienteDto, Cliente.class);
        if(StringUtils.isBlank(cliente.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            cliente.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return cliente;
    }

    public TipoInfraestructura convertDtoToDaoTipoInfraestructura(TipoInfraestructuraDto tipoInfraestructuraDto) {
        if(tipoInfraestructuraDto == null) {
            logger.warn("El tipo de infraestructura viene como vacia o nula");
            throw new InvalidDataException();
        }

        TipoInfraestructura tipoInfraestructura = modelMapper.map(tipoInfraestructuraDto, TipoInfraestructura.class);
        if(StringUtils.isBlank(tipoInfraestructura.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            tipoInfraestructura.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return tipoInfraestructura;
    }

    public VehiculoUso convertDtoToDaoVehiculoUso(VehiculoUsoDto vehiculoUsoDto) {
        if(vehiculoUsoDto == null) {
            logger.warn("El uso del vehiculo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        VehiculoUso vehiculoUso = modelMapper.map(vehiculoUsoDto, VehiculoUso.class);
        if(StringUtils.isBlank(vehiculoUso.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoUso.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoUso;
    }

    public PersonalPuesto convertDtoToDaoPuestoTrabajo(PersonalPuestoDeTrabajoDto personalPuestoDeTrabajoDto) {
        if(personalPuestoDeTrabajoDto == null) {
            logger.warn("El puesto de trabajo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        PersonalPuesto personalPuesto = modelMapper.map(personalPuestoDeTrabajoDto, PersonalPuesto.class);
        if(StringUtils.isBlank(personalPuesto.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            personalPuesto.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return personalPuesto;
    }

    public PersonalSubpuesto convertDtoToDaoSubpuestoTrabajo(PersonalSubpuestoDeTrabajoDto personalSubpuestoDeTrabajoDto) {
        if(personalSubpuestoDeTrabajoDto == null) {
            logger.warn("El subpuesto de trabajo viene como vacio o nulo");
            throw new InvalidDataException();
        }

        PersonalSubpuesto personalSubpuesto = modelMapper.map(personalSubpuestoDeTrabajoDto, PersonalSubpuesto.class);
        if(StringUtils.isBlank(personalSubpuesto.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            personalSubpuesto.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return personalSubpuesto;
    }

    public PersonalNacionalidad convertDtoToDaoNacionalidad(PersonalNacionalidadDto personalNacionalidadDto) {
        if(personalNacionalidadDto == null) {
            logger.warn("La nacionalidad viene como vacio o nulo");
            throw new InvalidDataException();
        }

        PersonalNacionalidad personalNacionalidad = modelMapper.map(personalNacionalidadDto, PersonalNacionalidad.class);
        if(StringUtils.isBlank(personalNacionalidad.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            personalNacionalidad.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return personalNacionalidad;
    }

    public ClienteDomicilio convertDtoToDaoClienteDomicilio(ClienteDomicilioDto clienteDomicilioDto) {
        if(clienteDomicilioDto == null) {
            logger.warn("El domicilio del cliente viene como vacio o nulo");
            throw new InvalidDataException();
        }

        ClienteDomicilio clienteDomicilio = modelMapper.map(clienteDomicilioDto, ClienteDomicilio.class);
        if(StringUtils.isBlank(clienteDomicilio.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            clienteDomicilio.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return clienteDomicilio;
    }

}
