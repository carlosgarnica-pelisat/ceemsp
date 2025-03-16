package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.EmpresaFormaEjecucion;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaFormaEjecucionRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.type.FormaEjecucionEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final EmpresaRepository empresaRepository;
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;

    @Autowired
    public EmpresaServiceImpl(DaoToDtoConverter daoToDtoConverter, EmpresaRepository empresaRepository, EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.empresaRepository = empresaRepository;
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
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

        AtomicBoolean tieneArmas = new AtomicBoolean(false);
        AtomicBoolean tieneCanes = new AtomicBoolean(false);
        List<EmpresaFormaEjecucion> formasEjecucion = this.empresaFormaEjecucionRepository.getAllByEmpresaAndEliminadoFalse(empresa.getId());
        formasEjecucion.forEach(x -> {
            if(x.getFormaEjecucion() == FormaEjecucionEnum.ARMAS) tieneArmas.set(true);
            if(x.getFormaEjecucion() == FormaEjecucionEnum.CANES) tieneCanes.set(true);
        });

        EmpresaDto empresaDto =  daoToDtoConverter.convertDaoToDtoEmpresa(empresa);

        empresaDto.setTieneArmas(tieneArmas.get());
        empresaDto.setTieneCanes(tieneCanes.get());

        return empresaDto;
    }
}
