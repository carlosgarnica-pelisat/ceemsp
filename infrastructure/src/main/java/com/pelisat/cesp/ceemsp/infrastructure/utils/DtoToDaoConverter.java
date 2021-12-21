package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
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

    public EmpresaEscrituraConsejo convertDtoToDaoEmpresaEscrituraConsejo(EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto) {
        if(empresaEscrituraConsejoDto == null) {
            logger.warn("El consejo de la escritura de la empresa viene como vacia o nula");
            throw new InvalidDataException();
        }

        EmpresaEscrituraConsejo empresaEscrituraConsejo = modelMapper.map(empresaEscrituraConsejoDto, EmpresaEscrituraConsejo.class);
        if(StringUtils.isBlank(empresaEscrituraConsejo.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            empresaEscrituraConsejo.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return empresaEscrituraConsejo;
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
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(ClienteDomicilioDto.class, ClienteDomicilio.class).addMappings(mapper -> mapper.skip(ClienteDomicilio::setTipoInfraestructura));

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

    public Vehiculo convertDtoToDaoVehiculo(VehiculoDto vehiculoDto) {
        if(vehiculoDto == null) {
            logger.warn("el vehiculo viene como nulo o vacio");
            throw new InvalidDataException();
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(VehiculoDto.class, Vehiculo.class)
                .addMappings(mapper -> mapper.skip(Vehiculo::setMarca))
                .addMappings(mapper -> mapper.skip(Vehiculo::setSubmarca))
                .addMappings(mapper -> mapper.skip(Vehiculo::setTipo));

        Vehiculo vehiculo = modelMapper.map(vehiculoDto, Vehiculo.class);
        if(StringUtils.isBlank(vehiculo.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculo.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculo;
    }

    public Personal convertDtoToDaoPersonal(PersonaDto personaDto) {
        if(personaDto == null) {
            logger.warn("El personal viene como nulo o vacio");
            throw new InvalidDataException();
        }

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper
                .typeMap(PersonaDto.class, Personal.class)
                .addMappings(mapper -> mapper.skip(Personal::setModalidad))
                .addMappings(mapper -> mapper.skip(Personal::setPuesto))
                .addMappings(mapper -> mapper.skip(Personal::setSubpuesto))
                .addMappings(mapper -> mapper.skip(Personal::setNacionalidad))
                .addMappings(mapper -> mapper.skip(Personal::setDomicilioAsignado));

        Personal personal = modelMapper.map(personaDto, Personal.class);
        if(StringUtils.isBlank(personal.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            personal.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return personal;
    }

    public Can convertDtoToDaoCan(CanDto canDto) {
        if(canDto == null) {
            logger.warn("El can viene como nulo o vacio");
            throw new InvalidDataException();
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(CanDto.class, Can.class)
                .addMappings(mapper -> mapper.skip(Can::setClienteAsignado))
                .addMappings(mapper -> mapper.skip(Can::setDomicilioAsignado))
                .addMappings(mapper -> mapper.skip(Can::setDomicilioClienteAsignado))
                .addMappings(mapper -> mapper.skip(Can::setRaza));

        Can can = modelMapper.map(canDto, Can.class);
        if(StringUtils.isBlank(can.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            can.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return can;
    }

    public Arma convertDtoToDaoArma(ArmaDto armaDto) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(ArmaDto.class, Arma.class)
                .addMappings(mapper -> mapper.skip(Arma::setBunker))
                .addMappings(mapper -> mapper.skip(Arma::setMarca))
                .addMappings(mapper -> mapper.skip(Arma::setClase))
                .addMappings(mapper -> mapper.skip(Arma::setLicenciaColectiva));

        Arma arma = modelMapper.map(armaDto, Arma.class);
        if(StringUtils.isBlank(armaDto.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            arma.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return arma;
    }

    public ComunicadoGeneral convertDtoToDaoConverter(ComunicadoGeneralDto comunicadoGeneralDto) {
        if(comunicadoGeneralDto == null) {
            logger.warn("El comunicado a convertir a dao viene como nulo o vacio");
            throw new InvalidDataException();
        }

        ComunicadoGeneral comunicadoGeneral = modelMapper.map(comunicadoGeneralDto, ComunicadoGeneral.class);
        if(StringUtils.isBlank(comunicadoGeneral.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            comunicadoGeneral.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return comunicadoGeneral;
    }

    public VehiculoColor convertDtoToDaoColor(VehiculoColorDto vehiculoColorDto) {
        if(vehiculoColorDto == null) {
            logger.warn("El color de vehiculo a convertir a dao viene como nulo o vacio");
            throw new InvalidDataException();
        }

        VehiculoColor vehiculoColor = modelMapper.map(vehiculoColorDto, VehiculoColor.class);
        if(StringUtils.isBlank(vehiculoColor.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            vehiculoColor.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return vehiculoColor;
    }

    public PersonalCertificacion convertDtoToDaoPersonalCertificacion(PersonalCertificacionDto personalCertificacionDto) {
        if(personalCertificacionDto == null) {
            logger.warn("La certificacion del personal a convertir viene como nulo o vacio");
            throw new InvalidDataException();
        }

        PersonalCertificacion personalCertificacion = modelMapper.map(personalCertificacionDto, PersonalCertificacion.class);

        if(StringUtils.isBlank(personalCertificacion.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            personalCertificacion.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return personalCertificacion;
    }

    public CanAdiestramiento convertDtoToDaoAdiestramiento(CanAdiestramientoDto canAdiestramientoDto) {
        if(canAdiestramientoDto == null) {
            logger.warn("El adiestramiento del can a convertir viene como nulo o vacio");
            throw new InvalidDataException();
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(CanAdiestramientoDto.class, CanAdiestramiento.class)
                .addMappings(mapper -> mapper.skip(CanAdiestramiento::setTipoAdiestramiento));

        CanAdiestramiento canAdiestramiento = modelMapper.map(canAdiestramientoDto, CanAdiestramiento.class);

        if(StringUtils.isBlank(canAdiestramiento.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            canAdiestramiento.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return canAdiestramiento;
    }

    public CanCartillaVacunacion convertDtoToDaoCanCartillaVacunacion(CanCartillaVacunacionDto canCartillaVacunacionDto) {
        if(canCartillaVacunacionDto == null) {
            logger.warn("La cartilla de vaunacion del can a convertir viene como nulo o vacio");
            throw new InvalidDataException();
        }

        CanCartillaVacunacion canCartillaVacunacion = modelMapper.map(canCartillaVacunacionDto, CanCartillaVacunacion.class);

        if(StringUtils.isBlank(canCartillaVacunacion.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            canCartillaVacunacion.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return canCartillaVacunacion;
    }

    public CanConstanciaSalud convertDtoToDaoCanConstanciaSalud(CanConstanciaSaludDto canConstanciaSaludDto) {
        if(canConstanciaSaludDto == null) {
            logger.warn("La constancia de salud viene como nula o vacia");
            throw new InvalidDataException();
        }

        CanConstanciaSalud canConstanciaSalud = modelMapper.map(canConstanciaSaludDto, CanConstanciaSalud.class);

        if(StringUtils.isBlank(canConstanciaSalud.getUuid())) {
            logger.info("El uuid viene como nulo. Generando uno nuevo");
            canConstanciaSalud.setUuid(RandomStringUtils.randomAlphanumeric(MAXIMUM_UUID_CHARS));
        }

        return canConstanciaSalud;
    }
}
