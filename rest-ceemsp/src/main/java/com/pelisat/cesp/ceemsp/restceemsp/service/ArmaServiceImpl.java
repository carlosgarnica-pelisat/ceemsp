package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.IncidenciaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArmaServiceImpl implements ArmaService {

    private static final int MAX_NUMEROS = 10;

    private final Logger logger = LoggerFactory.getLogger(ArmaService.class);
    private final ArmaRepository armaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaService empresaService;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArmaMarcaService armaMarcaService;
    private final ArmaClaseService armaClaseService;
    private final EmpresaLicenciaColectivaService empresaLicenciaColectivaService;
    private final PersonaService personaService;
    private final ArchivosService archivosService;
    private final IncidenciaRepository incidenciaRepository;
    private final IncidenciaArmaRepository incidenciaArmaRepository;
    private final IncidenciaComentarioRepository incidenciaComentarioRepository;
    private final IncidenciaArchivoRepository incidenciaArchivoRepository;

    @Autowired
    public ArmaServiceImpl(ArmaRepository armaRepository, DaoToDtoConverter daoToDtoConverter,
                           DtoToDaoConverter dtoToDaoConverter, EmpresaService empresaService,
                           UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper,
                           EmpresaDomicilioService empresaDomicilioService, ArmaMarcaService armaMarcaService,
                           ArmaClaseService armaClaseService, EmpresaLicenciaColectivaService empresaLicenciaColectivaService,
                           PersonaService personaService, IncidenciaRepository incidenciaRepository, IncidenciaArmaRepository incidenciaArmaRepository,
                           IncidenciaComentarioRepository incidenciaComentarioRepository, ArchivosService archivosService,
                           IncidenciaArchivoRepository incidenciaArchivoRepository) {
        this.armaRepository = armaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.empresaDomicilioService = empresaDomicilioService;
        this.armaMarcaService = armaMarcaService;
        this.armaClaseService = armaClaseService;
        this.empresaLicenciaColectivaService = empresaLicenciaColectivaService;
        this.personaService = personaService;
        this.incidenciaRepository = incidenciaRepository;
        this.incidenciaArmaRepository = incidenciaArmaRepository;
        this.incidenciaComentarioRepository = incidenciaComentarioRepository;
        this.archivosService = archivosService;
        this.incidenciaArchivoRepository = incidenciaArchivoRepository;
    }

    @Deprecated
    @Override
    public List<ArmaDto> obtenerArmasPorEmpresaUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las armas guardadas para la empresa {}", uuid);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        List<Arma> armas = armaRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        List<ArmaDto> response = armas.stream().map(arma -> {
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
            armaDto.setMarca(armaMarcaService.obtenerPorId(arma.getMarca()));
            armaDto.setClase(armaClaseService.obtenerPorId(arma.getClase()));
            return armaDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public List<ArmaDto> obtenerArmasPorLicenciaColectivaUuid(String empresaUuid, String licenciaColectivaUuid) {
        if(StringUtils.isBlank(licenciaColectivaUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las armas guardadas para la empresa {}", licenciaColectivaUuid);
        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(empresaUuid, licenciaColectivaUuid, false);
        List<Arma> armas = armaRepository.getAllByLicenciaColectivaAndEliminadoFalse(empresaLicenciaColectivaDto.getId());

        List<ArmaDto> response = armas.stream().map(arma -> {
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
            armaDto.setMarca(armaMarcaService.obtenerPorId(arma.getMarca()));
            armaDto.setClase(armaClaseService.obtenerPorId(arma.getClase()));
            if(arma.getPersonal() != null) {
                armaDto.setPersonal(personaService.obtenerPorId(arma.getPersonal()));
            }
            if(arma.getIncidencia() != null) {
                Incidencia incidencia = incidenciaRepository.getOne(arma.getIncidencia());
                armaDto.setIncidencia(daoToDtoConverter.convertDaoToDtoIncidencia(incidencia));
            }
            return armaDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public List<ArmaDto> obtenerTodasArmasPorLicenciaColectivaUuid(String empresaUuid, String licenciaColectivaUuid) {
        if(StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las armas guardadas para la empresa {}", licenciaColectivaUuid);
        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(empresaUuid, licenciaColectivaUuid, false);
        List<Arma> armas = armaRepository.getAllByLicenciaColectiva(empresaLicenciaColectivaDto.getId());

        List<ArmaDto> response = armas.stream().map(arma -> {
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
            armaDto.setMarca(armaMarcaService.obtenerPorId(arma.getMarca()));
            armaDto.setClase(armaClaseService.obtenerPorId(arma.getClase()));
            if(arma.getPersonal() != null) {
                armaDto.setPersonal(personaService.obtenerPorId(arma.getPersonal()));
            }
            return armaDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public ArmaDto obtenerArmaPorUuid(String uuid, String armaUuid) {
        return null;
    }

    @Override
    public ArmaDto obtenerArmaPorId(String empresaUuid, Integer armaId) {
        if(StringUtils.isBlank(empresaUuid) || armaId == null || armaId < 1) {
            logger.warn("El uuid de la empresa o el id del vehiculo a consultar vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el arma con el id [{}]", armaId);

        Arma arma = armaRepository.getOne(armaId);

        if(arma == null) {
            logger.warn("El arma no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);

        armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
        armaDto.setMarca(armaMarcaService.obtenerPorId(arma.getMarca()));
        armaDto.setClase(armaClaseService.obtenerPorId(arma.getClase()));

        return armaDto;
    }

    @Override
    public ArmaDto guardarArma(String uuid, String licenciaColectivaUuid, String username, ArmaDto armaDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(username) || armaDto == null) {
            logger.warn("El uuid, el usuario o el arma a registrar vienen como nulas o vacias");
            throw new InvalidDataException();
        }

        logger.info("Registrando una nueva arma");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(uuid, licenciaColectivaUuid, true);

        Arma arma = dtoToDaoConverter.convertDtoToDaoArma(armaDto);
        daoHelper.fulfillAuditorFields(true, arma, usuarioDto.getId());
        arma.setEmpresa(empresaDto.getId());
        arma.setClase(armaDto.getClase().getId());
        arma.setBunker(armaDto.getBunker().getId());
        arma.setMarca(armaDto.getMarca().getId());
        arma.setLicenciaColectiva(empresaLicenciaColectivaDto.getId());
        arma.setStatus(armaDto.getStatus());

        if(armaDto.getPersonal() != null) {
            arma.setPersonal(armaDto.getPersonal().getId());
        }

        Arma armaCreada = armaRepository.save(arma);

        return daoToDtoConverter.convertDaoToDtoArma(armaCreada);
    }

    @Override
    public ArmaDto modificarArma(String uuid, String licenciaColectivaUuid, String armaUuid, String username, ArmaDto armaDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(armaUuid) || StringUtils.isBlank(username) || armaDto == null) {
            logger.warn("El uuid, el usuario o el arma a registrar vienen como nulas o vacias");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el arma con el uuid [{}]", armaUuid);

        Arma arma = armaRepository.getByUuidAndEliminadoFalse(armaUuid);

        if(arma == null) {
            logger.warn("El arma no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        arma.setClase(armaDto.getClase().getId());
        arma.setBunker(armaDto.getBunker().getId());
        arma.setMarca(armaDto.getMarca().getId());
        arma.setCalibre(armaDto.getCalibre());
        arma.setTipo(armaDto.getTipo());
        arma.setSerie(armaDto.getSerie());
        arma.setMatricula(armaDto.getMatricula());
        arma.setStatus(armaDto.getStatus());

        if(armaDto.getPersonal() != null) {
            arma.setPersonal(armaDto.getPersonal().getId());
        }

        daoHelper.fulfillAuditorFields(false, arma, usuario.getId());

        armaRepository.save(arma);
        return armaDto;
    }

    @Override
    public ArmaDto eliminarArma(String uuid, String licenciaColectivaUuid, String armaUuid, String username, ArmaDto armaDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(armaUuid) || StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa o el id del vehiculo a consultar vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el arma con el uuid [{}]", armaUuid);

        Arma arma = armaRepository.getByUuidAndEliminadoFalse(armaUuid);

        if(arma == null) {
            logger.warn("El arma no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        arma.setMotivoBaja(armaDto.getMotivoBaja());
        arma.setObservacionesBaja(armaDto.getObservacionesBaja());
        arma.setFechaBaja(LocalDate.parse(armaDto.getFechaBaja()));
        arma.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, arma, usuario.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_DOMICILIO, armaUuid);
                arma.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        arma.setEliminado(true);
        arma.setStatus(ArmaStatusEnum.BAJA);
        daoHelper.fulfillAuditorFields(false, arma, usuario.getId());
        armaRepository.save(arma);
        return daoToDtoConverter.convertDaoToDtoArma(arma);
    }

    @Transactional
    @Override
    public ArmaDto cambiarStatusCustodia(String uuid, String licenciaColectivaUuid, String armaUuid, String username, String relatoHechos, MultipartFile documentoFundatorio) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(armaUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(relatoHechos)) {
            logger.warn("Alguno de los parametros requeridos viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Marcando el arma con uuid [{}] como en custodia", armaUuid);

        Arma arma = armaRepository.getByUuidAndEliminadoFalse(armaUuid);

        if(arma == null) {
            logger.warn("El arma no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        arma.setStatus(ArmaStatusEnum.CUSTODIA);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        Incidencia incidencia = new Incidencia();

        incidencia.setNumero(RandomStringUtils.randomNumeric(MAX_NUMEROS));
        incidencia.setFechaIncidencia(LocalDate.now());
        incidencia.setStatus(IncidenciaStatusEnum.ABIERTA);
        incidencia.setRelevancia(true);
        incidencia.setEmpresa(empresaDto.getId());
        daoHelper.fulfillAuditorFields(true, incidencia, usuario.getId());
        Incidencia incidenciaCreada = incidenciaRepository.save(incidencia);

        IncidenciaArma incidenciaArma = new IncidenciaArma();
        incidenciaArma.setUuid(RandomStringUtils.randomAlphanumeric(12));
        incidenciaArma.setIncidencia(incidenciaCreada.getId());
        incidenciaArma.setArma(arma.getId());
        daoHelper.fulfillAuditorFields(true, incidenciaArma, usuario.getId());

        IncidenciaComentario incidenciaComentario = new IncidenciaComentario();
        incidenciaComentario.setUuid(RandomStringUtils.randomAlphanumeric(12));
        incidenciaArma.setIncidencia(incidenciaCreada.getId());
        incidenciaComentario.setComentario(relatoHechos);
        daoHelper.fulfillAuditorFields(true, incidenciaComentario, usuario.getId());

        incidenciaArmaRepository.save(incidenciaArma);
        incidenciaComentarioRepository.save(incidenciaComentario);

        if(documentoFundatorio != null) {
            logger.info("Hay archivo");
            IncidenciaArchivo incidenciaArchivo = new IncidenciaArchivo();
            incidenciaArchivo.setIncidencia(incidenciaCreada.getId());
            daoHelper.fulfillAuditorFields(true, incidenciaArchivo, usuario.getId());
            try {
                String ruta = archivosService.guardarArchivoMultipart(documentoFundatorio, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_INCIDENCIA, uuid);
                incidenciaArchivo.setRutaArchivo(ruta);
                incidenciaArchivoRepository.save(incidenciaArchivo);

                arma.setIncidencia(incidencia.getId());
                daoHelper.fulfillAuditorFields(false, arma, usuario.getId());
                armaRepository.save(arma);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo.", ex);
            }
        }

        return daoToDtoConverter.convertDaoToDtoArma(arma);
    }
}
