package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.google.gson.Gson;
import com.pelisat.cesp.ceemsp.database.dto.MovimientoDto;
import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaFormaEjecucionRepository;
import com.pelisat.cesp.ceemsp.database.repository.EmpresaRepository;
import com.pelisat.cesp.ceemsp.database.repository.UsuarioRepository;
import com.pelisat.cesp.ceemsp.database.type.TipoMovimientoUsuarioEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoOperacionUsuarioEnum;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import com.pelisat.cesp.ceemsp.infrastructure.exception.NotFoundResourceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioMovimientosServiceImpl implements UsuarioMovimientosService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository;
    private final Logger logger = LoggerFactory.getLogger(UsuarioMovimientosService.class);

    @Autowired
    public UsuarioMovimientosServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository,
                                         EmpresaFormaEjecucionRepository empresaFormaEjecucionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.empresaFormaEjecucionRepository = empresaFormaEjecucionRepository;
    }

    @Override
    public List<MovimientoDto> obtenerMovimientos(String usuarioUuid, TipoMovimientoUsuarioEnum tipoMovimiento) {
        if(StringUtils.isBlank(usuarioUuid)) {
            logger.warn("Alguno de los parametros viene invalido");
            throw new InvalidDataException();
        }

        Usuario usuario = usuarioRepository.getUsuarioByUuidAndEliminadoFalse(usuarioUuid);

        if(usuario == null) {
            logger.warn("El usuario no existe en la base de datos");
            throw new NotFoundResourceException();
        }

        List<MovimientoDto> movimientos = null;

        switch(tipoMovimiento) {
            case EMPRESA:
                List<Empresa> empresasCreadasPorUsuario = empresaRepository.getAllByCreadoPor(usuario.getId());
                List<Empresa> empresasActualizadasPorUsuario = empresaRepository.getAllByActualizadoPor(usuario.getId());

                movimientos = empresasCreadasPorUsuario.stream()
                        .map(e -> {
                            MovimientoDto m = new MovimientoDto();
                            m.setId(e.getId());
                            m.setFechaMovimiento(e.getFechaCreacion().toString());
                            m.setMetadatos(new Gson().toJson(e));
                            m.setNombreEnte(e.getRazonSocial());
                            m.setPerteneceA("NA");
                            m.setUuid(e.getUuid());
                            m.setTipoOperacion(TipoOperacionUsuarioEnum.ALTA);
                            return m;
                        })
                        .collect(Collectors.toList());

                        movimientos.addAll(
                                empresasActualizadasPorUsuario.stream()
                                        .map(e -> {
                                            MovimientoDto m = new MovimientoDto();
                                            m.setId(e.getId());
                                            m.setFechaMovimiento(e.getFechaCreacion().toString());
                                            m.setMetadatos(new Gson().toJson(e));
                                            m.setNombreEnte(e.getRazonSocial());
                                            m.setPerteneceA("NA");
                                            m.setUuid(e.getUuid());
                                            m.setTipoOperacion(e.getEliminado() ? TipoOperacionUsuarioEnum.ELIMINACION : TipoOperacionUsuarioEnum.MODIFICACION);
                                            return m;
                                        })
                                        .collect(Collectors.toList())
                        );
                break;
            case EMPRESA_FORMAS_EJECUCION:
                break;
            case ESCRITURAS:
                break;
            case SOCIOS:
                break;
        }

        return movimientos;
    }
}
