package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaLicenciaColectivaDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaLicenciaColectiva;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaLicenciaColectivaRepository;
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
public class EmpresaLicenciaColectivaServiceImpl implements EmpresaLicenciaColectivaService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final DaoHelper<CommonModel> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(EmpresaLicenciaColectivaService.class);

    @Autowired
    public EmpresaLicenciaColectivaServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                               EmpresaLicenciaColectivaRepository empresaLicenciaColectivaRepository,
                                               UsuarioService usuarioService, DaoHelper<CommonModel> daoHelper,
                                               EmpresaService empresaService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaLicenciaColectivaRepository = empresaLicenciaColectivaRepository;
        this.usuarioService = usuarioService;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
    }

    @Override
    public List<EmpresaLicenciaColectivaDto> obtenerLicenciasColectivasPorEmpresa(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacio");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);

        List<EmpresaLicenciaColectiva> empresaLicenciasColectivas = empresaLicenciaColectivaRepository
                .findAllByEmpresaAndEliminadoFalse(empresaDto.getId());

        return empresaLicenciasColectivas.stream().map(
                daoToDtoConverter::convertDaoToDtoEmpresaLicenciaColectiva)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaLicenciaColectivaDto obtenerLicenciaColectivaPorUuid(String empresaUuid, String licenciaUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(licenciaUuid)) {
            logger.warn("Uno o los uuid de la empresa o la licencia vienen como nulos o vacios");
            throw new InvalidDataException();
        }


        return null;
    }

    @Override
    public EmpresaLicenciaColectivaDto guardarLicenciaColectiva(String empresaUuid, String username, EmpresaLicenciaColectivaDto licenciaColectivaDto) {
        return null;
    }
}
