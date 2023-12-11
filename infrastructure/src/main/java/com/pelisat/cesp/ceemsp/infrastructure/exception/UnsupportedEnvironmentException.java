package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Este ambiente no esta configurado. Favor de configurarlo adecuadamente")
public class UnsupportedEnvironmentException extends RuntimeException {
}
