package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.database.model.VehiculoTipo;
import com.pelisat.cesp.ceemsp.database.type.VehiculoTipoEnum;
import com.pelisat.cesp.ceemsp.restempresas.service.CatalogoService;
import com.pelisat.cesp.ceemsp.restempresas.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CatalogoController {
    private static final String CATALOGO_URI = "/catalogos";
    private final JwtUtils jwtUtils;
    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoController(CatalogoService catalogoService, JwtUtils jwtUtils) {
        this.catalogoService = catalogoService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = CATALOGO_URI + "/canes/razas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanRazaDto> obtenerCanesRazas() {
        return catalogoService.obtenerCanesRazas();
    }

    @GetMapping(value = CATALOGO_URI + "/canes/adiestramientos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CanTipoAdiestramientoDto> obtenerCanesAdiestramientos() {
        return catalogoService.obtenerCanesAdiestramientos();
    }

    @GetMapping(value = CATALOGO_URI + "/armas/clases", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaClaseDto> obtenerArmasClases() {
        return catalogoService.obtenerClasesArmas();
    }

    @GetMapping(value = CATALOGO_URI + "/armas/marcas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaMarcaDto> obtenerArmasMarcas() {
        return catalogoService.obtenerArmasMarcas();
    }

    @GetMapping(value = CATALOGO_URI + "/armas/tipos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ArmaTipoDto> obtenerArmasTipos() {
        return catalogoService.obtenerArmasTipos();
    }

    @GetMapping(value = CATALOGO_URI + "/personal/nacionalidades", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalNacionalidadDto> obtenerNacionalidades() {
        return catalogoService.obtenerNacionalidades();
    }

    @GetMapping(value = CATALOGO_URI + "/personal/puestos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalPuestoDeTrabajoDto> obtenerPuestosTrabajo() {
        return catalogoService.obtenerPuestosDeTrabajo();
    }

    @GetMapping(value = CATALOGO_URI + "/personal/puestos/{uuid}/subpuestos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonalSubpuestoDeTrabajoDto> obtenerSubpuestosTrabajo(
            @PathVariable(value = "uuid") String uuid
    ) {
        return catalogoService.obtenerSubpuestosPorUuid(uuid);
    }

    @GetMapping(value = CATALOGO_URI + "/tipos-infraestructura", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TipoInfraestructuraDto> obtenerTiposInfraestructura() {
        return catalogoService.obtenerTiposInfraestructura();
    }

    @GetMapping(value = CATALOGO_URI + "/equipos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EquipoDto> obtenerEquipos() {
        return catalogoService.obtenerEquipos();
    }

    @GetMapping(value = CATALOGO_URI + "/equipos/empresas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EquipoDto> obtenerEquiposCalificablesParaEmpresa(
            HttpServletRequest request
    ) throws Exception {
        String username = jwtUtils.getUserFromToken(request.getHeader("Authorization"));
        return catalogoService.obtenerEquiposCalificables(username);
    }

    @GetMapping(value = CATALOGO_URI + "/uniformes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UniformeDto> obtenerUniformes() {
        return catalogoService.obtenerUniformes();
    }

    @GetMapping(value = CATALOGO_URI + "/vehiculos/usos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoUsoDto> obtenerUsosVehiculo() {
        return catalogoService.obtenerUsosVehiculos();
    }

    @GetMapping(value = CATALOGO_URI + "/vehiculos/marcas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoMarcaDto> obtenerMarcasVehiculo() {
        return catalogoService.obtenerMarcasVehiculos();
    }

    @GetMapping(value = CATALOGO_URI + "/vehiculos/marcas/tipos/{tipo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoMarcaDto> obtenerMarcasVehiculo(
            @PathVariable(value = "tipo") VehiculoTipoEnum tipo
    ) {
        return catalogoService.obtenerMarcasVehiculosTipo(tipo);
    }

    @GetMapping(value = CATALOGO_URI + "/vehiculos/marcas/{marcaUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VehiculoMarcaDto obtenerMarcaVehiculoPorUuid(@PathVariable(value = "marcaUuid") String marcaUuid) {
        return catalogoService.obtenerMarcaVehiculoPorUuid(marcaUuid);
    }

    @GetMapping(value = CATALOGO_URI + "/vehiculos/tipos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VehiculoTipoDto> obtenerTiposVehiculo() {
        return catalogoService.obtenerTiposVehiculo();
    }

    @GetMapping(value = CATALOGO_URI + "/estados", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EstadoDto> obtenerEstados() {
        return catalogoService.obtenerTodosLosEstados();
    }

    @GetMapping(value = CATALOGO_URI + "/estados/{estadoUuid}/municipios")
    public List<MunicipioDto> obtenerMunicipiosPorEstadoUuid(
            @PathVariable(value = "estadoUuid") String estadoUuid
    ) {
        return catalogoService.obtenerMunicipiosPorEstadoUuid(estadoUuid);
    }

    @GetMapping(value = CATALOGO_URI + "/estados/{estadoUuid}/municipios/{municipioUuid}/localidades")
    public List<LocalidadDto> obtenerLocalidadesPorMunicipioYEstadoUuid(
            @PathVariable(value = "estadoUuid") String estadoUuid,
            @PathVariable(value = "municipioUuid") String municipioUuid
    ) {
        return catalogoService.obtenerLocalidadesPorEstadoUuidYMunicipioUuid(estadoUuid, municipioUuid);
    }

    @GetMapping(value = CATALOGO_URI + "/estados/{estadoUuid}/municipios/{municipioUuid}/colonias")
    public List<ColoniaDto> obtenerColoniasPorMunicipioYEstadoUuid(
            @PathVariable(value = "estadoUuid") String estadoUuid,
            @PathVariable(value = "municipioUuid") String municipioUuid
    ) {
        return catalogoService.obtenerColoniasPorEstadoUuidYMunicipioUuid(estadoUuid, municipioUuid);
    }

    @GetMapping(value = CATALOGO_URI + "/calles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CalleDto> obtenerCalles(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        if(StringUtils.isBlank(query)) {
            return catalogoService.obtenerCalles(limit);
        } else {
            return catalogoService.obtenerCallesPorQuery(query);
        }
    }
}
