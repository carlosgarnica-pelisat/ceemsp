package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaRepository empresaRepository;

    @Autowired
    public EmpresaServiceImpl(DaoToDtoConverter daoToDtoConverter, EmpresaRepository empresaRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaRepository = empresaRepository;
    }

    @Override
    public EmpresaDto obtenerPorId(int id) {
        if(id < 1) {
            logger.warn("El uuid de la empresa a consultar viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getOne(id);

        if(empresa == null || empresa.getEliminado()) {
            logger.warn("La empresa no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresa);
    }
}
