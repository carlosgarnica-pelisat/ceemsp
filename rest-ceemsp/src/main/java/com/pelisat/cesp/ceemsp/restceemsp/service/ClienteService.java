package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClienteService {
    List<ClienteDto> obtenerClientesPorEmpresa(String empresaUuid);
    ClienteDto obtenerClientePorId(Integer id);
    ClienteDto obtenerClientePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    ClienteDto crearCliente(String empresaUuid, String username, ClienteDto clienteDto, MultipartFile archivo);
    ClienteDto modificarCliente(String empresaUuid, String clienteUuid, String username, ClienteDto clienteDto);
    ClienteDto eliminarCliente(String empresaUuid, String clienteUuid, String username);
}
