package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El recurso se encuentra activo o asignado. Favor de desvincularlo antes de eliminarlo.")
public class AlreadyActiveException extends RuntimeException {
}
