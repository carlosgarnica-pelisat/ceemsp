package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaFormaEjecucionDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaFormaEjecucionRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaFormaEjecucionServiceImpl implements EmpresaFormaEjecucionService {
    private final Logger logger = LoggerFactory.getLogger(EmpresaFormaEjecucionService.class);
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final UsuarioService usuarioService;

    @Autowired
    public EmpresaFormaEjecucionServiceImpl(EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository, DaoToDtoConverter daoToDtoConverter,
                                            UsuarioService usuarioService)
    {
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.usuarioService = usuarioService;
    }


    @Override
    public List<EmpresaFormaEjecucionDto> obtenerFormasEjecucionEmpresa(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo las formas de ejecucion de la empresa");
        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        return empresaFormaEjecucionRepository.getAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId())
                .stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaFormaEjecucion)
                .collect(Collectors.toList());
    }
}
