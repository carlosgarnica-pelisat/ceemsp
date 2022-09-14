package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaUniforme;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeElementoRepository;
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

    private final Logger logger = LoggerFactory.getLogger(EmpresaUniformeService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaUniformeRepository empresaUniformeRepository;
    private final UsuarioService usuarioService;
    private final CatalogoService catalogoService;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaUniformeElementoService empresaUniformeElementoService;
    //private final EmpresaUniformeElementoService empresaUniformeElementoService;

    @Autowired
    public EmpresaUniformeServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                      EmpresaUniformeRepository empresaUniformeRepository, UsuarioService usuarioService,
                                      CatalogoService catalogoService, DaoHelper<CommonModel> daoHelper,
                                      EmpresaUniformeElementoService empresaUniformeElementoService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaUniformeRepository = empresaUniformeRepository;
        this.usuarioService = usuarioService;
        this.catalogoService = catalogoService;
        this.daoHelper = daoHelper;
        this.empresaUniformeElementoService = empresaUniformeElementoService;
    }


    @Override
    public List<EmpresaUniformeDto> obtenerUniformesPorEmpresaUuid(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa viene como nulo o vacío");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        List<EmpresaUniforme> empresaUniformes = empresaUniformeRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());
        return empresaUniformes.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaUniforme).collect(Collectors.toList());
    }

    @Override
    public EmpresaUniformeDto obtenerUniformePorUuid(String uniformeUuid) {
        if(StringUtils.isBlank(uniformeUuid)) {
            logger.warn("El uuid de la empresa o del uniforme vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo el uniforme con el uuid [{}]", uniformeUuid);

        EmpresaUniforme empresaUniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);
        if(empresaUniforme == null) {
            logger.warn("El uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        EmpresaUniformeDto empresaUniformeDto = daoToDtoConverter.convertDaoToDtoEmpresaUniforme(empresaUniforme);
        empresaUniformeDto.setElementos(empresaUniformeElementoService.obtenerElementosUniformePorEmpresaUuid(uniformeUuid));
        return empresaUniformeDto;
    }

    @Override
    public EmpresaUniformeDto guardarUniforme(String usuario, EmpresaUniformeDto empresaUniformeDto) {
        if(StringUtils.isBlank(usuario) || empresaUniformeDto == null) {
            logger.warn("El usuario, la empresa o el uniforme a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un nuevo uniforme");

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(usuario);

        EmpresaUniforme empresaUniforme = dtoToDaoConverter.convertDtoToDaoEmpresaUniforme(empresaUniformeDto);
        daoHelper.fulfillAuditorFields(true, empresaUniforme, usuarioDto.getId());
        empresaUniforme.setEmpresa(usuarioDto.getEmpresa().getId());

        EmpresaUniforme empresaUniformeCreado = empresaUniformeRepository.save(empresaUniforme);

        return daoToDtoConverter.convertDaoToDtoEmpresaUniforme(empresaUniformeCreado);
    }

    @Override
    public EmpresaUniformeDto modificarUniforme(String uniformeUuid, String usuario, EmpresaUniformeDto empresaUniformeDto) {
        if(StringUtils.isBlank(usuario) || StringUtils.isBlank(uniformeUuid) || empresaUniformeDto == null) {
            logger.warn("|Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Modificando el uniforme");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(usuario);

        EmpresaUniforme empresaUniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);
        if(empresaUniforme == null) {
            logger.warn("El uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaUniforme.setNombre(empresaUniformeDto.getNombre());
        empresaUniforme.setDescripcion(empresaUniformeDto.getDescripcion());
        daoHelper.fulfillAuditorFields(false, empresaUniforme, usuarioDto.getId());
        empresaUniformeRepository.save(empresaUniforme);
        return empresaUniformeDto;
    }

    @Override
    public EmpresaUniformeDto eliminarUniforme(String uniformeUuid, String usuario) {
        if(StringUtils.isBlank(usuario) || StringUtils.isBlank(uniformeUuid)) {
            logger.warn("|Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el uniforme");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(usuario);

        EmpresaUniforme empresaUniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);
        if(empresaUniforme == null) {
            logger.warn("El uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaUniforme.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaUniforme, usuarioDto.getId());
        empresaUniformeRepository.save(empresaUniforme);
        return daoToDtoConverter.convertDaoToDtoEmpresaUniforme(empresaUniforme);
    }
}
