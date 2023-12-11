package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El usuario (email) ya se encuentra activo o asignado a otra empresa.")
public class AlreadyExistsUserException extends RuntimeException {
}
