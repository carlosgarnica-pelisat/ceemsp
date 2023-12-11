package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.CanDomicilioRepository;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonalCanRepository;
import com.pelisat.cesp.ceemsp.database.type.CanOrigenEnum;
import com.pelisat.cesp.ceemsp.database.type.CanStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanServiceImpl implements CanService {

    private final Logger logger = LoggerFactory.getLogger(CanService.class);
    private final CanRepository canRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final PersonaRepository personaRepository;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final ClienteService clienteService;
    private final ClienteDomicilioService clienteDomicilioService;
    private final CanRazaService canRazaService;
    private final CanCartillaVacunacionService canCartillaVacunacionService;
    private final CanConstanciaSaludService canConstanciaSaludService;
    private final CanAdiestramientoService canAdiestramientoService;
    private final CanFotografiaService canFotografiaService;
    private final ArchivosService archivosService;
    private final PersonalCanRepository personalCanRepository;
    private final CanDomicilioRepository canDomicilioRepository;

    @Autowired
    public CanServiceImpl(CanRepository canRepository, EmpresaService empresaService, UsuarioService usuarioService,
                          DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                          EmpresaDomicilioService empresaDomicilioService, PersonaRepository personaRepository,
                          ClienteService clienteService, ClienteDomicilioService clienteDomicilioService,
                          CanRazaService canRazaService, CanCartillaVacunacionService canCartillaVacunacionService,
                          CanConstanciaSaludService canConstanciaSaludService, CanAdiestramientoService canAdiestramientoService,
                          CanFotografiaService canFotografiaService, ArchivosService archivosService,
                          PersonalCanRepository personalCanRepository, CanDomicilioRepository canDomicilioRepository) {
        this.canRepository = canRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.personaRepository = personaRepository;
        this.empresaDomicilioService = empresaDomicilioService;
        this.clienteDomicilioService = clienteDomicilioService;
        this.clienteService = clienteService;
        this.canRazaService = canRazaService;
        this.canCartillaVacunacionService = canCartillaVacunacionService;
        this.canConstanciaSaludService = canConstanciaSaludService;
        this.canAdiestramientoService = canAdiestramientoService;
        this.canFotografiaService = canFotografiaService;
        this.archivosService = archivosService;
        this.personalCanRepository = personalCanRepository;
        this.canDomicilioRepository = canDomicilioRepository;
    }

    @Override
    public List<CanDto> obtenerCanesPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("el uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los canes con el uuid [{}]", empresaUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Can> canes = canRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return canes.stream().map(c -> {
            CanDto canDto = daoToDtoConverter.convertDaoToDtoCan(c);
            canDto.setRaza(canRazaService.obtenerPorId(c.getRaza()));
            if(canDto.getStatus() == CanStatusEnum.ACTIVO) {
                Personal personalAsignado = personaRepository.getByCanAndEliminadoFalse(c.getId());
                if(personalAsignado != null) {
                    canDto.setElementoAsignado(daoToDtoConverter.convertDaoToDtoPersona(personalAsignado));
                }
            }
            return canDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CanDto> obtenerCanesEliminadosPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("el uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los canes con el uuid [{}]", empresaUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Can> canes = canRepository.getAllByEmpresaAndEliminadoTrue(empresaDto.getId());

        return canes.stream().map(daoToDtoConverter::convertDaoToDtoCan).collect(Collectors.toList());
    }

    @Override
    public List<CanDto> obtenerCanesEnInstalacionesPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los canes con estatus en INSTALACIONES para la empresa [{}]", empresaUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<Can> canes = canRepository.getAllByEmpresaAndStatus(empresaDto.getId(), CanStatusEnum.INSTALACIONES);

        return canes.stream().map(daoToDtoConverter::convertDaoToDtoCan).collect(Collectors.toList());
    }

    @Override
    public CanDto obtenerCanPorUuid(String empresaUuid, String canUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el can con el uuid [{}]", canUuid);
        Can can = canRepository.getByUuid(canUuid);
        CanDto canDto = daoToDtoConverter.convertDaoToDtoCan(can);

        if(!soloEntidad) {
            canDto.setRaza(canRazaService.obtenerPorId(can.getRaza()));
            canDto.setDomicilioAsignado(empresaDomicilioService.obtenerPorId(can.getDomicilioAsignado()));

            if(can.getClienteAsignado() != null && can.getClienteAsignado() > 0) {
                canDto.setClienteAsignado(clienteService.obtenerClientePorId(can.getClienteAsignado()));
            }

            if(can.getDomicilioClienteAsignado() != null && can.getClienteAsignado() > 0) {
                canDto.setClienteDomicilio(clienteDomicilioService.obtenerPorId(can.getDomicilioClienteAsignado()));
            }

            canDto.setCartillasVacunacion(canCartillaVacunacionService.obtenerCartillasVacunacionPorCanUuid(empresaUuid, canUuid));
            canDto.setAdiestramientos(canAdiestramientoService.obtenerAdiestramientosPorCanUuid(empresaUuid, canUuid));
            canDto.setConstanciasSalud(canConstanciaSaludService.obtenerConstanciasSaludPorCanUuid(empresaUuid, canUuid));
            canDto.setFotografias(canFotografiaService.mostrarCanFotografias(empresaUuid, canUuid));

            if(canDto.getStatus() == CanStatusEnum.ACTIVO) {
                Personal personalAsignado = personaRepository.getByCanAndEliminadoFalse(can.getId());
                if(personalAsignado != null) {
                    canDto.setElementoAsignado(daoToDtoConverter.convertDaoToDtoPersona(personalAsignado));
                }
            }
        }

        return canDto;
    }

    @Override
    public File descargarDocumentoFundatorio(String empresaUuid, String canUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando el documento fundatorio para el can [{}]", canUuid);

        Can can = canRepository.getByUuid(canUuid);

        if(can == null) {
            logger.warn("El can no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        if(!can.getEliminado()) {
            logger.warn("El can no esta eliminado. Esta funcion no es compatible");
            throw new NotFoundResourceException();
        }

        return new File(can.getDocumentoFundatorioBaja());
    }

    @Override
    public CanDto obtenerCanPorId(int id) {
        if(id < 1) {
            logger.warn("El id a consultar no es valido");
            throw new InvalidDataException();
        }

        logger.info("Consultando el can con el id [{}]", id);

        Can can = canRepository.getOne(id);

        CanDto canDto = daoToDtoConverter.convertDaoToDtoCan(can);

        canDto.setRaza(canRazaService.obtenerPorId(can.getRaza()));
        canDto.setDomicilioAsignado(empresaDomicilioService.obtenerPorId(can.getDomicilioAsignado()));

        if(can.getClienteAsignado() != null && can.getClienteAsignado() > 0) {
            canDto.setClienteAsignado(clienteService.obtenerClientePorId(can.getClienteAsignado()));
        }

        if(can.getDomicilioClienteAsignado() != null && can.getClienteAsignado() > 0) {
            canDto.setClienteDomicilio(clienteDomicilioService.obtenerPorId(can.getDomicilioClienteAsignado()));
        }

        return canDto;
    }

    @Transactional
    @Override
    public CanDto guardarCan(String empresaUuid, String username, CanDto canDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(username) || canDto == null) {
            logger.warn("El uuid de la empresa, el usuario o el can a guardar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nuevo can en la base de datos");

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Can can = dtoToDaoConverter.convertDtoToDaoCan(canDto);
        daoHelper.fulfillAuditorFields(true, can, usuarioDto.getId());
        can.setFechaIngreso(LocalDate.parse(canDto.getFechaIngreso()));
        can.setEmpresa(empresaDto.getId());
        can.setRaza(canDto.getRaza().getId());
        can.setDomicilioAsignado(canDto.getDomicilioAsignado().getId());
        can.setFotografiaCapturada(false);

        if(canDto.getOrigen() != CanOrigenEnum.PROPIO) {
            can.setFechaInicio(LocalDate.parse(canDto.getFechaInicio()));
            can.setFechaFin(LocalDate.parse(canDto.getFechaFin()));
        }

        if(StringUtils.equals(canDto.getRaza().getNombre(), "Otro")) {
            can.setRazaOtro(canDto.getRazaOtro());
        }

        Can canCreado = canRepository.save(can);

        CanDto response = daoToDtoConverter.convertDaoToDtoCan(canCreado);

        response.setRaza(canDto.getRaza());
        response.setDomicilioAsignado(canDto.getDomicilioAsignado());
        return response;
    }

    @Transactional
    @Override
    public CanDto modificarCan(String empresaUuid, String canUuid, String username, CanDto canDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canDto == null) {
            logger.warn("Algunos de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        if(can == null) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(can.getDomicilioAsignado() != canDto.getDomicilioAsignado().getId()) {
            CanDomicilio canDomicilio = new CanDomicilio();
            canDomicilio.setCan(can.getId());
            canDomicilio.setDomicilioActual(canDto.getDomicilioAsignado().getId());
            canDomicilio.setDomicilioAnterior(can.getDomicilioAsignado());
            daoHelper.fulfillAuditorFields(true, canDomicilio, usuarioDto.getId());
            canDomicilioRepository.save(canDomicilio);
        }

        can.setNombre(canDto.getNombre());
        can.setGenero(canDto.getGenero());
        can.setRaza(canDto.getRaza().getId());
        can.setRazaOtro(canDto.getRazaOtro());
        can.setFechaIngreso(LocalDate.parse(canDto.getFechaIngreso()));
        can.setDomicilioAsignado(canDto.getDomicilioAsignado().getId());
        can.setEdad(canDto.getEdad());
        can.setPeso(canDto.getPeso());
        can.setChip(canDto.isChip());
        can.setTatuaje(canDto.isTatuaje());
        can.setDescripcion(canDto.getDescripcion());

        can.setRaza(canDto.getRaza().getId());
        can.setDomicilioAsignado(canDto.getDomicilioAsignado().getId());
        can.setOrigen(canDto.getOrigen());
        can.setRazonSocial(canDto.getRazonSocial());
        can.setStatus(canDto.getStatus());
        if(StringUtils.isNotBlank(canDto.getFechaInicio())) {
            can.setFechaInicio(LocalDate.parse(canDto.getFechaInicio()));
        }
        if(StringUtils.isNotBlank(canDto.getFechaFin())) {
            can.setFechaFin(LocalDate.parse(canDto.getFechaFin()));
        }

        if(can.getStatus() != CanStatusEnum.ACTIVO) {
            can.setElementoAsignado(null);
        }

        daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());
        canRepository.save(can);

        return daoToDtoConverter.convertDaoToDtoCan(can);
    }

    @Override
    @Transactional
    public CanDto eliminarCan(String empresaUuid, String canUuid, String username, CanDto canDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canDto == null) {
            logger.warn("Algunos de los parametros vienen como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        if(can == null) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        can.setMotivoBaja(canDto.getMotivoBaja());
        can.setObservacionesBaja(canDto.getObservacionesBaja());
        can.setFechaBaja(LocalDate.now());
        can.setEliminado(true);
        can.setStatus(CanStatusEnum.BAJA);
        daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_CAN, empresaUuid);
                can.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        canRepository.save(can);

        return daoToDtoConverter.convertDaoToDtoCan(can);
    }

    @Override
    public List<PersonalCanDto> obtenerMovimientosCan(String uuid, String canUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Can can = canRepository.getByUuid(canUuid);

        if(can == null) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<PersonalCan> movimientosCan = personalCanRepository.getAllByCan(can.getId());

        return movimientosCan.stream().map(movimiento -> {
            PersonalCanDto pad = new PersonalCanDto();
            Personal personal = personaRepository.getOne(movimiento.getPersonal());
            pad.setObservaciones(movimiento.getObservaciones());
            pad.setPersona(daoToDtoConverter.convertDaoToDtoPersona(personal));
            pad.setFechaCreacion(movimiento.getFechaCreacion().toString());
            pad.setFechaActualizacion(movimiento.getFechaActualizacion().toString());
            pad.setMotivoBajaAsignacion(movimiento.getMotivoBajaAsignacion());
            pad.setEliminado(movimiento.getEliminado());
            return pad;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CanDomicilioDto> obtenerMovimientosDomicilioCan(String uuid, String canUuid) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Can can = canRepository.getByUuid(canUuid);

        if(can == null) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<CanDomicilio> canDomicilios = canDomicilioRepository.getAllByCan(can.getId());

        return canDomicilios.stream().map(movimiento -> {
            CanDomicilioDto canDomicilioDto = new CanDomicilioDto();
            canDomicilioDto.setId(movimiento.getId());
            canDomicilioDto.setUuid(movimiento.getUuid());
            canDomicilioDto.setDomicilioAnterior(empresaDomicilioService.obtenerPorId(movimiento.getDomicilioAnterior()));
            canDomicilioDto.setDomicilioActual(empresaDomicilioService.obtenerPorId(movimiento.getDomicilioActual()));
            canDomicilioDto.setFechaCreacion(movimiento.getFechaCreacion().toString());
            canDomicilioDto.setFechaActualizacion(movimiento.getFechaActualizacion().toString());
            return canDomicilioDto;
        }).collect(Collectors.toList());
    }
}
