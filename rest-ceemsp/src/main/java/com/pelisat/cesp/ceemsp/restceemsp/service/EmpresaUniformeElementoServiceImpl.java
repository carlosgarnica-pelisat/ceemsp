package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaUniformeElementoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeElementoMovimientoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeElementoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaUniformeRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import com.pelisat.cesp.ceemsp.infrastructure.services.ArchivosService;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaUniformeElementoServiceImpl implements EmpresaUniformeElementoService {

    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final EmpresaUniformeElementoRepository empresaUniformeElementoRepository;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final EmpresaUniformeRepository empresaUniformeRepository;
    private final DaoHelper<CommonModel> daoHelper;
    private final Logger logger = LoggerFactory.getLogger(EmpresaUniformeElementoService.class);
    private final UniformeService uniformeService;
    private final ArchivosService archivosService;
    private final EmpresaUniformeElementoMovimientoRepository empresaUniformeElementoMovimientoRepository;

    @Autowired
    public EmpresaUniformeElementoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                              EmpresaUniformeElementoRepository empresaUniformeElementoRepository, EmpresaService empresaService,
                                              UsuarioService usuarioService, EmpresaUniformeRepository empresaUniformeRepository,
                                              DaoHelper<CommonModel> daoHelper, UniformeService uniformeService,
                                              ArchivosService archivosService, EmpresaUniformeElementoMovimientoRepository empresaUniformeElementoMovimientoRepository) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.empresaUniformeElementoRepository = empresaUniformeElementoRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.empresaUniformeRepository = empresaUniformeRepository;
        this.daoHelper = daoHelper;
        this.uniformeService = uniformeService;
        this.archivosService = archivosService;
        this.empresaUniformeElementoMovimientoRepository = empresaUniformeElementoMovimientoRepository;
    }

    @Override
    public List<EmpresaUniformeElementoDto> obtenerElementosUniformePorEmpresaUuid(String empresaUuid, String uniformeUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid)) {
            logger.warn("El uuid de la empresa o del uniforme vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        EmpresaDto empresaDto = empresaService.obtenerPorUuid(empresaUuid);
        EmpresaUniforme empresaUniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);

        if(empresaUniforme == null) {
            logger.warn("No se encontro el uniforme en la base de datos");
            throw new NotFoundResourceException();
        }

        List<EmpresaUniformeElemento> empresaUniformeElementos = empresaUniformeElementoRepository.findAllByUniformeAndEliminadoFalse(empresaUniforme.getId());

        return empresaUniformeElementos.stream()
                .map(e -> {
                    EmpresaUniformeElementoDto emued = daoToDtoConverter.convertDaoToDtoEmpresaUniformeElemento(e);
                    emued.setElemento(uniformeService.obtenerUniformePorId(e.getElemento()));
                    List<EmpresaUniformeElementoMovimiento> movimientos = empresaUniformeElementoMovimientoRepository.getAllByUniformeElementoAndEliminadoFalse(emued.getId());
                    emued.setMovimientos(movimientos.stream().map(daoToDtoConverter::convertDaoToDtoUniformeElementoMovimiento).collect(Collectors.toList()));
                    return emued;
                })
                .collect(Collectors.toList());
    }

    @Override
    public File obtenerArchivoUniforme(String empresaUuid, String uniformeUuid, String elementoUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid) || StringUtils.isBlank(elementoUuid)) {
            logger.warn("Alguno de los parametros viene como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando la imagen del elemento del uniforme con uuid [{}]", elementoUuid);

        EmpresaUniformeElemento empresaUniformeElemento = empresaUniformeElementoRepository.findByUuidAndEliminadoFalse(elementoUuid);

        if(empresaUniformeElemento == null) {
            logger.warn("El elemento no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(empresaUniformeElemento.getUbicacionArchivo());
    }

    @Transactional
    @Override
    public EmpresaUniformeElementoDto guardarUniformeElemento(String empresaUuid, String uniformeUuid, String username, EmpresaUniformeElementoDto empresaUniformeElementoDto, MultipartFile multipartFile) {
        if(empresaUniformeElementoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid) || multipartFile == null) {
            logger.warn("El uniforme, la empresa o el usuario estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando un elemento en el uniforme: [{}]", uniformeUuid);

        EmpresaUniforme uniforme = empresaUniformeRepository.findByUuidAndEliminadoFalse(uniformeUuid);

        if(uniforme == null) {
            logger.warn("No se encontro el uniforme en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaUniformeElemento empresaUniformeElemento = dtoToDaoConverter.convertDtoToDaoEmpresaUniformeElemento(empresaUniformeElementoDto);
        empresaUniformeElemento.setUniforme(uniforme.getId());
        empresaUniformeElemento.setElemento(empresaUniformeElementoDto.getElemento().getId());
        daoHelper.fulfillAuditorFields(true, empresaUniformeElemento, usuario.getId());

        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.FOTO_UNIFORME_ELEMENTO, empresaUuid);
            empresaUniformeElemento.setUbicacionArchivo(ruta);
        } catch(Exception ex) {
            logger.warn(ex.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
        EmpresaUniformeElemento empresaUniformeelementoCreado = empresaUniformeElementoRepository.save(empresaUniformeElemento);

        EmpresaUniformeElementoMovimiento empresaUniformeElementoMovimiento = dtoToDaoConverter.convertDtoToDaoEmpresaUniformeElementoMovimiento(empresaUniformeElementoDto.getMovimientos().get(0));
        empresaUniformeElementoMovimiento.setUniformeElemento(empresaUniformeelementoCreado.getId());
        daoHelper.fulfillAuditorFields(true, empresaUniformeElementoMovimiento, usuario.getId());
        empresaUniformeElementoMovimientoRepository.save(empresaUniformeElementoMovimiento);

        return daoToDtoConverter.convertDaoToDtoEmpresaUniformeElemento(empresaUniformeelementoCreado);
    }

    @Transactional
    @Override
    public EmpresaUniformeElementoDto modificarUniformeElemento(String empresaUuid, String uniformeUuid, String elementoUuid, String username, EmpresaUniformeElementoDto empresaUniformeElementoDto, MultipartFile multipartFile) {
        if(empresaUniformeElementoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid) || StringUtils.isBlank(elementoUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Modificando un elemento en el uniforme: [{}]", uniformeUuid);

        EmpresaUniformeElemento empresaUniformeElemento = empresaUniformeElementoRepository.findByUuidAndEliminadoFalse(elementoUuid);
        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(empresaUniformeElemento == null) {
            logger.warn("El elemento del uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaUniformeElemento.setElemento(empresaUniformeElementoDto.getElemento().getId());
        empresaUniformeElemento.setCantidad(empresaUniformeElementoDto.getCantidad());
        daoHelper.fulfillAuditorFields(false, empresaUniformeElemento, usuario.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Eliminando y modificando");
            archivosService.eliminarArchivo(empresaUniformeElemento.getUbicacionArchivo());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.FOTO_UNIFORME_ELEMENTO, empresaUuid);
                empresaUniformeElemento.setUbicacionArchivo(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        EmpresaUniformeElemento empresaUniformeelementoCreado = empresaUniformeElementoRepository.save(empresaUniformeElemento);

        EmpresaUniformeElementoMovimiento empresaUniformeElementoMovimiento = dtoToDaoConverter.convertDtoToDaoEmpresaUniformeElementoMovimiento(empresaUniformeElementoDto.getMovimientos().get(0));
        empresaUniformeElementoMovimiento.setUniformeElemento(empresaUniformeelementoCreado.getId());
        daoHelper.fulfillAuditorFields(true, empresaUniformeElementoMovimiento, usuario.getId());
        empresaUniformeElementoMovimientoRepository.save(empresaUniformeElementoMovimiento);

        return daoToDtoConverter.convertDaoToDtoEmpresaUniformeElemento(empresaUniformeelementoCreado);
    }

    @Override
    public EmpresaUniformeElementoDto eliminarUniformeElemento(String empresaUuid, String uniformeUuid, String elementoUuid, String username) {
        if(StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(uniformeUuid) || StringUtils.isBlank(elementoUuid)) {
            logger.warn("Alguno de los parametros vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el elemento en el uniforme: [{}]", uniformeUuid);

        EmpresaUniformeElemento empresaUniformeElemento = empresaUniformeElementoRepository.findByUuidAndEliminadoFalse(elementoUuid);
        UsuarioDto usuario = usuarioService.getUserByEmail(username);

        if(empresaUniformeElemento == null) {
            logger.warn("El elemento del uniforme no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaUniformeElemento.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaUniformeElemento, usuario.getId());
        EmpresaUniformeElemento empresaUniformeelementoCreado = empresaUniformeElementoRepository.save(empresaUniformeElemento);

        return daoToDtoConverter.convertDaoToDtoEmpresaUniformeElemento(empresaUniformeelementoCreado);
    }
}
