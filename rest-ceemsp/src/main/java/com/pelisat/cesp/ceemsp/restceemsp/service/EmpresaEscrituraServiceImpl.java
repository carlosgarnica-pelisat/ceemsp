package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.TipoArchivoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.MissingRelationshipException;
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
public class EmpresaEscrituraServiceImpl implements EmpresaEscrituraService {

    private final Logger logger = LoggerFactory.getLogger(EmpresaEscrituraService.class);
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository;
    private final EmpresaEscrituraRepresentanteRepository empresaEscrituraRepresentanteRepository;
    private final EmpresaEscrituraSocioRepository empresaEscrituraSociosRepository;
    private final EmpresaEscrituraConsejoRepository empresaEscrituraConsejoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;
    private final LocalidadService localidadService;
    private final MunicipioService municipioService;
    private final EstadoService estadoService;

    @Autowired
    public EmpresaEscrituraServiceImpl(
            EmpresaEscrituraRepository empresaEscrituraRepository, EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository,
            EmpresaEscrituraRepresentanteRepository empresaEscrituraRepresentanteRepository, EmpresaRepository empresaRepository,
            DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
            UsuarioService usuarioService, EmpresaEscrituraSocioRepository empresaEscrituraSociosRepository,
            EmpresaEscrituraConsejoRepository empresaEscrituraConsejoRepository, ArchivosService archivosService,
            LocalidadService localidadService, MunicipioService municipioService, EstadoService estadoService
    ) {
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.empresaEscrituraRepresentanteRepository = empresaEscrituraRepresentanteRepository;
        this.empresaEscrituraSociosRepository = empresaEscrituraSociosRepository;
        this.empresaRepository = empresaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaEscrituraConsejoRepository = empresaEscrituraConsejoRepository;
        this.archivosService = archivosService;
        this.localidadService = localidadService;
        this.municipioService = municipioService;
        this.estadoService = estadoService;
    }

    @Override
    public List<EmpresaEscrituraDto> obtenerEscriturasEmpresaPorUuid(String empresaUuid) {
        if(StringUtils.isBlank(empresaUuid)) {
            logger.warn("El uuid de la empresa viene como nulo o vacío");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);

        List<EmpresaEscritura> empresaEscrituras = empresaEscrituraRepository.findAllByEmpresaAndEliminadoFalse(empresa.getId());
        return empresaEscrituras.stream()
                .map(empresaEscritura -> {
                    EmpresaEscrituraDto empresaEscrituraDto = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscritura);
                    empresaEscrituraDto.setEstadoCatalogo(estadoService.obtenerPorId(empresaEscritura.getEstadoCatalogo()));
                    empresaEscrituraDto.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(empresaEscritura.getMunicipioCatalogo()));
                    empresaEscrituraDto.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(empresaEscritura.getLocalidadCatalogo()));

                    List<EmpresaEscrituraSocio> socios = empresaEscrituraSociosRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
                    List<EmpresaEscrituraApoderado> apoderados = empresaEscrituraApoderadoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
                    List<EmpresaEscrituraRepresentante> representantes = empresaEscrituraRepresentanteRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
                    List<EmpresaEscrituraConsejo> consejos = empresaEscrituraConsejoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

