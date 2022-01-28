package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaUniforme;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
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

    private final Logger logger = LoggerFactory.getLogger(EmpresaUniformeService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaUniformeRepository empresaUniformeRepository;
    private final UsuarioService usuarioService;
    private final CatalogoService catalogoService;
    private final DaoHelper<CommonModel> daoHelper;
    //private final EmpresaUniformeElementoService empresaUniformeElementoService;

    @Autowired
    public EmpresaUniformeServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                      EmpresaUniformeRepository empresaUniformeRepository, UsuarioService usuarioService,
                                      CatalogoService catalogoService, DaoHelper<CommonModel> daoHelper) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaUniformeRepository = empresaUniformeRepository;
        this.usuarioService = usuarioService;
        this.catalogoService = catalogoService;
        this.daoHelper = daoHelper;
    }

    @Override
    public List<EmpresaUniformeDto> obtenerUniformesPorEmpresaUuid(String empresaUsername) {
        if(StringUtils.isBlank(empresaUsername)) {
            logger.warn("El usuario de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo los uniformes de la empresa ligada al usuario [{}]", empresaUsername);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(empresaUsername);
        List<EmpresaUniforme> empresaUniformes = empresaUniformeRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());

        return empresaUniformes.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaUniforme).collect(Collectors.toList());
    }

    @Override
    public EmpresaUniformeDto obtenerUniformePorUuid(String empresaUsername, String uniformeUuid) {
        return null;
    }

    @Override
    public EmpresaUniformeDto guardarUniforme(String empresaUsername, EmpresaUniformeDto empresaUniformeDto) {
        return null;
    }
}
