package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El numero de registro de la empresa con la cual se esta registrando ya existe")
public class DuplicatedEnterpriseException extends RuntimeException{
}
