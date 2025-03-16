package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.type.TipoCadenaOriginalEnum;

public interface CadenaOriginalService {
    String generarCadenaOriginal(TipoCadenaOriginalEnum tipoCadenaOriginalEnum, UsuarioDto usuarioEmisor, UsuarioDto usuarioReceptor, EmpresaDto empresaDto);
}
