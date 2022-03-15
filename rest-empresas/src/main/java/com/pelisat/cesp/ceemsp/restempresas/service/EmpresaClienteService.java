package com.pelisat.cesp.ceemsp.restempresas.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmpresaClienteService {
    List<ClienteDto> obtenerClientesPorEmpresa(String empresaUuid);
    ClienteDto obtenerClientePorId(Integer id);
    ClienteDto obtenerClientePorUuid(String escrituraUuid, boolean soloEntidad);
    ClienteDto crearCliente(String username, ClienteDto clienteDto, MultipartFile archivo);
    ClienteDto modificarCliente(String clienteUuid, String username, ClienteDto clienteDto);
    ClienteDto eliminarCliente(String clienteUuid, String username);
}
