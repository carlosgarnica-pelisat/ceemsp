package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import com.pelisat.cesp.ceemsp.database.dto.TipoInfraestructuraDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Cliente;
import com.pelisat.cesp.ceemsp.database.model.ClienteDomicilio;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.ClienteDomicilioRepository;
import com.pelisat.cesp.ceemsp.database.repository.ClienteRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteDomicilioServiceImpl implements ClienteDomicilioService {

    private final Logger logger = LoggerFactory.getLogger(ClienteDomicilioService.class);
    private final ClienteDomicilioRepository clienteDomicilioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final TipoInfraestructuraService tipoInfraestructuraService;
    private final EstadoService estadoService;
    private final MunicipioService municipioService;
    private final ColoniaService coloniaService;
    private final LocalidadService localidadService;
    private final CalleService calleService;

    @Autowired
    public ClienteDomicilioServiceImpl(ClienteDomicilioRepository clienteDomicilioRepository, DaoToDtoConverter daoToDtoConverter,
                                   DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                   ClienteRepository clienteRepository, TipoInfraestructuraService tipoInfraestructuraService,
                                       EstadoService estadoService, MunicipioService municipioService, LocalidadService localidadService,
                                   ColoniaService coloniaService, CalleService calleService) {
        this.clienteDomicilioRepository = clienteDomicilioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
        this.tipoInfraestructuraService = tipoInfraestructuraService;
        this.estadoService = estadoService;
        this.municipioService = municipioService;
        this.localidadService = localidadService;
        this.coloniaService = coloniaService;
        this.calleService = calleService;
    }

    @Override
    public List<ClienteDomicilioDto> obtenerDomiciliosPorCliente(int clienteId) {
        if(clienteId < 1) {
            logger.warn("El id del cliente viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los domicilios del cliente con id [{}]", clienteId);

        List<ClienteDomicilio> clienteDomicilios = clienteDomicilioRepository.getAllByClienteAndEliminadoFalse(clienteId);

        return clienteDomicilios.stream().map(cd -> {
            ClienteDomicilioDto dto = daoToDtoConverter.convertDaoToDtoClienteDomicilio(cd);
            dto.setCalleCatalogo(calleService.obtenerCallePorId(cd.getCalleCatalogo()));
            dto.setColoniaCatalogo(coloniaService.obtenerColoniaPorId(cd.getColoniaCatalogo()));
            dto.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(cd.getLocalidadCatalogo()));
            dto.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(cd.getMunicipioCatalogo()));
            dto.setEstadoCatalogo(estadoService.obtenerPorId(cd.getEstadoCatalogo()));
            dto.setTipoInfraestructura(tipoInfraestructuraService.obtenerTipoInfraestructuraPorId(cd.getTipoInfraestructura()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClienteDomicilioDto> obtenerDomiciliosPorClienteUuid(String empresaUuid, String clienteUuid) {
        if(StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid del cliente viene como nula o vacia");
            throw new InvalidDataException();
        }

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        return obtenerDomiciliosPorCliente(cliente.getId());
    }

    @Override
    public ClienteDomicilioDto crearDomicilio(String username, String empresaUuid, String clienteUuid, ClienteDomicilioDto clienteDomicilioDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(clienteUuid) || clienteDomicilioDto == null) {
            logger.warn("Hay alguno de los parametros que no es valido");
            throw new InvalidDataException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);
        if(cliente == null) {
            logger.warn("El cliente viene como nulo en la base de datos");
            throw new NotFoundResourceException();
        }


        ClienteDomicilio c = dtoToDaoConverter.convertDtoToDaoClienteDomicilio(clienteDomicilioDto);
        daoHelper.fulfillAuditorFields(true, c, usuario.getId());
        c.setCliente(cliente.getId());
        c.setTipoInfraestructura(clienteDomicilioDto.getTipoInfraestructura().getId());
        c.setEstadoCatalogo(clienteDomicilioDto.getEstadoCatalogo().getId());
        c.setMunicipioCatalogo(clienteDomicilioDto.getMunicipioCatalogo().getId());
        c.setLocalidadCatalogo(clienteDomicilioDto.getLocalidadCatalogo().getId());
        c.setColoniaCatalogo(clienteDomicilioDto.getColoniaCatalogo().getId());
        c.setCalleCatalogo(clienteDomicilioDto.getCalleCatalogo().getId());

        c.setDomicilio1(clienteDomicilioDto.getCalleCatalogo().getNombre());
        c.setDomicilio2(clienteDomicilioDto.getColoniaCatalogo().getNombre());
        c.setLocalidad(clienteDomicilioDto.getLocalidadCatalogo().getNombre());
        c.setDomicilio3(clienteDomicilioDto.getMunicipioCatalogo().getNombre());
        c.setEstado(clienteDomicilioDto.getEstadoCatalogo().getNombre());

        ClienteDomicilio clienteDomicilioCreado = clienteDomicilioRepository.save(c);

        ClienteDomicilioDto dto = daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilioCreado);
        dto.setCalleCatalogo(calleService.obtenerCallePorId(clienteDomicilioCreado.getCalleCatalogo()));
        dto.setColoniaCatalogo(coloniaService.obtenerColoniaPorId(clienteDomicilioCreado.getColoniaCatalogo()));
        dto.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(clienteDomicilioCreado.getLocalidadCatalogo()));
        dto.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(clienteDomicilioCreado.getMunicipioCatalogo()));
        dto.setEstadoCatalogo(estadoService.obtenerPorId(clienteDomicilioCreado.getEstadoCatalogo()));
        dto.setTipoInfraestructura(clienteDomicilioDto.getTipoInfraestructura());
        return dto;
    }

    @Override
    public ClienteDomicilioDto obtenerPorId(Integer id) {
        if(id == null || id < 1) {
            logger.warn("El id viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el domicilio con el id [{}]", id);

        ClienteDomicilio clienteDomicilio = clienteDomicilioRepository.getOne(id);
        if(clienteDomicilio == null || clienteDomicilio.getEliminado()) {
            logger.warn("El domicilio del cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilio);
    }

    @Override
    public ClienteDomicilioDto modificarDomicilio(String empresaUuid, String clienteUuid, String domicilioUuid, String username, ClienteDomicilioDto clienteDomicilioDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) | StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(clienteUuid) || clienteDomicilioDto == null) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando domicilio con el uuid [{}]", domicilioUuid);

        ClienteDomicilio clienteDomicilio = clienteDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(clienteDomicilio == null || clienteDomicilio.getEliminado()) {
            logger.warn("El domicilio del cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        clienteDomicilio.setNombre(clienteDomicilioDto.getNombre());
        clienteDomicilio.setNumeroExterior(clienteDomicilioDto.getNumeroExterior());
        clienteDomicilio.setNumeroInterior(clienteDomicilioDto.getNumeroInterior());
        clienteDomicilio.setDomicilio4(clienteDomicilioDto.getDomicilio4());
        clienteDomicilio.setPais(clienteDomicilioDto.getPais());
        clienteDomicilio.setCodigoPostal(clienteDomicilioDto.getCodigoPostal());
        clienteDomicilio.setTelefonoFijo(clienteDomicilioDto.getTelefonoFijo());
        clienteDomicilio.setTelefonoMovil(clienteDomicilioDto.getTelefonoMovil());
        clienteDomicilio.setContacto(clienteDomicilioDto.getContacto());
        clienteDomicilio.setApellidoPaternoContacto(clienteDomicilioDto.getApellidoPaternoContacto());
        clienteDomicilio.setApellidoMaternoContacto(clienteDomicilioDto.getApellidoMaternoContacto());
        clienteDomicilio.setCorreoElectronico(clienteDomicilioDto.getCorreoElectronico());

        clienteDomicilio.setTipoInfraestructura(clienteDomicilioDto.getTipoInfraestructura().getId());
        clienteDomicilio.setEstadoCatalogo(clienteDomicilioDto.getEstadoCatalogo().getId());
        clienteDomicilio.setMunicipioCatalogo(clienteDomicilioDto.getMunicipioCatalogo().getId());
        clienteDomicilio.setLocalidadCatalogo(clienteDomicilioDto.getLocalidadCatalogo().getId());
        clienteDomicilio.setColoniaCatalogo(clienteDomicilioDto.getColoniaCatalogo().getId());
        clienteDomicilio.setCalleCatalogo(clienteDomicilioDto.getCalleCatalogo().getId());

        clienteDomicilio.setDomicilio1(clienteDomicilioDto.getCalleCatalogo().getNombre());
        clienteDomicilio.setDomicilio2(clienteDomicilioDto.getColoniaCatalogo().getNombre());
        clienteDomicilio.setLocalidad(clienteDomicilioDto.getLocalidadCatalogo().getNombre());
        clienteDomicilio.setDomicilio3(clienteDomicilioDto.getMunicipioCatalogo().getNombre());
        clienteDomicilio.setEstado(clienteDomicilioDto.getEstadoCatalogo().getNombre());

        daoHelper.fulfillAuditorFields(false, clienteDomicilio, usuario.getId());

        clienteDomicilioRepository.save(clienteDomicilio);

        return daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilio);
    }

    @Override
    public ClienteDomicilioDto eliminarDomicilio(String empresaUuid, String clienteUuid, String domicilioUuid, String username) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) | StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Eliminando domicilio con el uuid [{}]", domicilioUuid);

        ClienteDomicilio clienteDomicilio = clienteDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(clienteDomicilio == null || clienteDomicilio.getEliminado()) {
            logger.warn("El domicilio del cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        clienteDomicilio.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, clienteDomicilio, usuario.getId());
        clienteDomicilioRepository.save(clienteDomicilio);

        return daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilio);
    }
}
