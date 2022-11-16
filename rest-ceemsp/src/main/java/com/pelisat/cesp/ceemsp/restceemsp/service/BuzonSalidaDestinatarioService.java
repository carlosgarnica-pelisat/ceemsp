package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.BuzonInternoDestinatarioDto;

public interface BuzonSalidaDestinatarioService {
    BuzonInternoDestinatarioDto agregarDestinatario(String buzonInternoUuid, String username, BuzonInternoDestinatarioDto buzonInternoDestinatarioDto);
    BuzonInternoDestinatarioDto modificarDestinatario(String buzonInternoUuid, String destinatarioUuid, String username, BuzonInternoDestinatarioDto buzonInternoDestinatarioDto);
    BuzonInternoDestinatarioDto eliminarDestinatario(String buzonInternoUuid, String destinatarioUuid, String username);
}
