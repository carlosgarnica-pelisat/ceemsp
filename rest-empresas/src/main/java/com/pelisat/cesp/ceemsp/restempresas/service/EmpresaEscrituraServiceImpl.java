package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaEscrituraDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
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

import java.io.File;
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
    private final UsuarioService usuarioService;
    private final DaoToDtoConverter daoToDtoConverter;
    private final DtoToDaoConverter dtoToDaoConverter;
    private final DaoHelper<CommonModel> daoHelper;
    private final ArchivosService archivosService;
    private final CatalogoService catalogoService;

    @Autowired
    public EmpresaEscrituraServiceImpl(
            EmpresaEscrituraRepository empresaEscrituraRepository, EmpresaEscrituraApoderadoRepository empresaEscrituraApoderadoRepository,
            EmpresaEscrituraRepresentanteRepository empresaEscrituraRepresentanteRepository,
            DaoToDtoConverter daoToDtoConverter, DtoToDaoConverter dtoToDaoConverter, DaoHelper<CommonModel> daoHelper,
            UsuarioService usuarioService, EmpresaEscrituraSocioRepository empresaEscrituraSociosRepository,
            EmpresaEscrituraConsejoRepository empresaEscrituraConsejoRepository, ArchivosService archivosService,
            CatalogoService catalogoService
    ) {
        this.empresaEscrituraApoderadoRepository = empresaEscrituraApoderadoRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.empresaEscrituraRepresentanteRepository = empresaEscrituraRepresentanteRepository;
        this.empresaEscrituraSociosRepository = empresaEscrituraSociosRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.dtoToDaoConverter = dtoToDaoConverter;
        this.daoHelper = daoHelper;
        this.usuarioService = usuarioService;
        this.empresaEscrituraConsejoRepository = empresaEscrituraConsejoRepository;
        this.archivosService = archivosService;
        this.catalogoService = catalogoService;
    }

    @Override
    public List<EmpresaEscrituraDto> obtenerEscrituras(String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El uuid de la empresa viene como nulo o vacío");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        List<EmpresaEscritura> empresaEscrituras = empresaEscrituraRepository.findAllByEmpresaAndEliminadoFalse(usuarioDto.getEmpresa().getId());
        return empresaEscrituras.stream()
                .map(empresaEscritura -> {
                    EmpresaEscrituraDto empresaEscrituraDto = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscritura);
                    empresaEscrituraDto.setEstadoCatalogo(catalogoService.obtenerEstadoPorId(empresaEscritura.getEstadoCatalogo()));
                    empresaEscrituraDto.setMunicipioCatalogo(catalogoService.obtenerMunicipioPorId(empresaEscritura.getMunicipioCatalogo()));
                    empresaEscrituraDto.setLocalidadCatalogo(catalogoService.obtenerLocalidadPorId(empresaEscritura.getLocalidadCatalogo()));

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
    public EmpresaEscrituraDto obtenerEscrituraPorUuid(String escrituraUuid, boolean soloEntidad) {
        if(StringUtils.isBlank(escrituraUuid)) {
            logger.warn("El uuid de la empresa o de la escritura vienen como nulos o vacios");
            throw new InvalidDataException();
        }

        logger.info("Obteniendo la escritura con el uuid [{}]", escrituraUuid);

        EmpresaEscritura empresaEscritura = empresaEscrituraRepository.findByUuidAndEliminadoFalse(escrituraUuid);

        if(empresaEscritura == null) {
            logger.warn("La escritura no existe con el uuid [{}]", escrituraUuid);
            throw new NotFoundResourceException();
        }

        EmpresaEscrituraDto response = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(empresaEscritura);

        if(!soloEntidad) {
            logger.info("Descargando informacion de los socios");
            List<EmpresaEscrituraSocio> socios = empresaEscrituraSociosRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraApoderado> apoderados = empresaEscrituraApoderadoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraRepresentante> representantes = empresaEscrituraRepresentanteRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());
            List<EmpresaEscrituraConsejo> consejos = empresaEscrituraConsejoRepository.findAllByEscrituraAndEliminadoFalse(empresaEscritura.getId());

            response.setSocios(socios.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraSocio).collect(Collectors.toList()));
            response.setApoderados(apoderados.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraApoderado).collect(Collectors.toList()));
            response.setRepresentantes(representantes.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraRepresentante).collect(Collectors.toList()));
            response.setConsejos(consejos.stream().map(daoToDtoConverter::convertDaoToDtoEmpresaEscrituraConsejo).collect(Collectors.toList()));
        }

        return response;
    }

    @Override
    public File obtenerEscrituraPdf(String escrituraUuid) {
        if(StringUtils.isBlank(escrituraUuid)) {
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
