package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.SubmodalidadDto;
import com.pelisat.cesp.ceemsp.database.model.Submodalidad;
import com.pelisat.cesp.ceemsp.database.repository.SubmodalidadRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmodalidadServiceImpl implements SubmodalidadService{
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final SubmodalidadRepository submodalidadRepository;
    private final UsuarioService usuarioService;

    private final Logger logger = LoggerFactory.getLogger(SubmodalidadService.class);

    @Autowired
    public SubmodalidadServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, SubmodalidadRepository submodalidadRepository,
                                   UsuarioService usuarioService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.submodalidadRepository = submodalidadRepository;
        this.usuarioService = usuarioService;
    }


    @Override
    public List<SubmodalidadDto> obtenerSubmodalidadesPorModalidad(int modalidadId) {
        if(modalidadId < 1) {
            logger.warn("El id esta viniendo como nulo o vacio.");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo todas las submodalidades con la modalidad id [{}]", modalidadId);

        List<Submodalidad> submodalidades = submodalidadRepository.getAllByCategoriaAndEliminadoFalse(modalidadId);

        return submodalidades.stream()
                .map(daoToDtoConverter::convertDaoToDtoSubmodalidad)
                .collect(Collectors.toList());
    }
}
