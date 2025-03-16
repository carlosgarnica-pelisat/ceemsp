package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Arma;
import com.pelisat.cesp.ceemsp.database.model.ArmaDomicilio;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Personal;
import com.pelisat.cesp.ceemsp.database.repository.ArmaDomicilioRepository;
import com.pelisat.cesp.ceemsp.database.repository.ArmaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.*;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaLicenciaColectivaArmaServiceImpl implements EmpresaLicenciaColectivaArmaService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaLicenciaColectivaArmaService.class);
    private final ArmaRepository armaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final CatalogoService catalogoService;
    private final EmpresaLicenciaColectivaService empresaLicenciaColectivaService;
    private final ArchivosService archivosService;
    private final ArmaDomicilioRepository armaDomicilioRepository;
    private final PersonaRepository personaRepository;

    @Autowired
    public EmpresaLicenciaColectivaArmaServiceImpl(ArmaRepository armaRepository, DaoToDtoConverter daoToDtoConverter,
                                                   DtoToDaoConverter dtoToDaoConverter, CatalogoService catalogoService,
                                                   UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper, ArchivosService archivosService,
                                                   EmpresaDomicilioService empresaDomicilioService, EmpresaLicenciaColectivaService empresaLicenciaColectivaService,
                                                   ArmaDomicilioRepository armaDomicilioRepository, PersonaRepository personaRepository) {
        this.armaRepository = armaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.empresaDomicilioService = empresaDomicilioService;
        this.empresaLicenciaColectivaService = empresaLicenciaColectivaService;
        this.catalogoService = catalogoService;
        this.archivosService = archivosService;
        this.armaDomicilioRepository = armaDomicilioRepository;
        this.personaRepository = personaRepository;
    }


    @Override
    public List<ArmaDto> obtenerArmasPorLicenciaColectivaUuid(String licenciaColectivaUuid) {
        if(StringUtils.isBlank(licenciaColectivaUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las armas guardadas para la empresa {}", licenciaColectivaUuid);
        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(licenciaColectivaUuid, false);
        List<Arma> armas = armaRepository.getAllByLicenciaColectivaAndEliminadoFalse(empresaLicenciaColectivaDto.getId());

        List<ArmaDto> response = armas.stream().map(arma -> {
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
            armaDto.setMarca(catalogoService.obtenerArmaMarcaPorId(arma.getMarca()));
            armaDto.setClase(catalogoService.obtenerArmaClasePorId(arma.getClase()));

            if(armaDto.getStatus() == ArmaStatusEnum.ASIGNADA || armaDto.getStatus() == ArmaStatusEnum.ACTIVA) {
                Personal personalAsignado;
                if(armaDto.getTipo() == ArmaTipoEnum.CORTA) {
                    personalAsignado = personaRepository.getByArmaCortaAndEliminadoFalse(arma.getId());
                } else if(armaDto.getTipo() == ArmaTipoEnum.LARGA) {
                    personalAsignado = personaRepository.getByArmaLargaAndEliminadoFalse(arma.getId());
                } else {
                    throw new RuntimeException();
                }
                if(personalAsignado != null) {
                    armaDto.setPersonalAsignado(daoToDtoConverter.convertDaoToDtoPersona(personalAsignado));
                }
            }

            return armaDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public ArmaDto obtenerArmaPorId(Integer armaId) {
        if(armaId == null || armaId < 1) {
            logger.warn("El uuid de la empresa o el id del vehiculo a consultar vienen como nulos o invalidos");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el arma con el id [{}]", armaId);

        Arma arma = armaRepository.getOne(armaId);

        if(arma == null) {
            logger.warn("El arma no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoArma(arma);
    }

    @Transactional
    @Override
    public ArmaDto guardarArma(String licenciaColectivaUuid, String username, ArmaDto armaDto) {
        if(StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(username) || armaDto == null) {
            logger.warn("El uuid, el usuario o el arma a registrar vienen como nulas o vacias");
            throw new InvalidDataException();
        }

        Arma armaPorMatricula = armaRepository.getFirstByMatriculaAndEliminadoFalse(armaDto.getMatricula());
        if(armaPorMatricula != null) {
            logger.warn("Esta arma ya se encuentra registrada por esta matricula: [{}]", armaDto.getMatricula());
            throw new AlreadyExistsArmaByMatriculaException();
        }

        logger.info("Registrando una nueva arma");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaLicenciaColectivaDto empresaLicenciaColectivaDto = empresaLicenciaColectivaService.obtenerLicenciaColectivaPorUuid(licenciaColectivaUuid, true);

        Arma arma = dtoToDaoConverter.convertDtoToDaoArma(armaDto);
        daoHelper.fulfillAuditorFields(true, arma, usuarioDto.getId());
        arma.setEmpresa(usuarioDto.getEmpresa().getId());
        arma.setClase(armaDto.getClase().getId());
        arma.setBunker(armaDto.getBunker().getId());
        arma.setMarca(armaDto.getMarca().getId());
        arma.setLicenciaColectiva(empresaLicenciaColectivaDto.getId());
        arma.setStatus(armaDto.getStatus());

        Arma armaCreada = armaRepository.save(arma);

        return daoToDtoConverter.convertDaoToDtoArma(armaCreada);
    }

    @Transactional
    @Override
    public ArmaDto modificarArma(String licenciaColectivaUuid, String armaUuid, String username, ArmaDto armaDto) {
        if(StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(armaUuid) || StringUtils.isBlank(username) || armaDto == null) {
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

        if(arma.getBunker() != armaDto.getBunker().getId()) {
            ArmaDomicilio armaDomicilio = new ArmaDomicilio();
            armaDomicilio.setArma(arma.getId());
            armaDomicilio.setDomicilioActual(armaDto.getBunker().getId());
            armaDomicilio.setDomicilioAnterior(arma.getBunker());
            daoHelper.fulfillAuditorFields(true, armaDomicilio, usuario.getId());
            armaDomicilioRepository.save(armaDomicilio);
        }

        arma.setClase(armaDto.getClase().getId());
        arma.setBunker(armaDto.getBunker().getId());
        arma.setMarca(armaDto.getMarca().getId());
        arma.setCalibre(armaDto.getCalibre());
        arma.setTipo(armaDto.getTipo());
        arma.setSerie(armaDto.getSerie());
        arma.setMatricula(armaDto.getMatricula());
        arma.setStatus(armaDto.getStatus());
        daoHelper.fulfillAuditorFields(false, arma, usuario.getId());

        armaRepository.save(arma);
        return armaDto;
    }

    @Transactional
    @Override
    public ArmaDto eliminarArma(String licenciaColectivaUuid, String armaUuid, String username, ArmaDto armaDto, MultipartFile documentoFundatorio) {
        if(StringUtils.isBlank(licenciaColectivaUuid) || StringUtils.isBlank(armaUuid) || StringUtils.isBlank(username)) {
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

        if(arma.getStatus() == ArmaStatusEnum.ACTIVA || arma.getStatus() == ArmaStatusEnum.ASIGNADA) {
            logger.warn("El arma se encuentra asignada a algun elemento. Favor de desasignarla antes de eliminarla.");
            throw new AlreadyActiveException();
        }

        // Validando si el archivo debe de venir en algun status que asi lo requiera
        if(documentoFundatorio == null && (StringUtils.equals("ROBO", armaDto.getMotivoBaja()) || StringUtils.equals("ASEGURAMIENTO", armaDto.getMotivoBaja()))) {
            logger.warn("El tipo de baja [{}] requiere un documento fundatorio", arma.getMotivoBaja());
            throw new MissingMandatoryDocumentException();
        }

        arma.setMotivoBaja(armaDto.getMotivoBaja());
        arma.setObservacionesBaja(armaDto.getObservacionesBaja());
        arma.setFechaBaja(LocalDate.parse(armaDto.getFechaBaja()));
        arma.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, arma, usuario.getId());

        if(documentoFundatorio != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(documentoFundatorio, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_ARMA, armaUuid);
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
}