                    empresaEscrituraDto.setSocios(socios.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraSocio).collect(Collectors.toList()));
                    empresaEscrituraDto.setApoderados(apoderados.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraApoderado).collect(Collectors.toList()));
                    empresaEscrituraDto.setRepresentantes(representantes.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraRepresentante).collect(Collectors.toList()));
                    empresaEscrituraDto.setConsejos(consejos.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraConsejo).collect(Collectors.toList()));
                    return empresaEscrituraDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaEscrituraDto obtenerEscrituraPorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la escritura con el uuid [{}]", escrituraUuid);

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);
        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("La escritura no existe con el uuid [{}]", escrituraUuid);
            throw new NotFoundResourceException();
        }

        if(empresaEscritura.getEmpresa() != empresa.getId()) {
            logger.warn("La escritura no pertenece a la empresa");
            throw new MissingRelationshipException();
        }

        EmpresaEscrituraDto response = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscritura);

        if(!soloEntidad) {
            logger.info("Descargando informacion de los integrantes");
            List<EmpresaEscrituraSocio> socios = empresaEscrituraSociosRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraApoderado> apoderados = empresaEscrituraApoderadoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraRepresentante> representantes = empresaEscrituraRepresentanteRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraConsejo> consejos = empresaEscrituraConsejoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

            response.setSocios(socios.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraSocio).collect(Collectors.toList()));
            response.setApoderados(apoderados.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraApoderado).collect(Collectors.toList()));
            response.setRepresentantes(representantes.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraRepresentante).collect(Collectors.toList()));
            response.setConsejos(consejos.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraConsejo).collect(Collectors.toList()));

            logger.info("Agregando catalogos de domicilio");
            response.setLocalidadCatalogo(localidadService.obtenerLocalidadPorId(empresaEscritura.getLocalidadCatalogo()));
            response.setEstadoCatalogo(estadoService.obtenerPorId(empresaEscritura.getEstadoCatalogo()));
            response.setMunicipioCatalogo(municipioService.obtenerMunicipioPorId(empresaEscritura.getMunicipioCatalogo()));
        }

        return response;
    }

    @Override
    @Transactional
    public EmpresaEscrituraDto guardarEscritura(String empresaUuid, EmpresaEscrituraDto empresaEscrituraDto,
                                                String username, MultipartFile multipartFile) {
        if(StringUtils.isBlank(empresaUuid) || empresaEscrituraDto == null || StringUtils.isBlank(username) || multipartFile == null) {
            logger.warn("El uuid o la escritura a crear vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.getByUuidAndEliminadoFalse(empresaUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        EmpresaEscritura empresaEscritura = dtoToDaoConverter.convertDtoToDaoEmpresaEscritura(empresaEscrituraDto);
        empresaEscritura.setEmpresa(empresa.getId());
        empresaEscritura.setEstadoCatalogo(empresaEscrituraDto.getEstadoCatalogo().getId());
        empresaEscritura.setMunicipioCatalogo(empresaEscrituraDto.getMunicipioCatalogo().getId());
        empresaEscritura.setLocalidadCatalogo(empresaEscrituraDto.getLocalidadCatalogo().getId());
        empresaEscritura.setFechaEscritura(LocalDate.parse(empresaEscrituraDto.getFechaEscritura()));
        daoHelper.fulfillAuditorFields(true, empresaEscritura, usuarioDto.getId());
        String ruta = "";
        try {
            ruta = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.ESCRITURA, empresaUuid);
            empresaEscritura.setRutaArchivo(ruta);
            EmpresaEscritura empresaEscrituraCreada = empresaEscrituraRepository.save(empresaEscritura);
            if(empresaEscrituraDto.getApoderados() != null && empresaEscrituraDto.getApoderados().size() > 0) {
                logger.info("Se encontraron apoderados. Agregando [{}] nuevos apoderados",
                        empresaEscrituraDto.getApoderados().size());
                List<EmpresaEscrituraApoderado> empresaEscrituraApoderados = empresaEscrituraDto.getApoderados()
                        .stream()
                        .map(ap -> {
                            EmpresaEscrituraApoderado eea = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraApoderado(ap);
                            eea.setEscritura(empresaEscrituraCreada.getId());
                            eea.setFechaInicio(LocalDate.parse(ap.getFechaInicio()));
                            eea.setFechaFin(LocalDate.parse(ap.getFechaFin()));
                            daoHelper.fulfillAuditorFields(true, eea, usuarioDto.getId());
                            return eea;
                        }).collect(Collectors.toList());

                empresaEscrituraApoderadoRepository.saveAll(empresaEscrituraApoderados);
            }

            if(empresaEscrituraDto.getSocios() != null && empresaEscrituraDto.getSocios().size() > 0) {
                logger.info("Se encontraron socios. Agregando [{}] nuevos socios",
                        empresaEscrituraDto.getSocios().size());

                List<EmpresaEscrituraSocio> empresaEscrituraSocios = empresaEscrituraDto.getSocios()
                        .stream()
                        .map(so -> {
                            EmpresaEscrituraSocio ees = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraSocio(so);
                            ees.setEscritura(empresaEscrituraCreada.getId());
                            daoHelper.fulfillAuditorFields(true, ees, usuarioDto.getId());
                            return ees;
                        }).collect(Collectors.toList());

                empresaEscrituraSociosRepository.saveAll(empresaEscrituraSocios);
            }

            if(empresaEscrituraDto.getRepresentantes() != null && empresaEscrituraDto.getRepresentantes().size() > 0) {
                logger.info("Se encontraron representantes. Agregando [{}] nuevos representantes",
                        empresaEscrituraDto.getSocios().size());

                List<EmpresaEscrituraRepresentante> empresaEscrituraRepresentantes = empresaEscrituraDto.getRepresentantes()
                        .stream()
                        .map(re -> {
                            EmpresaEscrituraRepresentante eer = dtoToDaoConverter.convertDtoToDaoEmpresaRepresentante(re);
                            eer.setEscritura(empresaEscrituraCreada.getId());
                            daoHelper.fulfillAuditorFields(true, eer, usuarioDto.getId());
                            return eer;
                        }).collect(Collectors.toList());

                empresaEscrituraRepresentanteRepository.saveAll(empresaEscrituraRepresentantes);
            }

            if(empresaEscrituraDto.getConsejos() != null && empresaEscrituraDto.getConsejos().size() > 0) {
                logger.info("Se encontraron miembros del consejo. Agregando [{}] nuevos consejos", empresaEscrituraDto.getConsejos().size());

                List<EmpresaEscrituraConsejo> empresaEscrituraConsejos = empresaEscrituraDto.getConsejos()
                        .stream()
                        .map(co -> {
                            EmpresaEscrituraConsejo eec = dtoToDaoConverter.convertDtoToDaoEmpresaEscrituraConsejo(co);
                            eec.setEscritura(empresaEscrituraCreada.getId());
                            daoHelper.fulfillAuditorFields(true, eec, usuarioDto.getId());
                            return eec;
                        }).collect(Collectors.toList());

                empresaEscrituraConsejoRepository.saveAll(empresaEscrituraConsejos);
            }

            if(!empresa.isEscriturasCapturadas()) {
                empresa.setEscriturasCapturadas(true);
                daoHelper.fulfillAuditorFields(false, empresa, usuarioDto.getId());
                empresaRepository.save(empresa);
            }

            EmpresaEscrituraDto response = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscrituraCreada);
            String[] tokens = empresaEscrituraCreada.getRutaArchivo().split("[\\\\|/]");
            response.setNombreArchivo(tokens[tokens.length - 1]);
            response.setLocalidadCatalogo(empresaEscrituraDto.getLocalidadCatalogo());
            response.setMunicipioCatalogo(empresaEscrituraDto.getMunicipioCatalogo());
            response.setEstadoCatalogo(empresaEscrituraDto.getEstadoCatalogo());
            return response;
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
            archivosService.eliminarArchivo(ruta);
            throw new InvalidDataException();
        }
    }

    @Transactional
    @Override
    public EmpresaEscrituraDto modificarEscritura(String empresaUuid, String escrituraUuid, EmpresaEscrituraDto empresaEscrituraDto, MultipartFile multipartFile, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username) || empresaEscrituraDto == null) {
            logger.warn("El uuid de la empresa, la escritura, el usuario o la escritura a modificar vienen como nulas o vacias");
            throw new InvalidDataException();
        }

        logger.info("Modificando la escritura con el uuid [{}]", escrituraUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);
        if(empresaEscritura == null) {
            logger.warn("La empresa no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        empresaEscritura.setNumeroEscritura(empresaEscrituraDto.getNumeroEscritura());
        empresaEscritura.setNombreFedatario(empresaEscrituraDto.getNombreFedatario());
        empresaEscritura.setApellidoPaterno(empresaEscrituraDto.getApellidoPaterno());
        empresaEscritura.setApellidoMaterno(empresaEscrituraDto.getApellidoMaterno());
        empresaEscritura.setCurp(empresaEscrituraDto.getCurp());
        empresaEscritura.setTipoFedatario(empresaEscrituraDto.getTipoFedatario());
        empresaEscritura.setNumero(empresaEscrituraDto.getNumero());
        empresaEscritura.setCiudad(empresaEscrituraDto.getCiudad());
        empresaEscritura.setDescripcion(empresaEscrituraDto.getDescripcion());

        empresaEscritura.setLocalidadCatalogo(empresaEscrituraDto.getLocalidadCatalogo().getId());
        empresaEscritura.setMunicipioCatalogo(empresaEscrituraDto.getMunicipioCatalogo().getId());
        empresaEscritura.setEstadoCatalogo(empresaEscrituraDto.getEstadoCatalogo().getId());

        daoHelper.fulfillAuditorFields(false, empresaEscritura, usuarioDto.getId());

        if(multipartFile != null) {
            logger.info("Se subio con un archivo. Eliminando y modificando");
            archivosService.eliminarArchivo(empresaEscritura.getRutaArchivo());
            String rutaArchivoNuevo = "";
            try {
                rutaArchivoNuevo = archivosService.guardarArchivoMultipart(multipartFile, TipoArchivoEnum.ESCRITURA, empresaUuid);
                empresaEscritura.setRutaArchivo(rutaArchivoNuevo);
            } catch(Exception ex) {
                logger.warn("No se ha podido guardar el archivo. {}", ex);
                throw new InvalidDataException();
            }
        }

        empresaEscrituraRepository.save(empresaEscritura);
        EmpresaEscrituraDto response = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscritura);
        String[] tokens = empresaEscritura.getRutaArchivo().split("[\\\\|/]");
        response.setNombreArchivo(tokens[tokens.length - 1]);
        response.setLocalidadCatalogo(empresaEscrituraDto.getLocalidadCatalogo());
        response.setMunicipioCatalogo(empresaEscrituraDto.getMunicipioCatalogo());
        response.setEstadoCatalogo(empresaEscrituraDto.getEstadoCatalogo());
        return response;
    }

    @Transactional
    @Override
    public EmpresaEscrituraDto eliminarEscritura(String empresaUuid, String escrituraUuid, String username) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid) || StringUtils.isBlank(username)) {
            logger.warn("Alguno de los parametros viene como nulo o vacio");
            throw new InvalidDataException();
        }

        logger.info("Se esta eliminando la escritura con el uuid [{}]", escrituraUuid);

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);
        EmpresaEscritura escritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);
        if(escritura == null) {
            logger.warn("La escritura no se encuentra en la base de datos");
            throw new NotFoundResourceException();
        }

        escritura.setEliminado(true);
        daoHelper.fulfillAuditorFields(false, escritura, usuarioDto.getId());
        empresaEscrituraRepository.save(escritura);

        logger.info("Escritura eliminada correctamente. Eliminando sus relaciones como socios, representantes y tal");

        List<EmpresaEscrituraSocio> empresaEscrituraSocios = empresaEscrituraSociosRepository.findAllByEscrituraAndEliminadoFalse(escritura.getId());
        List<EmpresaEscrituraApoderado> empresaEscrituraApoderados = empresaEscrituraApoderadoRepository.findAllByEscrituraAndEliminadoFalse(escritura.getId());
        List<EmpresaEscrituraRepresentante> empresaEscrituraRepresentantes = empresaEscrituraRepresentanteRepository.findAllByEscrituraAndEliminadoFalse(escritura.getId());
        List<EmpresaEscrituraConsejo> empresaEscrituraConsejos = empresaEscrituraConsejoRepository.findAllByEscrituraAndEliminadoFalse(escritura.getId());

        if(empresaEscrituraSocios != null || empresaEscrituraSocios.size() > 0) {
            logger.info("Socios encontrados. Eliminandolos");
            empresaEscrituraSociosRepository.saveAll(empresaEscrituraSocios.stream().map(s -> {
                daoHelper.fulfillAuditorFields(false, s, usuarioDto.getId());
                s.setEliminado(true);
                return s;
            }).collect(Collectors.toList()));
        }

        return daoToDtoConverter.convertDaoToDtoEmpresaEscritura(escritura);
    }

    @Override
    public File obtenerEscrituraPdf(String empresaUuid, String escrituraUuid) {
        if(StringUtils.isBlank(empresaUuid) || StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Descargando las escrituras en PDF para la escritura [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("La escritura no fue encontrada en la base de datos");
            throw new NotFoundResourceException();
        }

        return new File(empresaEscritura.getRutaArchivo());
    }
}
