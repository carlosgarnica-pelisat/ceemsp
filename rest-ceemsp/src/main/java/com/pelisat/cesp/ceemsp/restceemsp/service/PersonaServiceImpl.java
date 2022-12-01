package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.ModalidadDto;
import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.type.CuipStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonaServiceImpl implements PersonaService {

    private final DaoHelper<CommonModel> daoHelper;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
    private final ModalidadService modalidadService;
    private final PersonalPuestoDeTrabajoService personalPuestoDeTrabajoService;
    private final PersonalSubpuestoDeTrabajoService personalSubpuestoDeTrabajoService;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final PersonalNacionalidadService personalNacionalidadService;
    private final PersonalFotografiaService personalFotografiaService;
    private final PersonalCertificacionService personalCertificacionService;
    private final EstadoService estadoService;
    private final MunicipioService municipioService;
    private final ColoniaService coloniaService;
    private final LocalidadService localidadService;
    private final CalleService calleService;
    private final ArchivosService archivosService;
    private final Logger logger = LoggerFactory.getLogger(PersonaService.class);

    @Autowired
    public PersonaServiceImpl(DaoHelper<CommonModel> daoHelper, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaService empresaService,
                              UsuarioService usuarioService, PersonaRepository personaRepository,
                              ModalidadService modalidadService, PersonalPuestoDeTrabajoServiceImpl personalPuestoDeTrabajoService,
                              EmpresaDomicilioService empresaDomicilioService, PersonalNacionalidadService personalNacionalidadService,
                              PersonalCertificacionService personalCertificacionService, PersonalSubpuestoDeTrabajoService personalSubpuestoDeTrabajoService,
                              PersonalFotografiaService personalFotografiaService, EstadoService estadoService, MunicipioService municipioService,
                              LocalidadService localidadService, ColoniaService coloniaService, CalleService calleService,
                              ArchivosService archivosService) {
        this.daoHelper = daoHelper;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.personaRepository = personaRepository;
        this.personalPuestoDeTrabajoService = personalPuestoDeTrabajoService;
        this.modalidadService = modalidadService;
        this.empresaDomicilioService = empresaDomicilioService;
        this.personalNacionalidadService = personalNacionalidadService;
        this.personalCertificacionService = personalCertificacionService;
        this.personalSubpuestoDeTrabajoService = personalSubpuestoDeTrabajoService;
        this.personalFotografiaService = personalFotografiaService;
        this.calleService = calleService;
        this.coloniaService = coloniaService;
        this.localidadService = localidadService;
        this.estadoService = estadoService;
        this.municipioService = municipioService;
        this.archivosService = archivosService;
    }

    @Override
    public List<PersonaDto> obtenerTodos(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Personal> personal = personaRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return personal.stream().map(daoToDtoConverter::convertDaoToDtoPersona).collect(Collectors.toList());
    }

    @Override
    public List<PersonaDto> obtenerPersonasEliminadas(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Personal> personal = personaRepository.getAllByEmpresaAndEliminadoTrue(empresaDto.getId());

        return personal.stream().map(daoToDtoConverter::convertDaoToDtoPersona).collect(Collectors.toList());
    }

    @Override
    public PersonaDto obtenerPorUuid(String empresaUuid, String personaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid)) {
            logger.warn("El uuid de la empresa o de la persona vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo a la persona con el uuid [{}]", personaUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        Personal personal = personaRepository.getByUuid(personaUuid);
        if(personal == null) {
            logger.warn("La persona no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(personal.getEmpresa() != empresaDto.getId()) {
            logger.warn("El personal a consultar no pertenece a este recurso");
            throw new MissingRelationshipException();
        }

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setCertificaciones(personalCertificacionService.obtenerCertificacionesPorPersona(empresaUuid, personaUuid));
        personaDto.setNacionalidad(personalNacionalidadService.obtenerPorId(personal.getNacionalidad()));
        if(personal.getPuesto() > 0) {
            personaDto.setPuestoDeTrabajo(personalPuestoDeTrabajoService.obtenerPorId(personal.getPuesto()));
        }
        if(personal.getSubpuesto() > 0) {
            personaDto.setSubpuestoDeTrabajo(personalSubpuestoDeTrabajoService.obtenerPorId(personal.getSubpuesto()));
        }
        if(personal.getDomicilioAsignado() > 0) {
            personaDto.setDomicilioAsignado(empresaDomicilioService.obtenerPorId(personal.getDomicilioAsignado()));
        }
        personaDto.setFotografias(personalFotografiaService.mostrarPersonalFotografias(empresaUuid, personaUuid));
        personaDto.setCalleCatalogo(calleService.obtenerCallePorId(personal.getCalleCatalogo()));
        personaDto.setColoniaCatalogo(coloniaService.obtenerColoniaPorId(personal.getColoniaCatalogo()));
        personaDto.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(personal.getLocalidadCatalogo()));
        personaDto.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(personal.getMunicipioCatalogo()));
        personaDto.setEstadoCatalogo(estadoService.obtenerPorId(personal.getEstadoCatalogo()));
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

        if(personal == null || personal.getEliminado())  {
            logger.warn("La persona no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        PersonaDto personaDto = daoToDtoConverter.convertDaoToDtoPersona(personal);
        personaDto.setNacionalidad(personalNacionalidadService.obtenerPorId(personal.getNacionalidad()));

        return personaDto;
    }

    @Override
    public PersonaDto crearNuevo(PersonaDto personalDto, String username, String empresaUuid) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || personalDto == null) {
            logger.warn("El usuario, la empresa o el personal a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo personal");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        Personal personal = dtoToDaoConverter.convertDtoToDaoPersonal(personalDto);
        personal.setFechaNacimiento(LocalDate.parse(personalDto.getFechaNacimiento()));
        personal.setFechaIngreso(LocalDate.parse(personalDto.getFechaIngreso()));
        daoHelper.fulfillAuditorFields(true, personal, usuarioDto.getId());
        personal.setEmpresa(empresaDto.getId());
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
        response.setNacionalidad(personalDto.getNacionalidad());
        response.setCalleCatalogo(personalDto.getCalleCatalogo());
        response.setColoniaCatalogo(personalDto.getColoniaCatalogo());
        response.setLocalidadCatalogo(personalDto.getLocalidadCatalogo());
        response.setMunicipioCatalogo(personalDto.getMunicipioCatalogo());
        response.setEstadoCatalogo(personalDto.getEstadoCatalogo());
        return response;
    }

    @Override
    public PersonaDto modificarInformacionPuesto(PersonaDto personaDto, String username, String empresaUuid, String personaUuid, MultipartFile multipartFile) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) | StringUtils.isBlank(personaUuid) || personaDto == null) {
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

        // TODO: Hacer las validaciones respecto al tipo
        personal.setPuesto(personaDto.getPuestoDeTrabajo().getId());
        personal.setSubpuesto(personaDto.getSubpuestoDeTrabajo().getId());
        personal.setDetallesPuesto(personaDto.getDetallesPuesto());
        personal.setDomicilioAsignado(personaDto.getDomicilioAsignado().getId());
        personal.setEstatusCuip(personaDto.getEstatusCuip());
        personal.setCuip(personaDto.getCuip());
        personal.setNumeroVolanteCuip(personaDto.getNumeroVolanteCuip());

        if(personaDto.getModalidad() != null) {
            logger.info("La informacion del trabajo incluye modalidad");
            personal.setModalidad(personaDto.getModalidad().getId());
        }

        if(!StringUtils.isBlank(personaDto.getFechaVolanteCuip())) {
            logger.info("El volante de CUIP esta puesto. Convirtiendo");
            personal.setFechaVolanteCuip(LocalDate.parse(personaDto.getFechaVolanteCuip()));
        }

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.VOLANTE_CUIP, empresaUuid);
                personal.setRutaVolanteCuip(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo.", ex);
                throw new InvalidDataException();
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
    public PersonaDto modificarPersona(String empresaUuid, String personaUuid, String username, PersonaDto personaDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) | StringUtils.isBlank(personaUuid) || personaDto == null) {
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

        personal.setRfc(personaDto.getRfc());

        daoHelper.fulfillAuditorFields(false, personal, usuarioDto.getId());

        personaRepository.save(personal);
        return daoToDtoConverter.convertDaoToDtoPersona(personal);
    }

    @Override
    public PersonaDto eliminarPersona(String empresaUuid, String personaUuid, String username, PersonaDto personaDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid) || personaDto == null) {
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
        personal.setFechaBaja(LocalDate.parse(personaDto.getFechaBaja()));
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

    @Override
    public File descargarVolanteCuip(String empresaUuid, String personaUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(personaUuid)) {
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
}
