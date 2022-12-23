package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaCanServiceImpl implements EmpresaCanService {
    private final Logger logger = LoggerFactory.getLogger(EmpresaCanServiceImpl.class);
    private final CanRepository canRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final EmpresaClienteService empresaClienteService;
    private final EmpresaClienteDomicilioService empresaClienteDomicilioService;
    private final CatalogoService catalogoService;
    private final EmpresaCanCartillaVacunacionService empresaCanCartillaVacunacionService;
    private final EmpresaCanConstanciaSaludService empresaCanConstanciaSaludService;
    private final EmpresaCanAdiestramientoService empresaCanAdiestramientoService;
    private final EmpresaPersonalService empresaPersonalService;
    private final EmpresaCanFotografiaService empresaCanFotografiaService;

    @Autowired
    public EmpresaCanServiceImpl(CanRepository canRepository, UsuarioService usuarioService,
                                 DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                 DaoHelper<CommonModel> daoHelper, EmpresaDomicilioService empresaDomicilioService,
                                 CatalogoService catalogoService, ArchivosService archivosService,
                                 EmpresaPersonalService empresaPersonalService, EmpresaClienteService empresaClienteService,
                                 EmpresaClienteDomicilioService empresaClienteDomicilioService, EmpresaCanCartillaVacunacionService empresaCanCartillaVacunacionService,
                                 EmpresaCanConstanciaSaludService empresaCanConstanciaSaludService, EmpresaCanAdiestramientoService empresaCanAdiestramientoService,
                                 EmpresaCanFotografiaService empresaCanFotografiaService) {
        this.canRepository = canRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaDomicilioService = empresaDomicilioService;
        this.catalogoService = catalogoService;
        this.archivosService = archivosService;
        this.empresaPersonalService = empresaPersonalService;
        this.empresaClienteService = empresaClienteService;
        this.empresaClienteDomicilioService = empresaClienteDomicilioService;
        this.empresaCanCartillaVacunacionService = empresaCanCartillaVacunacionService;
        this.empresaCanAdiestramientoService = empresaCanAdiestramientoService;
        this.empresaCanConstanciaSaludService = empresaCanConstanciaSaludService;
        this.empresaCanFotografiaService = empresaCanFotografiaService;
    }

    @Override
    public List<CanDto> obtenerCanesPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("el uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los canes para el usuario", username);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Can> canes = canRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return canes.stream().map(daoToDtoConverter::convertDaoToDtoCan).collect(Collectors.toList());
    }

    @Override
    public CanDto obtenerCanPorUuid(String canUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el can con el uuid [{}]", canUuid);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        CanDto canDto = daoToDtoConverter.convertDaoToDtoCan(can);

        if(!soloEntidad) {
            canDto.setRaza(catalogoService.obtenerCanRazaPorId(can.getRaza()));
            canDto.setDomicilioAsignado(empresaDomicilioService.obtenerPorId(can.getDomicilioAsignado()));

            if(can.getClienteAsignado() != null && can.getClienteAsignado() > 0) {
                canDto.setClienteAsignado(empresaClienteService.obtenerClientePorId(can.getClienteAsignado()));
            }

            if(can.getDomicilioClienteAsignado() != null && can.getClienteAsignado() > 0) {
                canDto.setClienteDomicilio(empresaClienteDomicilioService.obtenerPorId(can.getDomicilioClienteAsignado()));
            }

            canDto.setCartillasVacunacion(empresaCanCartillaVacunacionService.obtenerCartillasVacunacionPorCanUuid(canUuid));
            canDto.setAdiestramientos(empresaCanAdiestramientoService.obtenerAdiestramientosPorCanUuid(canUuid));
            canDto.setConstanciasSalud(empresaCanConstanciaSaludService.obtenerConstanciasSaludPorCanUuid(canUuid));
            canDto.setFotografias(empresaCanFotografiaService.mostrarCanFotografias(canUuid));
        }

        return canDto;
    }

    @Override
    public CanDto obtenerCanPorId(int id) {
        if(id < 1) {
            logger.warn("El id a consultar no es valido");
            throw new InvalidDataException();
        }

        logger.info("Consultando el can con el id [{}]", id);

        Can can = canRepository.getOne(id);

        if(can == null || can.getEliminado()) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoCan(can);
    }

    @Transactional
    @Override
    public CanDto guardarCan(String username, CanDto canDto) {
        if(StringUtils.isBlank(username) || canDto == null) {
            logger.warn("El uuid de la empresa, el usuario o el can a guardar vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Guardando nuevo can en la base de datos");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Can can = dtoToDaoConverter.convertDtoToDaoCan(canDto);
        daoHelper.fulfillAuditorFields(true, can, usuarioDto.getId());
        can.setFechaIngreso(LocalDate.parse(canDto.getFechaIngreso()));
        can.setEmpresa(usuarioDto.getEmpresa().getId());
        can.setRaza(canDto.getRaza().getId());
        can.setDomicilioAsignado(canDto.getDomicilioAsignado().getId());

        Can canCreado = canRepository.save(can);

        return daoToDtoConverter.convertDaoToDtoCan(canCreado);
    }

    @Transactional
    @Override
    public CanDto modificarCan(String canUuid, String username, CanDto canDto) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canDto == null) {
            logger.warn("Algunos de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        if(can == null) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
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

        daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());
        canRepository.save(can);

        return daoToDtoConverter.convertDaoToDtoCan(can);
    }

    @Transactional
    @Override
    public CanDto eliminarCan(String canUuid, String username, CanDto canDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(canUuid) || StringUtils.isBlank(username) || canDto == null) {
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
        can.setFechaBaja(LocalDate.parse(canDto.getFechaBaja()));
        can.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_CAN, usuarioDto.getEmpresa().getUuid());
                can.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        canRepository.save(can);

        return daoToDtoConverter.convertDaoToDtoCan(can);
    }
}
