package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.database.type.SexoEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusquedaServiceImpl implements BusquedaService {
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    private final CanRepository canRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ArmaRepository armaRepository;
    private final EmpresaEscrituraRepository empresaEscrituraRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final Logger logger = LoggerFactory.getLogger(BusquedaService.class);
    private final DaoToDtoConverter daoToDtoConverter;
    private final VehiculoTipoRepository vehiculoTipoRepository;
    private final VehiculoMarcaRepository vehiculoMarcaRepository;
    private final VehiculoSubmarcaRepository vehiculoSubmarcaRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public BusquedaServiceImpl(EmpresaRepository empresaRepository, ClienteRepository clienteRepository,
                               PersonaRepository personaRepository, CanRepository canRepository,
                               VehiculoRepository vehiculoRepository, ArmaRepository armaRepository,
                               EmpresaEscrituraRepository empresaEscrituraRepository, IncidenciaRepository incidenciaRepository,
                               DaoToDtoConverter daoToDtoConverter, UsuarioService usuarioService,
                               VehiculoTipoRepository vehiculoTipoRepository,
                               VehiculoMarcaRepository vehiculoMarcaRepository,
                               VehiculoSubmarcaRepository vehiculoSubmarcaRepository) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.personaRepository = personaRepository;
        this.canRepository = canRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.armaRepository = armaRepository;
        this.empresaEscrituraRepository = empresaEscrituraRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.vehiculoTipoRepository = vehiculoTipoRepository;
        this.vehiculoMarcaRepository = vehiculoMarcaRepository;
        this.vehiculoSubmarcaRepository = vehiculoSubmarcaRepository;
        this.usuarioService = usuarioService;
    }
    @Override
    public ResultadosBusquedaDto realizarBusqueda(RealizarBusquedaDto busquedaDto, String username) {
        if(StringUtils.isBlank(username)) {
            logger.warn("El usuario viene como nulo o vacio");
            throw new InvalidDataException();
        }

        UsuarioDto usuarioDto = usuarioService.getUserByEmail(username);

        ResultadosBusquedaDto resultadosBusquedaDto = new ResultadosBusquedaDto();
        resultadosBusquedaDto.setFiltro(busquedaDto.getFiltro());
        resultadosBusquedaDto.setPalabraABuscar(busquedaDto.getPalabraABuscar());

        switch(busquedaDto.getFiltro()) {
            case CLIENTES:
                List<Cliente> clientesPorRazonSocialONombreComercial = clienteRepository.findAllByRazonSocialContainingOrNombreComercialContainingAndEmpresa(busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Cliente> clientesPorRfc = clienteRepository.findAllByRfcContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());

                List<Cliente> clienteResults = new ArrayList<>();
                clienteResults.addAll(clientesPorRazonSocialONombreComercial);
                clienteResults.addAll(clientesPorRfc);

                resultadosBusquedaDto.setResultadosBusquedaClientes(clienteResults.stream().distinct().map(c -> {
                    ClienteDto dto = daoToDtoConverter.convertDaoToDtoCliente(c);
                    dto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(c.getEmpresa())));
                    return dto;
                }).collect(Collectors.toList()));
                break;
            case PERSONAL:
                List<Personal> personalPorNombreCompleto = personaRepository.findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombresContainingAndEmpresa(busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Personal> personalPorRfc = personaRepository.findAllByRfcContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Personal> personalPorCurp = personaRepository.findAllByCurpContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Personal> personalPorCuip = personaRepository.findAllByCuipContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Personal> personalPorSexo = new ArrayList<>();
                try {
                    personalPorSexo = personaRepository.findAllBySexo(SexoEnum.valueOf(busquedaDto.getPalabraABuscar()));
                } catch(IllegalArgumentException ex) {}

                List<Personal> personalResults = new ArrayList<>();
                personalResults.addAll(personalPorNombreCompleto);
                personalResults.addAll(personalPorRfc);
                personalResults.addAll(personalPorCurp);
                personalResults.addAll(personalPorCuip);
                personalResults.addAll(personalPorSexo);

                resultadosBusquedaDto.setResultadosBusquedaPersona(personalResults.stream()
                        .distinct()
                        .map(p -> {
                            PersonaDto dto = daoToDtoConverter.convertDaoToDtoPersona(p);
                            dto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(p.getEmpresa())));
                            return dto;
                        }).collect(Collectors.toList()));
                break;
            case CANES:
                List<Can> canPorNombre = canRepository.findAllByNombreContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());

                List<Can> canResults = new ArrayList<>();
                canResults.addAll(canPorNombre);

                resultadosBusquedaDto.setResultadosBusquedaCan(canResults.stream()
                        .distinct()
                        .map(p -> {
                            CanDto dto = daoToDtoConverter.convertDaoToDtoCan(p);
                            dto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(p.getEmpresa())));
                            return dto;
                        }).collect(Collectors.toList()));
                break;
            case VEHICULOS:
                List<Vehiculo> vehiculoPorPlacas = vehiculoRepository.findAllByPlacasContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Vehiculo> vehiculoPorSerie = vehiculoRepository.findAllBySerieContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Vehiculo> vehiculoPorNumeroHolograma = vehiculoRepository.findAllByNumeroHologramaContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Vehiculo> vehiculoPorEmpresaBlindaje = vehiculoRepository.findAllByEmpresaBlindajeContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());

                List<Vehiculo> vehiculoResults = new ArrayList<>();
                vehiculoResults.addAll(vehiculoPorPlacas);
                vehiculoResults.addAll(vehiculoPorSerie);
                vehiculoResults.addAll(vehiculoPorNumeroHolograma);
                vehiculoResults.addAll(vehiculoPorEmpresaBlindaje);

                resultadosBusquedaDto.setResultadosBusquedaVehiculo(vehiculoResults.stream()
                        .distinct()
                        .map(v -> {
                            VehiculoDto dto = daoToDtoConverter.convertDaoToDtoVehiculo(v);
                            dto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(v.getEmpresa())));
                            dto.setTipo(daoToDtoConverter.convertDaoToDtoVehiculoTipo(vehiculoTipoRepository.getOne(v.getTipo())));
                            dto.setMarca(daoToDtoConverter.convertDaoToDtoVehiculoMarca(vehiculoMarcaRepository.getOne(v.getMarca())));
                            if(v.getSubmarca() > 0) {
                                dto.setSubmarca(daoToDtoConverter.convertDaoToDtoVehiculoSubmarca(vehiculoSubmarcaRepository.getOne(v.getSubmarca())));
                            }
                            return dto;
                        }).collect(Collectors.toList()));
                break;
            case ARMAS:
                List<Arma> armasPorSerie = armaRepository.findAllBySerieContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<Arma> armasPorMatricula = armaRepository.findAllByMatriculaContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());

                List<Arma> armaResults = new ArrayList<>();
                armaResults.addAll(armasPorMatricula);
                armaResults.addAll(armasPorSerie);

                resultadosBusquedaDto.setResultadosBusquedaArma(armaResults.stream()
                        .distinct()
                        .map(a -> {
                            ArmaDto dto = daoToDtoConverter.convertDaoToDtoArma(a);
                            dto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(a.getEmpresa())));
                            return dto;
                        }).collect(Collectors.toList()));
                break;
            case ESCRITURAS:
                List<EmpresaEscritura> escriturasPorNumero = empresaEscrituraRepository.findAllByNumeroContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                List<EmpresaEscritura> escriturasPorNombres = empresaEscrituraRepository.findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombreFedatarioContainingAndEmpresa(busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());

                List<EmpresaEscritura> escrituraResults = new ArrayList<>();
                escrituraResults.addAll(escriturasPorNumero);
                escrituraResults.addAll(escriturasPorNombres);

                resultadosBusquedaDto.setResultadosBusquedaEscrituras(escrituraResults.stream()
                        .distinct()
                        .map(e -> {
                            EmpresaEscrituraDto dto = daoToDtoConverter.convertDaoToDtoEmpresaEscritura(e);
                            dto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(e.getEmpresa())));
                            return dto;
                        }).collect(Collectors.toList()));
                break;
            case INCIDENCIAS:
                List<Incidencia> coincidenciasPorNumero = incidenciaRepository.findAllByNumeroContainingAndEmpresa(busquedaDto.getPalabraABuscar(), usuarioDto.getEmpresa().getId());
                resultadosBusquedaDto.setResultadosBusquedaIncidencias(coincidenciasPorNumero.stream()
                        .distinct()
                        .map(i -> {
                            IncidenciaDto dto = daoToDtoConverter.convertDaoToDtoIncidencia(i);
                            dto.setEmpresa(daoToDtoConverter.convertDaoToDtoEmpresa(empresaRepository.getOne(i.getEmpresa())));
                            return dto;
                        }).collect(Collectors.toList()));
                break;
        }

        return resultadosBusquedaDto;
    }
}
