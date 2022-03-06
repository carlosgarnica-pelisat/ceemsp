package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class EmpresaUsuarioServiceImpl implements EmpresaUsuarioService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaUsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final EmpresaService empresaService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;

    @Autowired
    public EmpresaUsuarioServiceImpl(UsuarioRepository usuarioRepository, DaoToDtoConverter daoToDtoConverter,
                                     DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
                                     EmpresaService empresaService) {
        this.usuarioRepository = usuarioRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.empresaService = empresaService;
    }

    @Override
    public List<UsuarioDto> obtenerUsuarios(String uuid) {
        return null;
    }

    @Transactional
    @Override
    public UsuarioDto guardarUsuario(String uuid, String username, UsuarioDto usuarioDto) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(username) || usuarioDto == null) {
            logger.warn("El uuid de la empresa, el usuario o el userbname vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Se esta guardando o actualizando el usuario de la empresa [{}]", uuid);
        EmpresaDto empresaDto = empresaService.obtenerPorUuid(uuid);
        Usuario usuarioAuditable = usuarioRepository.getUsuarioByEmail(username);
        Usuario usuario = usuarioRepository.getUsuarioByEmpresaAndEliminadoFalse(empresaDto.getId());

        if(usuario == null) {
            logger.info("El usuario no se encontro en la base de datos. Se esta creando uno nuevo para la empresa [{}]", uuid);
            Usuario usuarioACrear = dtoToDaoConverter.convertDtoToDaoUser(usuarioDto);
            usuarioACrear.setEmpresa(empresaDto.getId());
            daoHelper.fulfillAuditorFields(true, usuarioACrear, usuarioAuditable.getId());
            usuarioRepository.save(usuarioACrear);
            return usuarioDto;
        } else {
            logger.info("El usuario si se encontro en la base de datos. Actualizando para la empresa [{}]", uuid);
            usuario.setNombres(usuarioDto.getNombres());
            usuario.setApellidos(usuarioDto.getApellidos());
            usuario.setEmail(usuarioDto.getEmail());
            daoHelper.fulfillAuditorFields(false, usuario, usuarioAuditable.getId());
            usuarioRepository.save(usuario);
            return usuarioDto;
        }
    }

    @Override
    public UsuarioDto modificarUsuario(String uuid, String usuarioUuid, String username, UsuarioDto usuarioDto) {
        return null;
    }

    @Override
    public UsuarioDto eliminarUsuario(String uuid, String usuarioUuid, String username) {
        return null;
    }
}
