package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "La persona no tiene CUIP tramitada. No se puede asignar el arma")
public class MissingCuipException extends RuntimeException {
}
