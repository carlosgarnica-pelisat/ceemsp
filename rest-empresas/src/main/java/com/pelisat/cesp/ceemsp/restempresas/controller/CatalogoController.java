package com.pelisat.cesp.ceemsp.restempresas.controller;

import com.pelisat.cesp.ceemsp.database.dto.*;
import com.pelisat.cesp.ceemsp.restempresas.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CatalogoController {
    private static final String CATALOGO_URI = "/catalogos";
    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
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

    @GetMapping(value = CATALOGO_URI + "/domicilios/tipos-infraestructura", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TipoInfraestructuraDto> obtenerTiposInfraestructura() {
        return catalogoService.obtenerTiposInfraestructura();
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


}
