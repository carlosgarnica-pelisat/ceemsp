package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final UsuarioService usuarioService;
    private final EmpresaRepository empresaRepository;
    private final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;

    @Autowired
    public EmpresaServiceImpl(UsuarioService usuarioService, EmpresaRepository empresaRepository, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter) {
        this.usuarioService = usuarioService;
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
    }

    @Override
    public List<EmpresaDto> obtenerTodas() {
        return null;
    }

    @Override
    public EmpresaDto obtenerPorUuid(String uuiid) {
        return null;
    }

    @Override
    public EmpresaDto obtenerPorId(int id) {
        return null;
    }

    @Override
    public EmpresaDto crearEmpresa(EmpresaDto empresaDto, String username) {
        if(StringUtils.isBlank(username) || empresaDto == null) {
            logger.warn("La empresa a crear o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Dando de alta a una nueva empresa con razon social: [{}]", empresaDto.getNombreComercial());

        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new InvalidDataException();
        }

        Empresa empresa = dtoToDaoConverter.convertDtoToDaoEmpresa(empresaDto);

        empresa.setFechaCreacion(LocalDateTime.now());
        empresa.setCreadoPor(usuario.getId());
        empresa.setActualizadoPor(usuario.getId());
        empresa.setFechaActualizacion(LocalDateTime.now());

        Empresa empresaCreada = empresaRepository.save(empresa);

        return daoToDtoConverter.convertDaoToDtoEmpresa(empresaCreada);
    }

    @Override
    public EmpresaDto modificarEmpresa(EmpresaDto empresaDto, String username, String uuid) {
        return null;
    }

    @Override
    public EmpresaDto eliminarEmpresa(String username, String uuid) {
        return null;
    }
}
