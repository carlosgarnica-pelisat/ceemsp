package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ExisteUsuarioDto;
import com.pelisat.cesp.ceemsp.database.dto.ExisteVehiculoDto;
import com.pelisat.cesp.ceemsp.database.dto.NextRegisterDto;
import com.pelisat.cesp.ceemsp.database.dto.ProximaVisitaDto;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.model.Vehiculo;
import com.pelisat.cesp.ceemsp.database.model.Visita;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.database.repository.VehiculoRepository;
import com.pelisat.cesp.ceemsp.database.repository.VisitaRepository;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoToDtoConverter;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DtoToDaoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicServiceImpl implements PublicService {

    private final EmpresaRepository empresaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DaoToDtoConverter daoToDtoConverter;
    private final VisitaRepository visitaRepository;
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(PublicService.class);

    @Autowired
    public PublicServiceImpl(EmpresaRepository empresaRepository, VehiculoRepository vehiculoRepository,
                             DaoToDtoConverter daoToDtoConverter, VisitaRepository visitaRepository,
                             UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.daoToDtoConverter = daoToDtoConverter;
        this.visitaRepository = visitaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public NextRegisterDto findNextRegister(NextRegisterDto nextRegisterDto) {
        if(nextRegisterDto == null) {
            logger.warn("El dto para consultar el proximo registro viene como nulo o vacio");
            throw new InvalidDataException();
        }

        Empresa empresa = empresaRepository.findFirstByTipoTramiteOrderByFechaCreacionDesc(nextRegisterDto.getTipo());
        NextRegisterDto response = new NextRegisterDto();

        if(empresa == null) {
            response.setNumeroSiguiente("001");
        } else {
            String currentNumber = empresa.getRegistro().split("/")[2];
            int nextNumber = Integer.parseInt(currentNumber);
            nextNumber = nextNumber+1;
            String nextNumberString = Integer.toString(nextNumber);
            while(nextNumberString.length() < 3) {
                nextNumberString = "0" + nextNumberString;
            }
            response.setNumeroSiguiente(nextNumberString);
        }

        return response;
    }

    @Override
    public ExisteVehiculoDto buscarExistenciaVehiculo(ExisteVehiculoDto existeVehiculoDto) {
        if(existeVehiculoDto == null) {
            logger.warn("El objeto para buscar la existencia del vehiculo no existe.");
            throw new InvalidDataException();
        }

        logger.info("Consultando vehiculo con placa [{}] y serie [{}]", existeVehiculoDto.getPlacas(), existeVehiculoDto.getNumeroSerie());
        ExisteVehiculoDto response = new ExisteVehiculoDto();

        Vehiculo vehiculoPorSerie = vehiculoRepository.getBySerieAndEliminadoFalse(existeVehiculoDto.getNumeroSerie());

        if(vehiculoPorSerie != null) {
            logger.info("Se encontro vehiculo por numero de serie");
            response.setExiste(true);
            response.setVehiculo(daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoPorSerie));
            response.setNumeroSerie(existeVehiculoDto.getNumeroSerie());
            return response;
        }

        Vehiculo vehiculoPorPlacas = vehiculoRepository.getByPlacasAndEliminadoFalse(existeVehiculoDto.getPlacas());

        if(vehiculoPorPlacas != null) {
            logger.info("Se encontro vehiculo por placas");
            response.setExiste(true);
            response.setVehiculo(daoToDtoConverter.convertDaoToDtoVehiculo(vehiculoPorPlacas));
            response.setPlacas(existeVehiculoDto.getPlacas());
            return response;
        }

        response.setExiste(false);

        return response;
    }

    @Override
    public ProximaVisitaDto buscarProximaVisita() {
        Visita visita = visitaRepository.findFirstByEliminadoFalseOrderByFechaCreacionDesc();
        ProximaVisitaDto response = new ProximaVisitaDto();

        if(visita == null) {
            response.setNumeroSiguiente("001");
        } else {
            String currentNumber = visita.getNumeroOrden().split("/")[2];
            int nextNumber = Integer.parseInt(currentNumber);
            nextNumber = nextNumber+1;
            String nextNumberString = Integer.toString(nextNumber);
            while(nextNumberString.length() < 3) {
                nextNumberString = "0" + nextNumberString;
            }
            response.setNumeroSiguiente(nextNumberString);
        }

        return response;
    }
}
