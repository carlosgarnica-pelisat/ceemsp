package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.PersonaDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.repository.PersonaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaServiceImpl implements PersonaService {

    private final DaoHelper<CommonModel> daoHelper;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;

    @Autowired
    public PersonaServiceImpl(DaoHelper<CommonModel> daoHelper, DaoToDtoConverter daoToDtoConverter,
                              DtoToDaoConverter dtoToDaoConverter, EmpresaService empresaService,
                              UsuarioService usuarioService, PersonaRepository personaRepository) {
        this.daoHelper = daoHelper;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.personaRepository = personaRepository;
    }

    @Override
    public List<PersonaDto> obtenerTodos(String empresaUuid) {
        return null;
    }

    @Override
    public PersonaDto obtenerPorUuid(String empresaUuid, String personaUuid) {
        return null;
    }

    @Override
    public PersonaDto obtenerPorId(String empresaUuid, Integer id) {
        return null;
    }

    @Override
    public PersonaDto crearNuevo(PersonaDto personalNacionalidadDto, String username, String empresaUuid) {
        return null;
    }
}
