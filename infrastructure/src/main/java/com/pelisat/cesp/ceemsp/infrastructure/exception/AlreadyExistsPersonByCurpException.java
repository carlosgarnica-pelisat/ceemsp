package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La persona ya se encuentra registrada con este CURP")
public class AlreadyExistsPersonByCurpException extends RuntimeException {
}