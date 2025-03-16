package com.pelisat.cesp.ceemsp.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La ventana para reportes mensuales todavia se encuentra activa.")
public class ReportAlreadyPresentedException extends RuntimeException {
}
