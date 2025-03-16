package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraConsejoDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraConsejoRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaEscrituraRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaEscrituraConsejoServiceImpl implements EmpresaEscrituraConsejoService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraConsejoService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final UsuarioService usuarioService;
    private final EmpresaEscrituraConsejoRepository empresaEscrituraConsejoRepository;
    private final EmpresaService empresaService;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final ArchivosService archivosService;

    @Autowired
    public EmpresaEscrituraConsejoServiceImpl(DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter,
                                              DaoHelper<CommonModel> daoHelper, UsuarioService usuarioService,
                                              EmpresaEscrituraConsejoRepository empresaEscrituraConsejoRepository,
                                              EmpresaService empresaService, EmpresaEscrituraRepository empresaEscrituraRepository,
                                              ArchivosService archivosService) {
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaEscrituraConsejoRepository = empresaEscrituraConsejoRepository;
        this.empresaService = empresaService;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.archivosService = archivosService;
    }

    @Override
    public List<EmpresaEscrituraConsejoDto> obtenerConsejosPorEscritura(String empresaUuid, String escrituraUuid) {
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

        List<EmpresaEscrituraConsejo> empresaEscrituraConsejos = empresaEscrituraConsejoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

        return empresaEscrituraConsejos.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraConsejo)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaEscrituraConsejoDto> obtenerTodosConsejosPorEscritura(String empresaUuid, String escrituraUuid) {
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

        List<EmpresaEscrituraConsejo> empresaEscrituraConsejos = empresaEscrituraConsejoRepository.findAllByEscritura(empresaEscritura.getId());

        return empresaEscrituraConsejos.stream()
                .map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraConsejo)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraConsejoDto obtenerConsejoPorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad) {
        return null;
    }

    @Override
    @Transactional
    public EmpresaEscrituraConsejoDto crearConsejo(String empresaUuid, String escrituraUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto) {
        if(empresaEscrituraConsejoDto == null || StringUtils.isBlank(username) || StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El consejo, la empresa o la escritura estan viniendo como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Creando consejo en la escritura: [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("No se encontro la escritura en la base de datos");
            throw new NotFoundResourceException();
        }

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraConsejo empresaEscrituraConsejo = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraConsejo(empresaEscrituraConsejoDto);
        empresaEscrituraConsejo.setEscritura(empresaEscritura.getId());
        daoHelper.fulfillAuditorFields(true, empresaEscrituraConsejo, usuario.getId());
        EmpresaEscrituraConsejo empresaEscrituraConsejoCreado = empresaEscrituraConsejoRepository.save(empresaEscrituraConsejo);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraConsejo(empresaEscrituraConsejoCreado);
    }

    @Override
    @Transactional
    public EmpresaEscrituraConsejoDto actualizarConsejo(String empresaUuid, String escrituraUuid, String consejoUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || StringUtils.isBlank(consejoUuid) || empresaEscrituraConsejoDto == null) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta modificando el consejo de la escritura con el uuid [{}]", consejoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraConsejo empresaEscrituraConsejo = empresaEscrituraConsejoRepository.findByUuidAndEliminadoFalse(consejoUuid);

        if(empresaEscrituraConsejo == null) {
            logger.warn("El consejo a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraConsejo.setNombres(empresaEscrituraConsejoDto.getNombres());
        empresaEscrituraConsejo.setApellidos(empresaEscrituraConsejoDto.getApellidos());
        empresaEscrituraConsejo.setApellidoMaterno(empresaEscrituraConsejoDto.getApellidoMaterno());
        empresaEscrituraConsejo.setCurp(empresaEscrituraConsejoDto.getCurp());
        empresaEscrituraConsejo.setSexo(empresaEscrituraConsejoDto.getSexo());
        empresaEscrituraConsejo.setPuesto(empresaEscrituraConsejoDto.getPuesto());
        daoHelper.fulfillAuditorFields(false, empresaEscrituraConsejo, usuario.getId());
        empresaEscrituraConsejoRepository.save(empresaEscrituraConsejo);

        return empresaEscrituraConsejoDto;
    }

    @Transactional
    @Override
    public EmpresaEscrituraConsejoDto eliminarConsejo(String empresaUuid, String escrituraUuid, String consejoUuid, String username, EmpresaEscrituraConsejoDto empresaEscrituraConsejoDto, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(consejoUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o invalido");
            throw new InvalidDataException();
        }

        logger.info("Eliminando el consejo con el ID [{}]", consejoUuid);

        UsuarioDto usuario = usuarioService.getUserByEmail(username);
        EmpresaEscrituraConsejo empresaEscrituraConsejo = empresaEscrituraConsejoRepository.findByUuidAndEliminadoFalse(consejoUuid);

        if(empresaEscrituraConsejo == null) {
            logger.warn("El consejo a modificar no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscrituraConsejo.setObservacionesBaja(empresaEscrituraConsejoDto.getObservacionesBaja());
        empresaEscrituraConsejo.setFechaBaja(LocalDate.parse(empresaEscrituraConsejoDto.getFechaBaja()));
        empresaEscrituraConsejo.setMotivoBaja(empresaEscrituraConsejoDto.getMotivoBaja());
        empresaEscrituraConsejo.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, empresaEscrituraConsejo, usuario.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Agregando");
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.DOCUMENTO_FUNDATORIO_BAJA_CONSEJO, empresaUuid);
                empresaEscrituraConsejo.setDocumentoFundatorioBaja(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        empresaEscrituraConsejoRepository.save(empresaEscrituraConsejo);

        return daoToDtoConverter.convertDaoToDtoEmpresaEscrituraConsejo(empresaEscrituraConsejo);
    }

    @Override
    public File obtenerDocumentoFundatorioBajaConsejo(String empresaUuid, String escrituraUuid, String consejoUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(consejoUuid)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Descargando el documento fundatorio con uuid [{}]", consejoUuid);
        EmpresaEscrituraConsejo consejo = empresaEscrituraConsejoRepository.findByUuid(consejoUuid);

        if(consejo == null) {
            logger.warn("La fotografia esta eliminada o no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        if(!consejo.getEliminado()) {
            logger.warn("El elemento no se encuentra eliminado");
            throw new NotFoundResourceException();
        }

        return new File(consejo.getDocumentoFundatorioBaja());
    }
}
