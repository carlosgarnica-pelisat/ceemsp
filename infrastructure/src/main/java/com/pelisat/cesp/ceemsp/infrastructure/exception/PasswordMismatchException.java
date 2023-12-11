package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La contrasena anterior es incorrecta o no coincide.")
public class PasswordMismatchException extends RuntimeException {
}
