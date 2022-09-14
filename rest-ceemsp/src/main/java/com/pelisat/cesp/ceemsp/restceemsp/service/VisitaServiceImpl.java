package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoUsoDto;
import com.pelisat.cesp.ceemsp.database.dto.VisitaDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoVisitaEnum;
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

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitaServiceImpl implements VisitaService {

    private final Logger logger = LoggerFactory.getLogger(VisitaService.class);
    private final VisitaRepository visitaRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final VisitaArchivoService visitaArchivoService;
    private final CalleService calleService;
    private final ColoniaService coloniaService;
    private final LocalidadService localidadService;
    private final MunicipioService municipioService;
    private final EstadoService estadoService;

    @Autowired
    public VisitaServiceImpl(VisitaRepository visitaRepository, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                             EmpresaService empresaService, UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper,
                             VisitaArchivoService visitaArchivoService, CalleService calleService, ColoniaService coloniaService,
                             LocalidadService localidadService, MunicipioService municipioService, EstadoService estadoService) {
        this.visitaRepository = visitaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.visitaArchivoService = visitaArchivoService;
        this.calleService = calleService;
        this.coloniaService = coloniaService;
        this.localidadService = localidadService;
        this.municipioService = municipioService;
        this.estadoService = estadoService;
    }

    @Override
    public List<VisitaDto> obtenerTodas() {
        logger.info("Consultando todas las visitas en la base de datos");
        List<Visita> visitas = visitaRepository.getAllByEliminadoFalse();
        return visitas.stream()
                .map(v -> {
                    VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(v);
                    if(v.getEmpresa() != null && v.getEmpresa() > 0) {
                        visitaDto.setEmpresa(empresaService.obtenerPorId(v.getEmpresa()));
                    }
                    visitaDto.setResponsable(usuarioService.getUserById(v.getResponsable()));
                    return visitaDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitaDto> obtenerProximasVisitas() {
        logger.info("Consultando las proximas visitas en la base de datos");
        List<Visita> visitas = visitaRepository.getAllByFechaVisitaGreaterThanEqualAndEliminadoFalse(LocalDate.now());
        return visitas.stream()
                .map(v -> {
                    VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(v);
                    if(v.getEmpresa() != null && v.getEmpresa() > 0) {
                        visitaDto.setEmpresa(empresaService.obtenerPorId(v.getEmpresa()));
                    }
                    visitaDto.setResponsable(usuarioService.getUserById(v.getResponsable()));
                    return visitaDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public VisitaDto obtenerPorUuid(String uuid) {
        if(StringUtils.isBlank(uuid)) {
            logger.warn("El uuid de la visita a consultar viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        VisitaDto visitaDto = daoToDtoConverter.convertDaoToDtoVisita(visita);

        if(visita.getEmpresa() != null && visita.getEmpresa() > 0) {
            visitaDto.setEmpresa(empresaService.obtenerPorId(visita.getEmpresa()));
        }

        visitaDto.setResponsable(usuarioService.getUserById(visita.getResponsable()));
        visitaDto.setArchivos(visitaArchivoService.obtenerArchivosPorVisita(uuid));

        if(visita.getCalleCatalogo() > 0) {
            visitaDto.setCalleCatalogo(calleService.obtenerCallePorId(visita.getCalleCatalogo()));
        }
        if(visita.getColoniaCatalogo() > 0) {
            visitaDto.setColoniaCatalogo(coloniaService.obtenerColoniaPorId(visita.getColoniaCatalogo()));
        }
        if(visita.getLocalidadCatalogo() > 0) {
            visitaDto.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(visita.getLocalidadCatalogo()));
        }
        if(visita.getMunicipioCatalogo() > 0) {
            visitaDto.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(visita.getMunicipioCatalogo()));
        }
        if(visita.getEstadoCatalogo() > 0) {
            visitaDto.setEstadoCatalogo(estadoService.obtenerPorId(visita.getEstadoCatalogo()));
        }

        return visitaDto;
    }

    @Override
    public VisitaDto obtenerPorId(Integer id) {
        return null;
    }

    @Transactional
    @Override
    public VisitaDto crearNuevo(VisitaDto visitaDto, String username) {
        if(StringUtils.isBlank(username) || visitaDto == null) {
            logger.warn("El usuario o la visita a registrar viene como nula o vacia");
            throw new InvalidDataException();
        }

        logger.info("Registrando una nueva visita");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        Visita visita = dtoToDaoConverter.convertDtoToDaoVisita(visitaDto);
        if(visitaDto.getTipoVisita() == TipoVisitaEnum.ORDINARIA || (visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && visitaDto.isExisteEmpresa())) {
            visita.setEmpresa(visitaDto.getEmpresa().getId());
        }

        if(visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && !visitaDto.isExisteEmpresa()) {
            visita.setCalleCatalogo(visitaDto.getCalleCatalogo().getId());
            visita.setColoniaCatalogo(visitaDto.getColoniaCatalogo().getId());
            visita.setLocalidadCatalogo(visitaDto.getLocalidadCatalogo().getId());
            visita.setMunicipioCatalogo(visitaDto.getMunicipioCatalogo().getId());
            visita.setEstadoCatalogo(visitaDto.getEstadoCatalogo().getId());

            visita.setDomicilio1(visitaDto.getCalleCatalogo().getNombre());
            visita.setDomicilio2(visitaDto.getColoniaCatalogo().getNombre());
            visita.setLocalidad(visitaDto.getLocalidadCatalogo().getNombre());
            visita.setDomicilio3(visitaDto.getMunicipioCatalogo().getNombre());
            visita.setEstado(visitaDto.getEstadoCatalogo().getNombre());
        }

        visita.setResponsable(visitaDto.getResponsable().getId());
        visita.setFechaVisita(LocalDate.parse(visitaDto.getFechaVisita()));
        daoHelper.fulfillAuditorFields(true, visita, usuarioDto.getId());

        Visita visitaCreada = visitaRepository.save(visita);

        return daoToDtoConverter.convertDaoToDtoVisita(visitaCreada);
    }

    @Transactional
    @Override
    public VisitaDto modificarVisita(String uuid, String username, VisitaDto visitaDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || visitaDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la informacion del requerimiento en la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(visitaDto.getTipoVisita() == TipoVisitaEnum.ORDINARIA || (visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && visitaDto.isExisteEmpresa())) {
            visita.setEmpresa(visitaDto.getEmpresa().getId());
        }

        if(visitaDto.getTipoVisita() == TipoVisitaEnum.EXTRAORDINARIA && !visitaDto.isExisteEmpresa()) {
            visita.setCalleCatalogo(visitaDto.getCalleCatalogo().getId());
            visita.setColoniaCatalogo(visitaDto.getColoniaCatalogo().getId());
            visita.setLocalidadCatalogo(visitaDto.getLocalidadCatalogo().getId());
            visita.setMunicipioCatalogo(visitaDto.getMunicipioCatalogo().getId());
            visita.setEstadoCatalogo(visitaDto.getEstadoCatalogo().getId());

            visita.setDomicilio1(visitaDto.getCalleCatalogo().getNombre());
            visita.setDomicilio2(visitaDto.getColoniaCatalogo().getNombre());
            visita.setLocalidad(visitaDto.getLocalidadCatalogo().getNombre());
            visita.setDomicilio3(visitaDto.getMunicipioCatalogo().getNombre());
            visita.setEstado(visitaDto.getEstadoCatalogo().getNombre());

            visita.setNumeroExterior(visitaDto.getNumeroExterior());
            visita.setNumeroInterior(visitaDto.getNumeroInterior());
        }
        visita.setNombreComercial(visitaDto.getNombreComercial());
        visita.setRazonSocial(visitaDto.getRazonSocial());
        visita.setObservaciones(visitaDto.getObservaciones());

        visita.setFechaVisita(LocalDate.parse(visitaDto.getFechaVisita()));
        visita.setTipoVisita(visitaDto.getTipoVisita());
        visita.setNumeroRegistro(visitaDto.getNumeroRegistro());
        visita.setNumeroOrden(visitaDto.getNumeroOrden());
        visita.setResponsable(visitaDto.getResponsable().getId());

        daoHelper.fulfillAuditorFields(false, visita, usuarioDto.getId());
        visitaRepository.save(visita);
        return daoToDtoConverter.convertDaoToDtoVisita(visita);
    }

    @Transactional
    @Override
    public VisitaDto eliminarVisita(String uuid, String username) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        visita.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, visita, usuarioDto.getId());
        visitaRepository.save(visita);
        return daoToDtoConverter.convertDaoToDtoVisita(visita);
    }

    @Transactional
    @Override
    public VisitaDto modificarRequerimiento(String uuid, String username, VisitaDto visitaDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || visitaDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Modificando la informacion del requerimiento en la visita con el uuid [{}]", uuid);

        Visita visita = visitaRepository.findByUuidAndEliminadoFalse(uuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        if(visita == null) {
            logger.warn("La visita no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(visitaDto.isRequerimiento()) {
            visita.setRequerimiento(true);
            visita.setFechaTermino(LocalDate.parse(visitaDto.getFechaTermino()));
            visita.setDetallesRequerimiento(visitaDto.getDetallesRequerimiento());
        } else {
            visita.setRequerimiento(false);
            visita.setFechaTermino(null);
            visita.setDetallesRequerimiento(null);
        }
        daoHelper.fulfillAuditorFields(false, visita, usuarioDto.getId());
        visitaRepository.save(visita);
        return daoToDtoConverter.convertDaoToDtoVisita(visita);
    }


}
