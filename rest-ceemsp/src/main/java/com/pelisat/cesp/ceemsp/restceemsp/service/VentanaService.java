package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.VentanaDto;

import java.util.List;

public interface VentanaService {
    List<VentanaDto> obtenerVentanas();
    VentanaDto obtenerVentanaPorUuid(String uuid);
    VentanaDto guardarVentana(VentanaDto ventanaDto, String username);
    VentanaDto modificarVentana(String uuid, VentanaDto ventanaDto, String username);
    VentanaDto eliminarVentana(String uuid, String username);
}
