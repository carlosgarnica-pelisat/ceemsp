package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEquipoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEquipo;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaEquipoServiceImpl implements EmpresaEquipoService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaUniformeService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaEquipoRepository empresaEquipoRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final EquipoService equipoService;
    private final DaoHelper<CommonModel> daoHelper;

    public EmpresaEquipoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                    EmpresaEquipoRepository empresaEquipoRepository, EmpresaService empresaService,
                                    UsuarioService usuarioService, EquipoService equipoService, DaoHelper<CommonModel> daoHelper) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaEquipoRepository = empresaEquipoRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.equipoService = equipoService;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<EmpresaEquipoDto> obtenerEquiposPorEmpresaUuid(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<EmpresaEquipo> empresaEquipos = empresaEquipoRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return empresaEquipos.stream().map(u -> {
            EmpresaEquipoDto empresaEquipoDto = daoToDtoConverter.convertDaoToDtoEmpresaEquipo(u);
            empresaEquipoDto.setEquipo(equipoService.obtenerEquipoPorId(u.getEquipo()));
            return empresaEquipoDto;
        }).collect(Collectors.toList());
    }

    @Override
    public EmpresaEquipoDto obtenerEquipoPorUuid(String empresaUuid, String equipoUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(equipoUuid)) {
            logger.warn("El uuid de la empresa o del equipo vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el equipo con el uuid [{}]", equipoUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaEquipo empresaEquipo = empresaEquipoRepository.findByUuidAndEliminadoFalse(equipoUuid);
        if(empresaEquipo == null) {
            logger.warn("El equipo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(empresaEquipo.getEmpresa() != empresaDto.getId()) {
            logger.warn("El equipo a consultar no pertenece a este recurso");
            throw new MissingRelationshipException();
        }

        EmpresaEquipoDto empresaEquipoDto = daoToDtoConverter.convertDaoToDtoEmpresaEquipo(empresaEquipo);
        empresaEquipoDto.setEquipo(equipoService.obtenerEquipoPorId(empresaEquipo.getEquipo()));
        return empresaEquipoDto;
    }

    @Override
    public EmpresaEquipoDto guardarEquipo(String empresaUuid, String usuario, EmpresaEquipoDto empresaEquipoDto) {
        if(StringUtils.isBlank(usuario) || StringUtils.isBlank(empresaUuid) || empresaEquipoDto == null) {
            logger.warn("El usuario, la empresa o el uniforme a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo equipo");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(usuario);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        EmpresaEquipo empresaEquipo = new EmpresaEquipo();
        daoHelper.fulfillAuditorFields(true, empresaEquipo, usuarioDto.getId());
        empresaEquipo.setEmpresa(empresaDto.getId());
        empresaEquipo.setEquipo(empresaEquipoDto.getEquipo().getId());
        empresaEquipo.setCantidad(empresaEquipoDto.getCantidad());
        EmpresaEquipo empresaEquipoCreado = empresaEquipoRepository.save(empresaEquipo);

        return daoToDtoConverter.convertDaoToDtoEmpresaEquipo(empresaEquipoCreado);
    }

    @Override
    public EmpresaEquipoDto modificarEquipo(String empresaUuid, String equipoUuid, String usuario, EmpresaEquipoDto empresaEquipoDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(equipoUuid) || StringUtils.isBlank(usuario) || empresaEquipoDto == null) {
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

        empresaEquipo.setEquipo(empresaEquipoDto.getEquipo().getId());
        empresaEquipo.setCantidad(empresaEquipoDto.getCantidad());
        daoHelper.fulfillAuditorFields(false, empresaEquipo, usuarioDto.getId());
        EmpresaEquipo empresaEquipoCreado = empresaEquipoRepository.save(empresaEquipo);

        return daoToDtoConverter.convertDaoToDtoEmpresaEquipo(empresaEquipoCreado);
    }

    @Override
    public EmpresaEquipoDto eliminarEquipo(String empresaUuid, String equipoUuid, String usuario) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(equipoUuid) || StringUtils.isBlank(usuario)) {
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
