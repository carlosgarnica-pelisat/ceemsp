package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaUniforme;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaUniformeServiceImpl implements EmpresaUniformeService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaUniformeRepository empresaUniformeRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final UniformeService uniformeService;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaUniformeElementoService empresaUniformeElementoService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaLicenciaColectivaService.class);

    @Autowired
    public EmpresaUniformeServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                      EmpresaUniformeRepository empresaUniformeRepository, EmpresaService empresaService,
                                      UsuarioService usuarioService, UniformeService uniformeService,
                                      DaoHelper<CommonModel> daoHelper, EmpresaUniformeElementoService empresaUniformeElementoService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaUniformeRepository = empresaUniformeRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.uniformeService = uniformeService;
        this.daoHelper = daoHelper;
        this.empresaUniformeElementoService = empresaUniformeElementoService;
    }

    @Override
    public List<EmpresaUniformeDto> obtenerUniformesPorEmpresaUuid(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa se encuentra nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        List<EmpresaUniforme> empresaUniformes = empresaUniformeRepository.findAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return empresaUniformes.stream().map(u -> {
            EmpresaUniformeDto uniformeDto = daoToDtoConverter.convertDaoToDtoEmpresaUniforme(u);
            // TODO: agregar los elementos del uniforme dentro del mismo response
            return uniformeDto;
        }).collect(Collectors.toList());
    }

    @Override
    public EmpresaUniformeDto obtenerUniformePorUuid(String empresaUuid, String uniformeUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid)) {
            logger.warn("El uuid de la empresa o del uniforme vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el uniforme con el uuid [{}]", uniformeUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaUniforme empresaUniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);
        if(empresaUniforme == null) {
            logger.warn("El uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(empresaUniforme.getEmpresa() != empresaDto.getId()) {
            logger.warn("El uniforme a consultar no pertenece a este recurso");
            throw new MissingRelationshipException();
        }

        EmpresaUniformeDto empresaUniformeDto = daoToDtoConverter.convertDaoToDtoEmpresaUniforme(empresaUniforme);
        empresaUniformeDto.setElementos(empresaUniformeElementoService.obtenerElementosUniformePorEmpresaUuid(empresaUuid, uniformeUuid));
        return empresaUniformeDto;
    }

    @Override
    public EmpresaUniformeDto guardarUniforme(String empresaUuid, String username, EmpresaUniformeDto empresaUniformeDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || empresaUniformeDto == null) {
            logger.warn("El usuario, la empresa o el uniforme a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo uniforme");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        EmpresaUniforme empresaUniforme = dtoToDaoConverter.convertDtoToDaoEmpresaUniforme(empresaUniformeDto);
        daoHelper.fulfillAuditorFields(true, empresaUniforme, usuarioDto.getId());
        empresaUniforme.setEmpresa(empresaDto.getId());

        EmpresaUniforme empresaUniformeCreado = empresaUniformeRepository.save(empresaUniforme);

        return daoToDtoConverter.convertDaoToDtoEmpresaUniforme(empresaUniformeCreado);
    }
}
