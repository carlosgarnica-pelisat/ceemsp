package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.CanDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Can;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.repository.CanRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.smartcardio.CommandAPDU;
import javax.transaction.Transactional;
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
    private final PersonaService personaService;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final ClienteService clienteService;
    private final ClienteDomicilioService clienteDomicilioService;
    private final CanRazaService canRazaService;
    private final CanCartillaVacunacionService canCartillaVacunacionService;
    private final CanConstanciaSaludService canConstanciaSaludService;
    private final CanAdiestramientoService canAdiestramientoService;

    @Autowired
    public CanServiceImpl(CanRepository canRepository, EmpresaService empresaService, UsuarioService usuarioService,
                          DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                          EmpresaDomicilioService empresaDomicilioService, PersonaService personaService,
                          ClienteService clienteService, ClienteDomicilioService clienteDomicilioService,
                          CanRazaService canRazaService, CanCartillaVacunacionService canCartillaVacunacionService,
                          CanConstanciaSaludService canConstanciaSaludService, CanAdiestramientoService canAdiestramientoService) {
        this.canRepository = canRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.personaService = personaService;
        this.empresaDomicilioService = empresaDomicilioService;
        this.clienteDomicilioService = clienteDomicilioService;
        this.clienteService = clienteService;
        this.canRazaService = canRazaService;
        this.canCartillaVacunacionService = canCartillaVacunacionService;
        this.canConstanciaSaludService = canConstanciaSaludService;
        this.canAdiestramientoService = canAdiestramientoService;
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

        return canes.stream().map(daoToDtoConverter::convertDaoToDtoCan).collect(Collectors.toList());
    }

    @Override
    public CanDto obtenerCanPorUuid(String empresaUuid, String canUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid)) {
            logger.warn("El uuid de la empresa o del can vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el can con el uuid [{}]", canUuid);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        CanDto canDto = daoToDtoConverter.convertDaoToDtoCan(can);

        if(!soloEntidad) {
            canDto.setRaza(canRazaService.obtenerPorId(can.getRaza()));
            canDto.setDomicilioAsignado(empresaDomicilioService.obtenerPorId(can.getDomicilioAsignado()));
            if(can.getElementoAsignado() != null && can.getElementoAsignado() > 0) {
                canDto.setElementoAsignado(personaService.obtenerPorId(empresaUuid, can.getElementoAsignado()));
            }

            if(can.getClienteAsignado() != null && can.getClienteAsignado() > 0) {
                canDto.setClienteAsignado(clienteService.obtenerClientePorId(can.getClienteAsignado()));
            }

            if(can.getDomicilioClienteAsignado() != null && can.getClienteAsignado() > 0) {
                canDto.setClienteDomicilio(clienteDomicilioService.obtenerPorId(can.getDomicilioClienteAsignado()));
            }

            canDto.setCartillasVacunacion(canCartillaVacunacionService.obtenerCartillasVacunacionPorCanUuid(empresaUuid, canUuid));
            canDto.setAdiestramientos(canAdiestramientoService.obtenerAdiestramientosPorCanUuid(empresaUuid, canUuid));
            canDto.setConstanciasSalud(canConstanciaSaludService.obtenerConstanciasSaludPorCanUuid(empresaUuid, canUuid));
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

        Can canCreado = canRepository.save(can);

        return daoToDtoConverter.convertDaoToDtoCan(canCreado);
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

    @Override
    public CanDto eliminarCan(String empresaUuid, String canUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(canUuid) || StringUtils.isBlank(username)) {
            logger.warn("Algunos de los parametros vienen como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        Can can = canRepository.getByUuidAndEliminadoFalse(canUuid);
        if(can == null) {
            logger.warn("El can no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        can.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, can, usuarioDto.getId());
        canRepository.save(can);

        return daoToDtoConverter.convertDaoToDtoCan(can);
    }
}
