package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoColorDto;
import com.pelisat.cesp.ceemsp.database.dto.VehiculoDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaDomicilio;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.model.VehiculoColor;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoColorRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
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
public class VehiculoColorServiceImpl implements VehiculoColorService {

    private final Logger logger = LoggerFactory.getLogger(VehiculoColorServiceImpl.class);
    private final VehiculoColorRepository vehiculoColorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public VehiculoColorServiceImpl(VehiculoColorRepository vehiculoColorRepository, VehiculoRepository vehiculoRepository,
                                    UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                    DaoHelper<CommonModel> daoHelper, EmpresaService empresaService) {
        this.vehiculoColorRepository = vehiculoColorRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
    }

    @Override
    public List<VehiculoColorDto> obtenerTodosPorVehiculoUuid(String vehiculoUuid, String empresaUuid) {
        if(StringUtils.isBlank(vehiculoUuid)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        Vehiculo vehiculo = vehiculoRepository.getByUuid(vehiculoUuid);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<VehiculoColor> colores = vehiculoColorRepository.getAllByVehiculoAndEliminadoFalse(vehiculo.getId());
        return colores.stream().map(daoToDtoConverter::convertDaoToDtoVehiculoColor).collect(Collectors.toList());
    }

    @Override
    public List<VehiculoColorDto> obtenerTodosPorVehiculoId(int id) {
        return null;
    }

    @Override
    public VehiculoColorDto guardarcolor(String empresaUuid, String vehiculoUuid, String username, VehiculoColorDto vehiculoColorDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username) || vehiculoColorDto == null) {
            logger.warn("El uuid de la empresa, del vehiculo, el usuario o el color a dar de alta vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        Vehiculo vehiculo = vehiculoRepository.getByUuid(vehiculoUuid);

        if(vehiculo == null) {
            logger.warn("El vehiculo no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        VehiculoColor vehiculoColor = dtoToDaoConverter.convertDtoToDaoColor(vehiculoColorDto);
        vehiculoColor.setVehiculo(vehiculo.getId());
        daoHelper.fulfillAuditorFields(true, vehiculoColor, usuarioDto.getId());

        VehiculoColor vehiculoColorCreado = vehiculoColorRepository.save(vehiculoColor);

        return daoToDtoConverter.convertDaoToDtoVehiculoColor(vehiculoColorCreado);
    }

    @Override
    public VehiculoColorDto modificarColor(String empresaUuid, String vehiculoUuid, String colorUuid, String username, VehiculoColorDto vehiculoColorDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username) || vehiculoColorDto == null) {
            logger.warn("El uuid de la empresa, del vehiculo, el usuario o el color a dar de alta vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando el color con el uuid [{}]", colorUuid);
        VehiculoColor vehiculoColor = vehiculoColorRepository.findByUuidAndEliminadoFalse(colorUuid);

        if(vehiculoColor == null) {
            logger.warn("El color no existe en la base de datos");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        vehiculoColor.setColor(vehiculoColorDto.getColor());
        vehiculoColor.setDescripcion(vehiculoColorDto.getDescripcion());
        daoHelper.fulfillAuditorFields(false, vehiculoColor, usuarioDto.getId());
        vehiculoColorRepository.save(vehiculoColor);

        return daoToDtoConverter.convertDaoToDtoVehiculoColor(vehiculoColor);
    }

    @Override
    public VehiculoColorDto eliminarColor(String empresaUuid, String vehiculoUuid, String colorUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(vehiculoUuid) || StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa, del vehiculo, el usuario o el color a dar de alta vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el color con el uuid [{}]", colorUuid);
        VehiculoColor vehiculoColor = vehiculoColorRepository.findByUuidAndEliminadoFalse(colorUuid);

        if(vehiculoColor == null) {
            logger.warn("El color no existe en la base de datos");
            throw new InvalidDataException();
        }
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        vehiculoColor.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, vehiculoColor, usuarioDto.getId());
        vehiculoColorRepository.save(vehiculoColor);
        return daoToDtoConverter.convertDaoToDtoVehiculoColor(vehiculoColor);
    }
}
