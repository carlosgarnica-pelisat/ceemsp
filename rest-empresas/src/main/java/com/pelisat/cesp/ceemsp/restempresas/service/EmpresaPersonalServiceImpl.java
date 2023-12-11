package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.*;
import com.pelisat.cesp.ceemsp.infrastructure.exception.*;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class EmpresaPersonalServiceImpl implements EmpresaPersonalService {
    private final DaoHelper<CommonModel> daoHelper;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final CatalogoService catalogoService;
    private final EmpresaPersonalFotografiasService personalFotografiaService;
    private final EmpresaPersonalCertificacionService personalCertificacionService;
    private final EmpresaModalidadService empresaModalidadService;
    private final CanRepository canRepository;
    private final ArmaRepository armaRepository;
    private final ArmaMarcaRepository armaMarcaRepository;
    private final ArmaClaseRepository armaClaseRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteDomicilioRepository clienteDomicilioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PersonalCanRepository personalCanRepository;
    private final PersonalVehiculoRepository personalVehiculoRepository;
    private final PersonalArmaRepository personalArmaRepository;
    private final ArchivosService archivosService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaPersonalService.class);
    private final PersonalPuestoRepository personalPuestoRepository;

    @Autowired
    public EmpresaPersonalServiceImpl(DaoHelper<CommonModel> daoHelper, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaPersonalFotografiasService empresaPersonalFotografiasService,
                              UsuarioService usuarioService, PersonaRepository personaRepository,
                              CatalogoService catalogoService, EmpresaDomicilioService empresaDomicilioService, EmpresaPersonalCertificacionService empresaPersonalCertificacionService,
                              EmpresaModalidadService empresaModalidadService, CanRepository canRepository, ArmaRepository armaRepository,
                              ArmaMarcaRepository armaMarcaRepository, ArmaClaseRepository armaClaseRepository, ClienteRepository clienteRepository,
                              ClienteDomicilioRepository clienteDomicilioRepository, VehiculoRepository vehiculoRepository,
                              PersonalCanRepository personalCanRepository, PersonalVehiculoRepository personalVehiculoRepository,
                              PersonalArmaRepository personalArmaRepository, ArchivosService archivosService,
                                      PersonalPuestoRepository personalPuestoRepository) {
        this.daoHelper = daoHelper;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.personaRepository = personaRepository;
        this.empresaDomicilioService = empresaDomicilioService;
        this.personalCertificacionService = empresaPersonalCertificacionService;
        this.personalFotografiaService = empresaPersonalFotografiasService;
        this.catalogoService = catalogoService;
        this.empresaModalidadService = empresaModalidadService;
        this.canRepository = canRepository;
        this.armaRepository = armaRepository;
        this.armaMarcaRepository = armaMarcaRepository;
        this.armaClaseRepository = armaClaseRepository;
        this.clienteRepository = clienteRepository;
        this.clienteDomicilioRepository = clienteDomicilioRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.personalCanRepository = personalCanRepository;
        this.personalVehiculoRepository = personalVehiculoRepository;
        this.personalArmaRepository = personalArmaRepository;
        this.archivosService = archivosService;
        this.personalPuestoRepository = personalPuestoRepository;
    }

    @Override
    public List<PersonaDto> obtenerTodos(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Personal> personal = personaRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return personal.stream().map(p -> {
            PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
            if(p.getPuesto() > 0)
                dto.setPuestoDeTrabajo(daoToDtoConverter.convertDaoToDtoPersonalPuestoDeTrabajo(personalPuestoRepository.getById(p.getPuesto())));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PersonaDto> obtenerSinAsignar(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Personal> personal = personaRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return personal.stream().map(daoToDtoConverter::convertDaoToDtoPersona).collect(Collectors.toList());
    }

    @Override
    public PersonaDto obtenerPorUuid(String personaUuid) {
        if(StringUtils.isBlank(personaUuid)) {
            logger.warn("El uuid de la empresa o de la persona vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo a la persona con el uuid [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        if(personal == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setCertificaciones(personalCertificacionService.obtenerCertificacionesPorPersona(personaUuid));
        personaDto.setNacionalidad(catalogoService.obtenerNacionalidadPorId(personal.getNacionalidad()));
        if(personal.getPuesto() > 0) {
            personaDto.setPuestoDeTrabajo(catalogoService.obtenerPuestoPorId(personal.getPuesto()));
        }
        if(personal.getSubpuesto() > 0) {
            personaDto.setSubpuestoDeTrabajo(catalogoService.obtenerSubpuestoPorId(personal.getSubpuesto()));
        }
        if(personal.getDomicilioAsignado() > 0) {
            personaDto.setDomicilioAsignado(empresaDomicilioService.obtenerPorId(personal.getDomicilioAsignado()));
        }
        personaDto.setFotografias(personalFotografiaService.mostrarPersonalFotografias(personaUuid));
        personaDto.setCalleCatalogo(catalogoService.obtenerCallePorId(personal.getCalleCatalogo()));
        personaDto.setColoniaCatalogo(catalogoService.obtenerColoniaPorId(personal.getColoniaCatalogo()));
        personaDto.setLocalidadCatalogo(catalogoService.obtenerLocalidadPorId(personal.getLocalidadCatalogo()));
        personaDto.setMunicipioCatalogo(catalogoService.obtenerMunicipioPorId(personal.getMunicipioCatalogo()));
        personaDto.setEstadoCatalogo(catalogoService.obtenerEstadoPorId(personal.getEstadoCatalogo()));

        if(personal.getModalidad() != null) {
            EmpresaModalidadDto empresaModalidadDto = empresaModalidadService.obtenerEmpresaModalidadPorId(personal.getModalidad());
            personaDto.setModalidad(empresaModalidadDto);
        }

        if(personal.getCan() != null) {
            Can can = canRepository.getOne(personal.getCan());
            personaDto.setCan(daoToDtoConverter.convertDaoToDtoCan(can));
        }

        if(personal.getArmaCorta() != null) {
            Arma arma = armaRepository.getOne(personal.getArmaCorta());
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setClase(daoToDtoConverter.convertDaoToDtoArmaClase(armaClaseRepository.getOne(arma.getClase())));
            armaDto.setMarca(daoToDtoConverter.convertDaoToDtoArmaMarca(armaMarcaRepository.getOne(arma.getMarca())));
            personaDto.setArmaCorta(armaDto);
        }

        if(personal.getArmaLarga() != null) {
            Arma arma = armaRepository.getOne(personal.getArmaLarga());
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setClase(daoToDtoConverter.convertDaoToDtoArmaClase(armaClaseRepository.getOne(arma.getClase())));
            armaDto.setMarca(daoToDtoConverter.convertDaoToDtoArmaMarca(armaMarcaRepository.getOne(arma.getMarca())));
            personaDto.setArmaLarga(armaDto);
        }

        if(personal.getVehiculo() != null) {
            Vehiculo vehiculo = vehiculoRepository.getOne(personal.getVehiculo());
            personaDto.setVehiculo(daoToDtoConverter.convertDaoToDtoVehiculo(vehiculo));
        }

        if(personal.getCliente() != null && personal.getClienteDomicilio() != null) {
            Cliente cliente = clienteRepository.getOne(personal.getCliente());
            ClienteDomicilio clienteDomicilio = clienteDomicilioRepository.getOne(personal.getClienteDomicilio());

            personaDto.setCliente(daoToDtoConverter.convertDaoToDtoCliente(cliente));
            personaDto.setClienteDomicilio(daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilio));
        }

        if(StringUtils.isNotBlank(personal.getRutaVolanteCuip())) {
            personaDto.setArchivoVolanteCuipCargado(true);
        }
        return personaDto;
    }

    @Override
    public PersonaDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("La empresa o el id vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la persona con el id [{}]", id);

        Personal personal = personaRepository.getOne(id);

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setNacionalidad(catalogoService.obtenerNacionalidadPorId(personal.getNacionalidad()));

        return personaDto;
    }

    @Override
    @Transactional
    public PersonaDto crearNuevo(PersonaDto personalDto, String username) {
        if(StringUtils.isBlank(username) || personalDto == null) {
            logger.warn("El usuario, la empresa o el personal a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo personal");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Personal personal = dtoToDaoConverter.convertDtoToDaoPersonal(personalDto);
        personal.setFechaNacimiento(LocalDate.parse(personalDto.getFechaNacimiento()));
        personal.setFechaIngreso(LocalDate.parse(personalDto.getFechaIngreso()));
        daoHelper.fulfillAuditorFields(true, personal, usuarioDto.getId());
        personal.setEmpresa(usuarioDto.getEmpresa().getId());
        personal.setCalleCatalogo(personalDto.getCalleCatalogo().getId());
        personal.setColoniaCatalogo(personalDto.getColoniaCatalogo().getId());
        personal.setLocalidadCatalogo(personalDto.getLocalidadCatalogo().getId());
        personal.setMunicipioCatalogo(personalDto.getMunicipioCatalogo().getId());
        personal.setEstadoCatalogo(personalDto.getEstadoCatalogo().getId());
        personal.setNacionalidad(personalDto.getNacionalidad().getId());
        personal.setDomicilio1(personalDto.getCalleCatalogo().getNombre());
        personal.setDomicilio2(personalDto.getColoniaCatalogo().getNombre());
        personal.setLocalidad(personalDto.getLocalidadCatalogo().getNombre());
        personal.setEstado(personalDto.getEstadoCatalogo().getNombre());
        personal.setDomicilio3(personalDto.getMunicipioCatalogo().getNombre());

        Personal personalCreado = personaRepository.save(personal);

        PersonaDto response = daoToDtoConverter.convertDaoToDtoPersona(personalCreado);
        return response;
    }

    @Override
    public File descargarVolanteCuip(String personaUuid) {
        if(StringUtils.isBlank(personaUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando el volante de cuip para la persona [{}]", personaUuid);

        Personal personal = personaRepository.getByUuid(personaUuid);

        if(personal == null) {
            logger.warn("El personal no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(personal.getEstatusCuip() != CuipStatusEnum.EN_TRAMITE) {
            logger.warn("El status no es en tramite. No es posible descargar el volante");
            throw new InvalidDataException();
        }

        return new File(personal.getRutaVolanteCuip());
    }

    @Override
    @Transactional
    public PersonaDto modificarInformacionPuesto(PersonaDto personaDto, String username, String personaUuid, MultipartFile multipartFile) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(personaUuid) || personaDto == null) {
            logger.warn("El usuario, la empresa, la persona o la informacion del trabajo a modificar vienen como nulas o invalidas");
            throw new InvalidDataException();
        }

        logger.info("Modificando los detalles de trabajo para la persona [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(personal == null) {
            logger.warn("La persona a cambiar la informacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(personaDto.getFormaEjecucion() != personal.getFormaEjecucion()) {
            logger.info("La forma de ejecucion ha cambiado. Revisando si era canes");
            if(personal.getFormaEjecucion() == FormaEjecucionEnum.CANES && personal.getCan() != null) {
                logger.info("La forma de ejecucion era canes, desasignando");
                PersonalCanDto personalCanDto = new PersonalCanDto();
                personalCanDto.setMotivoBajaAsignacion("Baja por cambio de forma de ejecucion");
                desasignarCanAPersona(personaUuid, personalCanDto, username);
            }
        }

        // TODO: Hacer las validaciones respecto al tipo
        personal.setPuesto(personaDto.getPuestoDeTrabajo().getId());
        personal.setSubpuesto(personaDto.getSubpuestoDeTrabajo().getId());
        personal.setDetallesPuesto(personaDto.getDetallesPuesto());
        personal.setDomicilioAsignado(personaDto.getDomicilioAsignado().getId());
        personal.setEstatusCuip(personaDto.getEstatusCuip());
        personal.setFormaEjecucion(personaDto.getFormaEjecucion());

        if(personaDto.getEstatusCuip() == CuipStatusEnum.NA) {
            personal.setFechaVolanteCuip(null);
            personal.setNumeroVolanteCuip(null);
            personal.setCuip(null);
            personal.setRutaVolanteCuip(null);
        } else if(personaDto.getEstatusCuip() == CuipStatusEnum.EN_TRAMITE) {
            personal.setCuip(null);
            personal.setFechaVolanteCuip(LocalDate.parse(personaDto.getFechaVolanteCuip()));
            personal.setNumeroVolanteCuip(personaDto.getNumeroVolanteCuip());
            if(multipartFile != null) {
                logger.info("Se subio con un archivo. Agregando");
                String rutaArchivoNuevo = "";
                try {
                    rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.VOLANTE_CUIP, usuarioDto.getEmpresa().getUuid());
                    personal.setRutaVolanteCuip(rutaArchivoNuevo);
                } catch(Exception ex) {
                    logger.warn("No se ha podido guardar el archivo.", ex);
                    throw new InvalidDataException();
                }
            }
        } else if(personaDto.getEstatusCuip() == CuipStatusEnum.TRAMITADO) {
            Personal personalPorCuip = personaRepository.getByCuipAndEliminadoFalse(personaDto.getCuip());

            if(personalPorCuip != null && personalPorCuip.getId() != personal.getId()) {
                logger.warn("El CUIP esta registrado a una persona ya activa");
                throw new AlreadyExistsPersonByCuipException();
            }

            personal.setFechaVolanteCuip(null);
            personal.setNumeroVolanteCuip(null);
            personal.setRutaVolanteCuip(null);
            personal.setCuip(personaDto.getCuip());
        }

        if(personaDto.getModalidad() != null) {
            logger.info("La informacion del trabajo incluye modalidad");
            personal.setModalidad(personaDto.getModalidad().getId());
        }

        if(!personal.isPuestoTrabajoCapturado()) {
            personal.setPuestoTrabajoCapturado(true);
        }

        // Validando si ha cambiado el puesto de trabajo a uno sin portacion
        if(!personaDto.getSubpuestoDeTrabajo().isPortacion()) {
            logger.debug("El puesto no tiene portacion. Verificando armas");
            if(personal.getArmaCorta() != null) {
                PersonalArmaDto personalArmaDto = new PersonalArmaDto();
                personalArmaDto.setMotivoBajaAsignacion("Baja por cambio de puesto que no permite la portacion");
                desasignarArmaCortaAPersona(personaUuid, personalArmaDto, username);
            }
            if(personal.getArmaLarga() != null) {
                PersonalArmaDto personalArmaDto = new PersonalArmaDto();
                personalArmaDto.setMotivoBajaAsignacion("Baja por cambio de puesto que no permite la portacion");
                desasignarArmaLargaAPersona(personaUuid, personalArmaDto, username);
            }
        }

        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());
        personaRepository.save(personal);

        PersonaDto response = daoToDtoConverter.convertDaoToDtoPersona(personal);
        response.setPuestoDeTrabajo(personaDto.getPuestoDeTrabajo());
        response.setSubpuestoDeTrabajo(personaDto.getSubpuestoDeTrabajo());
        return response;
    }

    @Override
    @Transactional
    public PersonaDto modificarPersona(String personaUuid, String username, PersonaDto personaDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(personaUuid) || personaDto == null) {
            logger.warn("El usuario, la empresa, la persona o la informacion del trabajo a modificar vienen como nulas o invalidas");
            throw new InvalidDataException();
        }

        logger.info("Modificando la persona con el uuid [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(personal == null) {
            logger.warn("La persona a cambiar la informacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        personal.setNacionalidad(personaDto.getNacionalidad().getId());
        personal.setNombres(personaDto.getNombres());
        personal.setApellidoPaterno(personaDto.getApellidoPaterno());
        personal.setApellidoMaterno(personaDto.getApellidoMaterno());
        personal.setDomicilio4(personaDto.getDomicilio4());
        personal.setCodigoPostal(personaDto.getCodigoPostal());
        personal.setCurp(personaDto.getCurp());
        personal.setFechaNacimiento(LocalDate.parse(personaDto.getFechaNacimiento()));
        personal.setFechaIngreso(LocalDate.parse(personaDto.getFechaIngreso()));
        personal.setEstadoCivil(personaDto.getEstadoCivil());
        personal.setTelefono(personaDto.getTelefono());
        personal.setCorreoElectronico(personaDto.getCorreoElectronico());
        personal.setTipoSangre(personaDto.getTipoSangre());


        personal.setEstadoCatalogo(personaDto.getEstadoCatalogo().getId());
        personal.setMunicipioCatalogo(personaDto.getMunicipioCatalogo().getId());
        personal.setLocalidadCatalogo(personaDto.getLocalidadCatalogo().getId());
        personal.setColoniaCatalogo(personaDto.getColoniaCatalogo().getId());
        personal.setCalleCatalogo(personaDto.getCalleCatalogo().getId());

        personal.setDomicilio1(personaDto.getCalleCatalogo().getNombre());
        personal.setDomicilio2(personaDto.getColoniaCatalogo().getNombre());
        personal.setLocalidad(personaDto.getLocalidadCatalogo().getNombre());
        personal.setDomicilio3(personaDto.getMunicipioCatalogo().getNombre());
        personal.setEstado(personaDto.getEstadoCatalogo().getNombre());

        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());

        personaRepository.save(personal);
        return daoToDtoConverter.convertDaoToDtoPersona(personal);
    }

    @Transactional
    @Override
    public PersonaDto eliminarPersona(String personaUuid, String username, PersonaDto personaDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(personaUuid) || personaDto == null) {
            logger.warn("El usuario, la empresa, la persona o la informacion del trabajo a modificar vienen como nulas o invalidas");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la persona con el uuid [{}]", personaUuid);

        Personal personal = personaRepository.getByUuidAndEliminadoFalse(personaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(personal == null) {
            logger.warn("La persona a eliminar la informacion no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());
        personal.setMotivoBaja(personaDto.getMotivoBaja());
        personal.setObservacionesBaja(personaDto.getObservacionesBaja());
        personal.setFechaBaja(LocalDate.now());
        personal.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_PERSONAL, personaUuid);
                personal.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }
        personaRepository.save(personal);
        return daoToDtoConverter.convertDaoToDtoPersona(personal);
    }

    @Transactional
    @Override
    public void asignarCanAPersona(String personaUuid, PersonalCanDto personalCanDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalCanDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        if(personalCanDto.getCan() == null) {
            logger.info("El can que se le va a asignar a la persona viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Asignando el can a la persona con uuid [{}]", personaUuid);

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonalCan existePersonaCan = personalCanRepository.getByPersonalAndEliminadoFalse(persona.getId());

        if (existePersonaCan != null || persona.getCan() != null) {
            logger.warn("La persona ya tiene un can asignado");
            throw new AlreadyAssignedResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Can can = canRepository.getByUuidAndEliminadoFalse(personalCanDto.getCan().getUuid());

        if(can.getStatus() != CanStatusEnum.INSTALACIONES) {
            logger.warn("El can no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        persona.setCan(personalCanDto.getCan().getId());
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        can.setStatus(CanStatusEnum.ACTIVO);
        daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());
        canRepository.save(can);

        PersonalCan personalCan = new PersonalCan();
        personalCan.setPersonal(persona.getId());
        personalCan.setCan(personalCanDto.getCan().getId());
        personalCan.setObservaciones(personalCanDto.getObservaciones());
        personalCan.setUuid(RandomStringUtils.randomAlphanumeric(12));
        daoHelper.fulfillAuditorFields(true, personalCan, usuarioDto.getId());

        personalCanRepository.save(personalCan);
    }

    @Transactional
    @Override
    public void desasignarCanAPersona(String personaUuid, PersonalCanDto personalCanDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalCanDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Quitando la asignacion del can a la persona con uuid [{personaUuid}]");

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonalCan personalCan = personalCanRepository.getByPersonalAndEliminadoFalse(persona.getId());

        if (personalCan == null || persona.getCan() == null) {
            logger.warn("La persona no tiene can asignado actualmente");
            throw new AlreadyAssignedResourceException();
        }

        Can can = canRepository.getOne(personalCan.getCan());

        if(can.getStatus() != CanStatusEnum.ACTIVO || can.getEliminado()) {
            logger.warn("El can no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        persona.setCan(null);
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        can.setStatus(CanStatusEnum.INSTALACIONES);
        daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());
        canRepository.save(can);

        personalCan.setEliminado(true);
        personalCan.setMotivoBajaAsignacion(personalCanDto.getMotivoBajaAsignacion());
        daoHelper.fulfillAuditorFields(true, personalCan, usuarioDto.getId());

        personalCanRepository.save(personalCan);
    }

    @Transactional
    @Override
    public void asignarVehiculoAPersona(String personaUuid, PersonalVehiculoDto personalVehiculoDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalVehiculoDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        if(personalVehiculoDto.getVehiculo() == null) {
            logger.info("El vehiculo que se le va a asignar a la persona viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Asignando el vehiculo a la persona con uuid [{}]", personaUuid);

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonalVehiculo existePersonaVehiculo = personalVehiculoRepository.getByPersonalAndEliminadoFalse(persona.getId());

        if (existePersonaVehiculo != null || persona.getVehiculo() != null) {
            logger.warn("La persona ya tiene un vehiculo asignado");
            throw new AlreadyAssignedResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Vehiculo vehiculo = vehiculoRepository.getByUuidAndEliminadoFalse(personalVehiculoDto.getVehiculo().getUuid());

        if(vehiculo.getStatus() != VehiculoStatusEnum.INSTALACIONES.INSTALACIONES) {
            logger.warn("El vehiculo no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        persona.setVehiculo(personalVehiculoDto.getVehiculo().getId());
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        vehiculo.setStatus(VehiculoStatusEnum.ACTIVO);
        daoHelper.fulfillAuditorFields(false, vehiculo, usuarioDto.getId());
        vehiculoRepository.save(vehiculo);

        PersonalVehiculo personalVehiculo = new PersonalVehiculo();
        personalVehiculo.setPersonal(persona.getId());
        personalVehiculo.setVehiculo(personalVehiculoDto.getVehiculo().getId());
        personalVehiculo.setObservaciones(personalVehiculoDto.getObservaciones());
        personalVehiculo.setUuid(RandomStringUtils.randomAlphanumeric(12));
        daoHelper.fulfillAuditorFields(true, personalVehiculo, usuarioDto.getId());

        personalVehiculoRepository.save(personalVehiculo);
    }

    @Transactional
    @Override
    public void desasignarVehiculoAPersona(String personaUuid, PersonalVehiculoDto personalVehiculoDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalVehiculoDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Quitando la asignacion del can a la persona con uuid [{personaUuid}]");

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonalVehiculo personalVehiculo = personalVehiculoRepository.getByPersonalAndEliminadoFalse(persona.getId());

        if (personalVehiculo == null || persona.getVehiculo() == null) {
            logger.warn("La persona no tiene vehiculo asignado actualmente");
            throw new AlreadyAssignedResourceException();
        }

        Vehiculo vehiculo = vehiculoRepository.getOne(personalVehiculo.getVehiculo());

        if(vehiculo.getStatus() != VehiculoStatusEnum.ACTIVO || vehiculo.getEliminado()) {
            logger.warn("El vehiculo no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        persona.setVehiculo(null);
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        vehiculo.setStatus(VehiculoStatusEnum.INSTALACIONES);
        daoHelper.fulfillAuditorFields(false, vehiculo, usuarioDto.getId());
        vehiculoRepository.save(vehiculo);

        personalVehiculo.setEliminado(true);
        personalVehiculo.setMotivoBajaAsignacion(personalVehiculoDto.getMotivoBajaAsignacion());
        daoHelper.fulfillAuditorFields(true, personalVehiculo, usuarioDto.getId());

        personalVehiculoRepository.save(personalVehiculo);
    }

    @Transactional
    @Override
    public void asignarArmaCortaAPersona(String personaUuid, PersonalArmaDto personalArmaDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalArmaDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        if(personalArmaDto.getArma() == null) {
            logger.info("El arma corta que se le va a asignar a la persona viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Asignando el arma corta a la persona con uuid [{}]", personaUuid);

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(persona.getEstatusCuip() != CuipStatusEnum.TRAMITADO) {
            logger.warn("La persona no tiene su CUIP tramitada. No se puede asignar el arma corta");
            throw new MissingCuipException();
        }

        PersonalArma existePersonaArma = personalArmaRepository.getByPersonalAndArmaAndEliminadoFalse(persona.getId(), personalArmaDto.getArma().getId());

        if (existePersonaArma != null || persona.getArmaCorta() != null) {
            logger.warn("La persona ya tiene un can asignado");
            throw new AlreadyAssignedResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Arma arma = armaRepository.getByUuidAndEliminadoFalse(personalArmaDto.getArma().getUuid());

        if(arma.getStatus() != ArmaStatusEnum.DEPOSITO) {
            logger.warn("El arma corta no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        if(arma.getTipo() != ArmaTipoEnum.CORTA) {
            logger.warn("El arma a asignar no es corta. Se intenta asignar un arma corta");
            throw new NotFoundResourceException();
        }

        persona.setArmaCorta(personalArmaDto.getArma().getId());
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        arma.setStatus(ArmaStatusEnum.ASIGNADA);
        daoHelper.fulfillAuditorFields(false, arma, usuarioDto.getId());
        armaRepository.save(arma);

        PersonalArma personalArma = new PersonalArma();
        personalArma.setPersonal(persona.getId());
        personalArma.setArma(personalArmaDto.getArma().getId());
        personalArma.setObservaciones(personalArmaDto.getObservaciones());
        personalArma.setUuid(RandomStringUtils.randomAlphanumeric(12));
        personalArma.setTipo(personalArmaDto.getArma().getTipo());
        daoHelper.fulfillAuditorFields(true, personalArma, usuarioDto.getId());

        personalArmaRepository.save(personalArma);
    }

    @Transactional
    @Override
    public void desasignarArmaCortaAPersona(String personaUuid, PersonalArmaDto personalArmaDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalArmaDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Quitando la asignacion del arma corta a la persona con uuid [{personaUuid}]");

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<PersonalArma> personalArmas = personalArmaRepository.getAllByPersonalAndEliminadoFalse(persona.getId());

        AtomicReference<PersonalArma> armaEncontrada = new AtomicReference<>();

        personalArmas.forEach(pa -> {
            Arma arma = armaRepository.getOne(pa.getArma());
            if(arma.getTipo() == ArmaTipoEnum.CORTA) {
                armaEncontrada.set(pa);
            }
        });

        PersonalArma personalArma = armaEncontrada.get();

        if (personalArma == null || persona.getArmaCorta() == null) {
            logger.warn("La persona no tiene can asignado actualmente");
            throw new AlreadyAssignedResourceException();
        }

        Arma armaCorta = armaRepository.getOne(personalArma.getArma());

        if(armaCorta.getStatus() != ArmaStatusEnum.ASIGNADA || armaCorta.getEliminado()) {
            logger.warn("El arma corta no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        persona.setArmaCorta(null);
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        armaCorta.setStatus(ArmaStatusEnum.DEPOSITO);
        daoHelper.fulfillAuditorFields(false, armaCorta, usuarioDto.getId());
        armaRepository.save(armaCorta);

        personalArma.setEliminado(true);
        personalArma.setMotivoBajaAsignacion(personalArmaDto.getMotivoBajaAsignacion());
        daoHelper.fulfillAuditorFields(true, personalArma, usuarioDto.getId());

        personalArmaRepository.save(personalArma);
    }

    @Transactional
    @Override
    public void asignarArmaLargaAPersona(String personaUuid, PersonalArmaDto personalArmaDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalArmaDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        if(personalArmaDto.getArma() == null) {
            logger.info("El arma larga que se le va a asignar a la persona viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Asignando el arma larga a la persona con uuid [{}]", personaUuid);

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(persona.getEstatusCuip() != CuipStatusEnum.TRAMITADO) {
            logger.warn("La persona no tiene su CUIP tramitada. No se puede asignar el arma corta");
            throw new MissingCuipException();
        }

        PersonalArma existePersonaArma = personalArmaRepository.getByPersonalAndArmaAndEliminadoFalse(persona.getId(), personalArmaDto.getArma().getId());

        if (existePersonaArma != null || persona.getArmaLarga() != null) {
            logger.warn("La persona ya tiene un arma larga asignada");
            throw new AlreadyAssignedResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Arma arma = armaRepository.getByUuidAndEliminadoFalse(personalArmaDto.getArma().getUuid());

        if(arma.getStatus() != ArmaStatusEnum.DEPOSITO) {
            logger.warn("El arma larga no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        if(arma.getTipo() != ArmaTipoEnum.LARGA) {
            logger.warn("El arma a asignar no es larga. Se intenta asignar un arma larga");
            throw new NotFoundResourceException();
        }

        persona.setArmaLarga(personalArmaDto.getArma().getId());
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        arma.setStatus(ArmaStatusEnum.ASIGNADA);
        daoHelper.fulfillAuditorFields(false, arma, usuarioDto.getId());
        armaRepository.save(arma);

        PersonalArma personalArma = new PersonalArma();
        personalArma.setPersonal(persona.getId());
        personalArma.setArma(personalArmaDto.getArma().getId());
        personalArma.setObservaciones(personalArmaDto.getObservaciones());
        personalArma.setUuid(RandomStringUtils.randomAlphanumeric(12));
        personalArma.setTipo(personalArmaDto.getArma().getTipo());
        daoHelper.fulfillAuditorFields(true, personalArma, usuarioDto.getId());

        personalArmaRepository.save(personalArma);
    }

    @Transactional
    @Override
    public void desasignarArmaLargaAPersona(String personaUuid, PersonalArmaDto personalArmaDto, String username) {
        if(StringUtils.isBlank(personaUuid) || personalArmaDto == null || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Quitando la asignacion del arma larga a la persona con uuid [{personaUuid}]");

        Personal persona = personaRepository.getByUuidAndEliminadoFalse(personaUuid);

        if(persona == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<PersonalArma> personalArmas = personalArmaRepository.getAllByPersonalAndEliminadoFalse(persona.getId());

        AtomicReference<PersonalArma> armaEncontrada = new AtomicReference<>();

        personalArmas.forEach(pa -> {
            Arma arma = armaRepository.getOne(pa.getArma());
            if(arma.getTipo() == ArmaTipoEnum.LARGA) {
                armaEncontrada.set(pa);
            }
        });

        PersonalArma personalArma = armaEncontrada.get();

        if (personalArma == null || persona.getArmaLarga() == null) {
            logger.warn("La persona no tiene can asignado actualmente");
            throw new AlreadyAssignedResourceException();
        }

        Arma armaLarga = armaRepository.getOne(personalArma.getArma());

        if(armaLarga.getStatus() != ArmaStatusEnum.ASIGNADA || armaLarga.getEliminado()) {
            logger.warn("El arma larga no se encuentra en un status adecuado");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        persona.setArmaLarga(null);
        daoHelper.fulfillAuditorFields(false, persona, usuarioDto.getId());
        personaRepository.save(persona);

        armaLarga.setStatus(ArmaStatusEnum.DEPOSITO);
        daoHelper.fulfillAuditorFields(false, armaLarga, usuarioDto.getId());
        armaRepository.save(armaLarga);

        personalArma.setEliminado(true);
        personalArma.setMotivoBajaAsignacion(personalArmaDto.getMotivoBajaAsignacion());
        daoHelper.fulfillAuditorFields(true, personalArma, usuarioDto.getId());

        personalArmaRepository.save(personalArma);
    }
}
