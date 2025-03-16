package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ArmaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Arma;
import com.pelisat.cesp.ceemsp.database.repository.ArmaRepository;
import com.pelisat.cesp.ceemsp.database.type.ArmaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.ArmaTipoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaArmaServiceImpl implements EmpresaArmaService {

    private final UsuarioService usuarioService;
    private final ArmaRepository armaRepository;
    private final CatalogoService catalogoService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaDomicilioService empresaDomicilioService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaArmaService.class);

    @Autowired
    public EmpresaArmaServiceImpl(UsuarioService usuarioService, ArmaRepository armaRepository, CatalogoService catalogoService,
                                  DaoToDtoConverter daoToDtoConverter, EmpresaDomicilioService empresaDomicilioService) {
        this.usuarioService = usuarioService;
        this.armaRepository = armaRepository;
        this.catalogoService = catalogoService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaDomicilioService = empresaDomicilioService;
    }

    @Override
    public List<ArmaDto> obtenerArmasPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las armas guardadas para la empresa {}", username);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Arma> armas = armaRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        List<ArmaDto> response = armas.stream().map(arma -> {
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
            armaDto.setMarca(catalogoService.obtenerArmaMarcaPorId(arma.getMarca()));
            armaDto.setClase(catalogoService.obtenerArmaClasePorId(arma.getClase()));
            return armaDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public List<ArmaDto> obtenerArmasCortasPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las armas guardadas para la empresa {}", username);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Arma> armas = armaRepository.getAllByEmpresaAndTipoAndStatusAndEliminadoFalse(usuarioDto.getEmpresa().getId(), ArmaTipoEnum.CORTA, ArmaStatusEnum.DEPOSITO);

        List<ArmaDto> response = armas.stream().map(arma -> {
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
            armaDto.setMarca(catalogoService.obtenerArmaMarcaPorId(arma.getMarca()));
            armaDto.setClase(catalogoService.obtenerArmaClasePorId(arma.getClase()));
            return armaDto;
        }).collect(Collectors.toList());

        return response;
    }

    @Override
    public List<ArmaDto> obtenerArmasLargasPorEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las armas guardadas para la empresa {}", username);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<Arma> armas = armaRepository.getAllByEmpresaAndTipoAndStatusAndEliminadoFalse(usuarioDto.getEmpresa().getId(), ArmaTipoEnum.LARGA, ArmaStatusEnum.DEPOSITO);

        List<ArmaDto> response = armas.stream().map(arma -> {
            ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);
            armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
            armaDto.setMarca(catalogoService.obtenerArmaMarcaPorId(arma.getMarca()));
            armaDto.setClase(catalogoService.obtenerArmaClasePorId(arma.getClase()));
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

        ArmaDto armaDto = daoToDtoConverter.convertDaoToDtoArma(arma);

        armaDto.setBunker(empresaDomicilioService.obtenerPorId(arma.getBunker()));
        armaDto.setMarca(catalogoService.obtenerArmaMarcaPorId(arma.getMarca()));
        armaDto.setClase(catalogoService.obtenerArmaClasePorId(arma.getClase()));

        return armaDto;
    }
}
