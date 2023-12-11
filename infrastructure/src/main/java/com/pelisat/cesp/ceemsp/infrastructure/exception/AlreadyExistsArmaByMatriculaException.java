package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El arma ya se encuentra registrada por matricula")
public class AlreadyExistsArmaByMatriculaException extends RuntimeException {
}
