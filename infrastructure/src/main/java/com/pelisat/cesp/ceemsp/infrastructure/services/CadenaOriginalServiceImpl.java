package com.pelisat.cesp.ceemsp.infrastructure.services;

import com.pelisat.cesp.ceemsp.database.dto.EmpresaDto;
import com.pelisat.cesp.ceemsp.database.dto.UsuarioDto;
import com.pelisat.cesp.ceemsp.database.type.TipoCadenaOriginalEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

// LA CADENA ORIGINAL SIEMPRE DEBERA DE CONTENER LOS SIGUIENTES DATOS:
// ||VERSION_CORREO|UUID|TIMESTAMP_GUARDADO_MENSAJE|UUID_USUARIO_QUE_ENVIO|UUID_USUARIO_RECEPTOR|UUID_EMPRESA|TIPO_MENSAJE_ENVIADO(ENUM)|FLEX_FIELDS||
// LOS FLEX_FIELDS SERAN AGREGADOS MAS ADELANTE PERO INCLUIRAN MAYOR INFORMACION DE LO QUE CONSISTE EL MENSAJE PARA AGREGARLE MAS AUTENTICIDAD
// EL SELLO GENERADO DE MOMENTO SERA POR MEDIO DE UN HASH EL CUAL SERA GENERADO EN FUNCION DE LA CADENA ORIGINAL PARA CORROBORAR AUTENTICIDAD
// TODO MENSAJE ENVIADO DEBE DE GUARDARSE DENTRO DE LA BASE DE DATOS PARA CORROBORAR UUID YA QUE ESTE SE GENERA DE MANERA UNICA
@Service
public class CadenaOriginalServiceImpl implements CadenaOriginalService {

    @Override
    public String generarCadenaOriginal(TipoCadenaOriginalEnum tipoCadenaOriginalEnum, UsuarioDto usuarioEmisor, UsuarioDto usuarioReceptor, EmpresaDto empresaDto) {

        String uuid = UUID.randomUUID().toString(); // TODO, guardar esto en la base de datos para garantizar veracidad del correo
        String fechaHoraEnvio = LocalDateTime.now().toString(); // TODO, tomar esto de la base de datos

        return "||" +
                tipoCadenaOriginalEnum.getVersion() + "|" +
                uuid + "|" +
                fechaHoraEnvio + "|" +
                usuarioEmisor.getUuid() + "|" +
                usuarioReceptor.getUuid() + "|" +
                empresaDto.getUuid() + "|" +
                tipoCadenaOriginalEnum.getCodigo() + "||";
    }
}
