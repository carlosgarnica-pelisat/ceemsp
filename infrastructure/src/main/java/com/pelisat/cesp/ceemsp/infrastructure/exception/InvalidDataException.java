package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Se han enviado valores invalidos al API. Favor de verificarlos")
public class InvalidDataException extends RuntimeException {
}
