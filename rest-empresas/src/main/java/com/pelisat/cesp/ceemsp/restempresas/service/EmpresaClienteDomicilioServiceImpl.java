package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDomicilioDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.ClienteAsignacionPersonalRepository;
import com.pelisat.cesp.ceemsp.database.repository.ClienteDomicilioRepository;
import com.pelisat.cesp.ceemsp.database.repository.ClienteRepository;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaClienteDomicilioServiceImpl implements EmpresaClienteDomicilioService {
    private final Logger logger = LoggerFactory.getLogger(EmpresaClienteDomicilioServiceImpl.class);
    private final ClienteDomicilioRepository clienteDomicilioRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final CatalogoService catalogoService;
    private final PersonaRepository personaRepository;
    private final ClienteAsignacionPersonalRepository clienteAsignacionPersonalRepository;

    @Autowired
    public EmpresaClienteDomicilioServiceImpl(ClienteDomicilioRepository clienteDomicilioRepository, DaoToDtoConverter daoToDtoConverter,
                                       DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                       ClienteRepository clienteRepository, CatalogoService catalogoService,
                                              PersonaRepository personaRepository,
                                              ClienteAsignacionPersonalRepository clienteAsignacionPersonalRepository) {
        this.clienteDomicilioRepository = clienteDomicilioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
        this.catalogoService = catalogoService;
        this.personaRepository = personaRepository;
        this.clienteAsignacionPersonalRepository = clienteAsignacionPersonalRepository;
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
            dto.setCalleCatalogo(catalogoService.obtenerCallePorId(cd.getCalleCatalogo()));
            dto.setColoniaCatalogo(catalogoService.obtenerColoniaPorId(cd.getColoniaCatalogo()));
            dto.setLocalidadCatalogo(catalogoService.obtenerLocalidadPorId(cd.getLocalidadCatalogo()));
            dto.setMunicipioCatalogo(catalogoService.obtenerMunicipioPorId(cd.getMunicipioCatalogo()));
            dto.setEstadoCatalogo(catalogoService.obtenerEstadoPorId(cd.getEstadoCatalogo()));
            dto.setTipoInfraestructura(catalogoService.obtenerTipoInfraestructuraPorId(cd.getTipoInfraestructura()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClienteDomicilioDto> obtenerDomiciliosPorClienteUuid(String clienteUuid) {
        if(StringUtils.isBlank(clienteUuid)) {
            logger.warn("El uuid del cliente viene como nula o vacia");
            throw new InvalidDataException();
        }

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        return obtenerDomiciliosPorCliente(cliente.getId());
    }

    @Override
    @Transactional
    public ClienteDomicilioDto crearDomicilio(String username, String clienteUuid, ClienteDomicilioDto clienteDomicilioDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(clienteUuid) || clienteDomicilioDto == null) {
            logger.warn("Hay alguno de los parametros que no es valido");
            throw new InvalidDataException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);
        if(cliente == null) {
            logger.warn("El cliente viene como nulo en la base de datos");
            throw new NotFoundResourceException();
        }

        List<ClienteDomicilio> clienteDomicilios = clienteDomicilioRepository.getAllByClienteAndEliminadoFalse(cliente.getId());

        ClienteDomicilio c = dtoToDaoConverter.convertDtoToDaoClienteDomicilio(clienteDomicilioDto);
        daoHelper.fulfillAuditorFields(true, c, usuario.getId());
        c.setMatriz(clienteDomicilios.size() == 0);
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

        if(!cliente.isDomicilioCapturado()) {
            cliente.setDomicilioCapturado(true);
            daoHelper.fulfillAuditorFields(false, cliente, usuario.getId());
            clienteRepository.save(cliente);
        }

        ClienteDomicilioDto dto = daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilioCreado);
        dto.setCalleCatalogo(catalogoService.obtenerCallePorId(clienteDomicilioCreado.getCalleCatalogo()));
        dto.setColoniaCatalogo(catalogoService.obtenerColoniaPorId(clienteDomicilioCreado.getColoniaCatalogo()));
        dto.setLocalidadCatalogo(catalogoService.obtenerLocalidadPorId(clienteDomicilioCreado.getLocalidadCatalogo()));
        dto.setMunicipioCatalogo(catalogoService.obtenerMunicipioPorId(clienteDomicilioCreado.getMunicipioCatalogo()));
        dto.setEstadoCatalogo(catalogoService.obtenerEstadoPorId(clienteDomicilioCreado.getEstadoCatalogo()));
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
    @Transactional
    public ClienteDomicilioDto modificarDomicilio(String clienteUuid, String domicilioUuid, String username, ClienteDomicilioDto clienteDomicilioDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(clienteUuid) || clienteDomicilioDto == null) {
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

    @Transactional
    @Override
    public ClienteDomicilioDto cambiarMatriz(String clienteUuid, String domicilioUuid, String username) {
        if(StringUtils.isBlank(clienteUuid) || StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(username)) {
            logger.warn("El domicilio del cliente no existe en la base de datos");
            throw new InvalidDataException();
        }

        logger.info("Cambiando matriz del domicilio");

        ClienteDomicilio clienteDomicilio = clienteDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(clienteDomicilio == null) {
            logger.warn("El domicilio no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }

        ClienteDomicilio domicilioMatriz = clienteDomicilioRepository.findByClienteAndMatrizTrueAndEliminadoFalse(cliente.getId());

        if(domicilioMatriz == null) {
            logger.warn("El domicilio matriz no fue encontrado en la base de datos");
            throw new NotFoundResourceException();
        }
        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        domicilioMatriz.setMatriz(false);
        daoHelper.fulfillAuditorFields(false, domicilioMatriz, usuario.getId());
        clienteDomicilioRepository.save(domicilioMatriz);

        clienteDomicilio.setMatriz(true);
        daoHelper.fulfillAuditorFields(false, clienteDomicilio, usuario.getId());
        clienteDomicilioRepository.save(clienteDomicilio);

        return daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilio);
    }

    @Override
    @Transactional
    public ClienteDomicilioDto eliminarDomicilio(String clienteUuid, String domicilioUuid, String username) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(domicilioUuid) || StringUtils.isBlank(clienteUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Eliminando domicilio con el uuid [{}]", domicilioUuid);

        Cliente cliente = clienteRepository.findByUuidAndEliminadoFalse(clienteUuid);

        if(cliente == null) {
            logger.warn("El cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        ClienteDomicilio clienteDomicilio = clienteDomicilioRepository.findByUuidAndEliminadoFalse(domicilioUuid);
        if(clienteDomicilio == null || clienteDomicilio.getEliminado()) {
            logger.warn("El domicilio del cliente no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        List<ClienteDomicilio> clienteDomicilios = clienteDomicilioRepository.getAllByClienteAndEliminadoFalse(clienteDomicilio.getCliente());

        if(clienteDomicilio.isMatriz() && clienteDomicilios.size() > 1) {

            ClienteDomicilio domicilio = clienteDomicilios.stream()
                    .filter(n -> n.getId() != clienteDomicilio.getId())
                    .findFirst()
                    .orElseThrow(NotFoundResourceException::new);
            domicilio.setMatriz(true);
            daoHelper.fulfillAuditorFields(false, domicilio, usuario.getId());
            clienteDomicilioRepository.save(domicilio);
            clienteDomicilio.setMatriz(false);
        }

        if(clienteDomicilios.size() <= 1) {
            cliente.setDomicilioCapturado(false);
            daoHelper.fulfillAuditorFields(false, cliente, usuario.getId());
            clienteRepository.save(cliente);
        }

        List<Personal> personalAsignadoADomicilio = personaRepository.getAllByEmpresaAndClienteDomicilioAndEliminadoFalse(usuario.getEmpresa().getId(), clienteDomicilio.getId());
        personalAsignadoADomicilio.forEach(pad -> {
            pad.setClienteDomicilio(null);
            pad.setCliente(null);
            daoHelper.fulfillAuditorFields(false, pad, usuario.getId());

            ClienteAsignacionPersonal asignacion = clienteAsignacionPersonalRepository.getByPersonalAndEliminadoFalse(pad.getId());
            if(asignacion == null) {
                logger.warn("La asignacion de personal no existe en la base de datos");
                throw new NotFoundResourceException();
            }
            asignacion.setEliminado(true);
            daoHelper.fulfillAuditorFields(false, asignacion, usuario.getId());
            personaRepository.save(pad);
            clienteAsignacionPersonalRepository.save(asignacion);
        });

        clienteDomicilio.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, clienteDomicilio, usuario.getId());
        clienteDomicilioRepository.save(clienteDomicilio);

        return daoToDtoConverter.convertDaoToDtoClienteDomicilio(clienteDomicilio);
    }
}
