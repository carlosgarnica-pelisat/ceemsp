package com.pelisat.cesp.ceemsp.restceemsp.service;

import com.pelisat.cesp.ceemsp.database.dto.ClienteDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface ClienteService {
    List<ClienteDto> obtenerClientesPorEmpresa(String empresaUuid);
    List<ClienteDto> obtenerClientesEliminadosPorEmpresa(String empresaUuid);
    ClienteDto obtenerClientePorId(Integer id);
    ClienteDto obtenerClientePorUuid(String empresaUuid, String escrituraUuid, boolean soloEntidad);
    File obtenerContrato(String empresaUuid, String clienteUuid);
    File descargarDocumentoFundatorio(String uuid, String clienteUuid);
    ClienteDto crearCliente(String empresaUuid, String username, ClienteDto clienteDto, MultipartFile archivo);
    ClienteDto modificarCliente(String empresaUuid, String clienteUuid, String username, ClienteDto clienteDto);
    ClienteDto eliminarCliente(String empresaUuid, String clienteUuid, String username, ClienteDto clienteDto, MultipartFile multipartFile);
}
