package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEquipo;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEquipoMovimiento;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEquipoMovimientoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEquipoRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
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
public class EmpresaEquipoServiceImpl implements EmpresaEquipoService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaEquipoRepository empresaEquipoRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaEquipoMovimientoRepository empresaEquipoMovimientoRepository;
    private final CatalogoService catalogoService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaEquipoService.class);

    @Autowired
    public EmpresaEquipoServiceImpl(
            DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
            EmpresaEquipoRepository empresaEquipoRepository, EmpresaService empresaService,
            UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper,
            EmpresaEquipoMovimientoRepository empresaEquipoMovimientoRepository, CatalogoService catalogoService
    ) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaEquipoRepository = empresaEquipoRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.empresaEquipoMovimientoRepository = empresaEquipoMovimientoRepository;
        this.catalogoService = catalogoService;
    }

    @Override
    public List<EmpresaEquipoDto> obtenerEquiposPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<EmpresaEquipo> empresaEquipos = empresaEquipoRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return empresaEquipos.stream().map(u -> {
            EmpresaEquipoDto empresaEquipoDto = daoToDtoConverter.convertDaoToDtoEmpresaEquipo(u);
            empresaEquipoDto.setEquipo(catalogoService.obtenerEquipoPorId(u.getEquipo()));
            return empresaEquipoDto;
        }).collect(Collectors.toList());
    }

    @Override
    public EmpresaEquipoDto obtenerEquipoPorUuid(String equipoUuid, String username) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(equipoUuid)) {
            logger.warn("El uuid de la empresa o del equipo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el equipo con el uuid [{}]", equipoUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaEquipo empresaEquipo = empresaEquipoRepository.findByUuidAndEliminadoFalse(equipoUuid);
        if(empresaEquipo == null) {
            logger.warn("El equipo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(empresaEquipo.getEmpresa() != usuarioDto.getEmpresa().getId()) {
            logger.warn("El equipo a consultar no pertenece a este recurso");
            throw new MissingRelationshipException();
        }

        List<EmpresaEquipoMovimiento> movimientos = empresaEquipoMovimientoRepository.getAllByEmpresaEquipoAndEliminadoFalse(empresaEquipo.getId());

        EmpresaEquipoDto empresaEquipoDto = daoToDtoConverter.convertDaoToDtoEmpresaEquipo(empresaEquipo);
        empresaEquipoDto.setEquipo(catalogoService.obtenerEquipoPorId(empresaEquipo.getEquipo()));
        empresaEquipoDto.setMovimientos(movimientos.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEquipoMovimiento).collect(Collectors.toList()));
        return empresaEquipoDto;
    }

    @Override
    @Transactional
    public EmpresaEquipoDto guardarEquipo(EmpresaEquipoDto empresaEquipoDto, String usuario) {
        if(StringUtils.isBlank(usuario) || empresaEquipoDto == null) {
            logger.warn("El usuario, la empresa o el uniforme a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo equipo");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(usuario);

        EmpresaEquipo empresaEquipo = new EmpresaEquipo();
        daoHelper.fulfillAuditorFields(true, empresaEquipo, usuarioDto.getId());
        empresaEquipo.setEmpresa(usuarioDto.getEmpresa().getId());
        empresaEquipo.setEquipo(empresaEquipoDto.getEquipo().getId());
        empresaEquipo.setCantidad(empresaEquipoDto.getCantidad());
        EmpresaEquipo empresaEquipoCreado = empresaEquipoRepository.save(empresaEquipo);

        EmpresaEquipoMovimiento empresaEquipoMovimiento = dtoToDaoConverter.convertDtoToDaoEmpresaEquipoMovimiento(empresaEquipoDto.getMovimientos().get(0));
        empresaEquipoMovimiento.setEmpresaEquipo(empresaEquipoCreado.getId());
        daoHelper.fulfillAuditorFields(true, empresaEquipoMovimiento, usuarioDto.getId());
        empresaEquipoMovimientoRepository.save(empresaEquipoMovimiento);

        return daoToDtoConverter.convertDaoToDtoEmpresaEquipo(empresaEquipoCreado);
    }

    @Override
    @Transactional
    public EmpresaEquipoDto modificarEquipo(String equipoUuid, String usuario, EmpresaEquipoDto empresaEquipoDto) {
        if(StringUtils.isBlank(equipoUuid) || StringUtils.isBlank(usuario) || empresaEquipoDto == null) {
            logger.warn("Alguno de los parametros viene como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el equipo con el uuid [{}]", equipoUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(usuario);
        EmpresaEquipo empresaEquipo = empresaEquipoRepository.findByUuidAndEliminadoFalse(equipoUuid);
        if(empresaEquipo == null) {
            logger.warn("El equipo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEquipo.setCantidad(empresaEquipoDto.getCantidad());
        daoHelper.fulfillAuditorFields(false, empresaEquipo, usuarioDto.getId());
        EmpresaEquipo empresaEquipoCreado = empresaEquipoRepository.save(empresaEquipo);

        EmpresaEquipoMovimiento empresaEquipoMovimiento = dtoToDaoConverter.convertDtoToDaoEmpresaEquipoMovimiento(empresaEquipoDto.getMovimientos().get(0));
        empresaEquipoMovimiento.setEmpresaEquipo(empresaEquipoCreado.getId());
        daoHelper.fulfillAuditorFields(true, empresaEquipoMovimiento, usuarioDto.getId());
        empresaEquipoMovimientoRepository.save(empresaEquipoMovimiento);

        return daoToDtoConverter.convertDaoToDtoEmpresaEquipo(empresaEquipoCreado);
    }

    @Override
    @Transactional
    public EmpresaEquipoDto eliminarEquipo(String equipoUuid, String usuario) {
        if(StringUtils.isBlank(equipoUuid) || StringUtils.isBlank(usuario)) {
            logger.warn("Alguno de los parametros viene como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el equipo con el uuid [{}]", equipoUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(usuario);
        EmpresaEquipo empresaEquipo = empresaEquipoRepository.findByUuidAndEliminadoFalse(equipoUuid);
        if(empresaEquipo == null) {
            logger.warn("El equipo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEquipo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaEquipo, usuarioDto.getId());
        EmpresaEquipo empresaEquipoCreado = empresaEquipoRepository.save(empresaEquipo);

        return daoToDtoConverter.convertDaoToDtoEmpresaEquipo(empresaEquipoCreado);
    }
}
