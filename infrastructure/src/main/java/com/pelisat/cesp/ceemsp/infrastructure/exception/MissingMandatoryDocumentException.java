package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Este tipo de motivo de baja requiere un documento fundatorio")
public class MissingMandatoryDocumentException extends RuntimeException {
}
