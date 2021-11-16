package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraApoderadoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscritura;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraApoderado;
import com.pelisat.cesp.ceemsp.database.model.EmpresaEscrituraRepresentante;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraApoderadoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepresentanteRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaEscrituraApoderadoServiceImpl implements EmpresaEscrituraApoderadoService {

    private final EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final EmpresaService empresaService;
    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraSocioService.class);

    @Autowired
    public EmpresaEscrituraApoderadoServiceImpl(EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository, EmpresaEscrituraRepository empresaEscrituraRepository,
                                                    UsuarioService usuarioService, DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                                    DaoHelper<CommonModel> daoHelper, EmpresaService empresaService) {
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.usuarioService = usuarioService;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
    }

    @Override
    public List<EmpresaEscrituraApoderadoDto> obtenerApoderadosPorEscritura(String empresaUuid, String escrituraUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaEscrituraApoderado> empresaEscrituraApoderados = empresaEscrituraApoderadoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

        return empresaEscrituraApoderados.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraApoderado)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraApoderadoDto obtenerRepresentantePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad) {
        return null;
    }

    @Override
    public EmpresaEscrituraApoderadoDto crearApoderado(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraApoderadoDto empresaEscrituraApoderadoDto) {
        if(empresaEscrituraApoderadoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El apoderado, la empresa o la escritura estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando apoderado en la escritura: [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraApoderado empresaEscrituraApoderado = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraApoderado(empresaEscrituraApoderadoDto);
        empresaEscrituraApoderado.setEscritura(empresaEscritura.getId());
        empresaEscrituraApoderado.setFechaInicio(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaInicio()));
        empresaEscrituraApoderado.setFechaFin(LocalDate.parse(empresaEscrituraApoderadoDto.getFechaFin()));
        daoHelper.fulfillAuditorFields(true, empresaEscrituraApoderado, usuario.getId());
        EmpresaEscrituraApoderado empresaEscrituraRepresentanteCreada = empresaEscrituraApoderadoRepository.save(empresaEscrituraApoderado);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraApoderado(empresaEscrituraRepresentanteCreada);
    }
}
