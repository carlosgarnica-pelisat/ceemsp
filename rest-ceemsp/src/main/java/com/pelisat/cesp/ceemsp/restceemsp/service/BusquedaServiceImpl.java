package com.pelisat.cesp.ceemsp.restceemsp.service;

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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    public BusquedaServiceImpl(EmpresaRepository empresaRepository, ClienteRepository clienteRepository,
                               PersonaRepository personaRepository, CanRepository canRepository,
                               VehiculoRepository vehiculoRepository, ArmaRepository armaRepository,
                               EmpresaEscrituraRepository empresaEscrituraRepository, IncidenciaRepository incidenciaRepository,
                               DaoToDtoConverter daoToDtoConverter,
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
    }

    @Override
    public ResultadosBusquedaDto realizarBusqueda(RealizarBusquedaDto busquedaDto) {
        if(busquedaDto == null) {
            logger.warn("Parametros invalidos");
            throw new InvalidDataException();
        }

        ResultadosBusquedaDto resultadosBusquedaDto = new ResultadosBusquedaDto();
        resultadosBusquedaDto.setFiltro(busquedaDto.getFiltro());
        resultadosBusquedaDto.setPalabraABuscar(busquedaDto.getPalabraABuscar());

        switch(busquedaDto.getFiltro()) {
            case EMPRESAS:
                List<Empresa> empresaPorRazonSocialONombreComercial = empresaRepository.findAllByRazonSocialContainingOrNombreComercialContaining(busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar());
                List<Empresa> empresaPorNumeroRegistro = empresaRepository.findAllByRegistroContaining(busquedaDto.getPalabraABuscar());
                List<Empresa> empresaPorRfc = empresaRepository.findAllByRfcContaining(busquedaDto.getPalabraABuscar());
                List<Empresa> empresaPorCurp = empresaRepository.findAllByCurpContaining(busquedaDto.getPalabraABuscar());
                List<Empresa> empresaPorCorreo = empresaRepository.findAllByCorreoElectronicoContaining(busquedaDto.getPalabraABuscar());
                List<Empresa> empresaPorRegistroFederal = empresaRepository.findAllByRegistroFederalContaining(busquedaDto.getPalabraABuscar());

                List<Empresa> empresaResults = new ArrayList<>();
                empresaResults.addAll(empresaPorRazonSocialONombreComercial);
                empresaResults.addAll(empresaPorNumeroRegistro);
                empresaResults.addAll(empresaPorRfc);
                empresaResults.addAll(empresaPorCurp);
                empresaResults.addAll(empresaPorCorreo);
                empresaResults.addAll(empresaPorRegistroFederal);

                resultadosBusquedaDto.setResultadosBusquedaEmpresas(empresaResults.stream().distinct().map(daoToDtoConverter::convertDaoToDtoEmpresa).collect(Collectors.toList()));
                break;
            case CLIENTES:
                List<Cliente> clientesPorRazonSocialONombreComercial = clienteRepository.findAllByRazonSocialContainingOrNombreComercialContaining(busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar());
                List<Cliente> clientesPorRfc = clienteRepository.findAllByRfcContaining(busquedaDto.getPalabraABuscar());

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
                List<Personal> personalPorNombreCompleto = personaRepository.findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombresContaining(busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar());
                List<Personal> personalPorRfc = personaRepository.findAllByRfcContaining(busquedaDto.getPalabraABuscar());
                List<Personal> personalPorCurp = personaRepository.findAllByCurpContaining(busquedaDto.getPalabraABuscar());
                List<Personal> personalPorCuip = personaRepository.findAllByCuipContaining(busquedaDto.getPalabraABuscar());
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
                List<Can> canPorNombre = canRepository.findAllByNombreContaining(busquedaDto.getPalabraABuscar());

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
                List<Vehiculo> vehiculoPorPlacas = vehiculoRepository.findAllByPlacasContaining(busquedaDto.getPalabraABuscar());
                List<Vehiculo> vehiculoPorSerie = vehiculoRepository.findAllBySerieContaining(busquedaDto.getPalabraABuscar());
                List<Vehiculo> vehiculoPorNumeroHolograma = vehiculoRepository.findAllByNumeroHologramaContaining(busquedaDto.getPalabraABuscar());
                List<Vehiculo> vehiculoPorEmpresaBlindaje = vehiculoRepository.findAllByEmpresaBlindajeContaining(busquedaDto.getPalabraABuscar());

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
                List<Arma> armasPorSerie = armaRepository.findAllBySerieContaining(busquedaDto.getPalabraABuscar());
                List<Arma> armasPorMatricula = armaRepository.findAllByMatriculaContaining(busquedaDto.getPalabraABuscar());

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
                List<EmpresaEscritura> escriturasPorNumero = empresaEscrituraRepository.findAllByNumeroContaining(busquedaDto.getPalabraABuscar());
                List<EmpresaEscritura> escriturasPorNombres = empresaEscrituraRepository.findAllByApellidoPaternoContainingOrApellidoMaternoContainingOrNombreFedatarioContaining(busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar(), busquedaDto.getPalabraABuscar());

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
                List<Incidencia> coincidenciasPorNumero = incidenciaRepository.findAllByNumeroContaining(busquedaDto.getPalabraABuscar());
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
