package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaFormaEjecucion;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaFormaEjecucionRepository;
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
public class EmpresaFormaEjecucionServiceImpl implements EmpresaFormaEjecucionService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaFormaEjecucionServiceImpl.class);
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;

    @Autowired
    public EmpresaFormaEjecucionServiceImpl(EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository,
                                            UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter,
                                            DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                            EmpresaService empresaService) {
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
    }

    @Override
    public List<EmpresaFormaEjecucionDto> obtenerFormasEjecucionPorEmpresaUuid(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las formas de ejecucion para la empresa [{}]", empresaUuid);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        List<EmpresaFormaEjecucion> empresaFormasEjecucion = empresaFormaEjecucionRepository.getAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        if(empresaFormasEjecucion == null || empresaFormasEjecucion.size() == 0) {
            logger.warn("La empresa no tiene formas de ejecucion en la base de datos");
            throw new NotFoundResourceException();
        }

        return empresaFormasEjecucion.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaFormaEjecucion)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaFormaEjecucionDto crearFormaEjecucion(String empresaUuid, String username, EmpresaFormaEjecucionDto empresaFormaEjecucionDto) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || empresaFormaEjecucionDto == null) {
            logger.warn("La forma de ejecucion, la empresa o el usuario vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando una nueva de ejecucion para la empresa con el uuid [{}]", empresaUuid);

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaFormaEjecucion empresaFormaEjecucion = dtoToDaoConverter.convertDtoToDaoEmpresaFormaEjecucion(empresaFormaEjecucionDto);
        daoHelper.fulfillAuditorFields(true, empresaFormaEjecucion, usuarioDto.getId());
        empresaFormaEjecucion.setEmpresa(empresaDto.getId());

        EmpresaFormaEjecucion formaejecucionCreada = empresaFormaEjecucionRepository.save(empresaFormaEjecucion);

        return daoToDtoConverter.convertDaoToDtoEmpresaFormaEjecucion(formaejecucionCreada);
    }
}
