package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeElementoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeRepository;
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
public class EmpresaUniformeElementoServiceImpl implements EmpresaUniformeElementoService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaUniformeElementoRepository empresaUniformeElementoRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final EmpresaUniformeRepository empresaUniformeRepository;
    private final DaoHelper<CommonModel> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(EmpresaUniformeElementoService.class);
    private final UniformeService uniformeService;

    @Autowired
    public EmpresaUniformeElementoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                              EmpresaUniformeElementoRepository empresaUniformeElementoRepository, EmpresaService empresaService,
                                              UsuarioService usuarioService, EmpresaUniformeRepository empresaUniformeRepository,
                                              DaoHelper<CommonModel> daoHelper, UniformeService uniformeService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaUniformeElementoRepository = empresaUniformeElementoRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.empresaUniformeRepository = empresaUniformeRepository;
        this.daoHelper = daoHelper;
        this.uniformeService = uniformeService;
    }

    @Override
    public List<EmpresaUniformeElementoDto> obtenerElementosUniformePorEmpresaUuid(String empresaUuid, String uniformeUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid)) {
            logger.warn("El uuid de la empresa o del uniforme vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaUniforme empresaUniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);

        if(empresaUniforme == null) {
            logger.warn("No se encontro el uniforme en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaUniformeElemento> empresaUniformeElementos = empresaUniformeElementoRepository.findAllByUniformeAndEliminadoFalse(empresaUniforme.getId());

        return empresaUniformeElementos.stream()
                .map(e -> {
                    EmpresaUniformeElementoDto emued = daoToDtoConverter.convertDaoToDtoEmpresaUniformeElemento(e);
                    emued.setElemento(uniformeService.obtenerUniformePorId(e.getId()));
                    return emued;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaUniformeElementoDto guardarUniformeElemento(String empresaUuid, String uniformeUuid, String username, EmpresaUniformeElementoDto empresaUniformeElementoDto) {
        if(empresaUniformeElementoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid)) {
            logger.warn("El uniforme, la empresa o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un elemento en el uniforme: [{}]", uniformeUuid);

        EmpresaUniforme uniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);

        if(uniforme == null) {
            logger.warn("No se encontro el uniforme en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaUniformeElemento empresaUniformeElemento = dtoToDaoConverter.convertDtoToDaoEmpresaUniformeElemento(empresaUniformeElementoDto);
        empresaUniformeElemento.setUniforme(uniforme.getId());
        empresaUniformeElemento.setElemento(empresaUniformeElementoDto.getElemento().getId());
        daoHelper.fulfillAuditorFields(true, empresaUniformeElemento, usuario.getId());
        EmpresaUniformeElemento empresaUniformeelementoCreado = empresaUniformeElementoRepository.save(empresaUniformeElemento);

        return daoToDtoConverter.convertDaoToDtoEmpresaUniformeElemento(empresaUniformeelementoCreado);
    }
}
